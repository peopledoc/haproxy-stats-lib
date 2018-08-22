package com.peopledoc.statsretriever;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StatsRetriever {

    private static final Logger logger = LoggerFactory.getLogger(JSONStatsFetcher.class);

    private List<JSONStatsFetcher> fetchers;

    private static final Integer THREAD_COUNT = 20;

    private final ExecutorService pool;

    public StatsRetriever(String configFilePath) {
        this(new File(configFilePath));
    }

    /**
     * Construct a new StatsRetriever from a File
     *
     * @param configFile File containing a YAML map of environment -> URL
     */
    public StatsRetriever(File configFile) {
        Map<String, String> config = parseConfig(configFile);
        fetchers = new ArrayList<>();
        for (String url : config.values()) {
            try {
                fetchers.add(new JSONStatsFetcher(url));
            } catch (MalformedURLException e) {
                logger.error("Malformed URL \"{}\" in configuration file {} : {}", url, configFile.getPath(), e.getLocalizedMessage());
            }
        }
        pool = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    /**
     * Parse the configuration file
     *
     * @param configFile File to parse
     * @return Map between environment and URL to fetch
     */
    private Map<String, String> parseConfig(File configFile) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(configFile, Map.class);
        } catch (IOException e) {
            logger.error("Error when opening configuration file {} : {}", configFile.getPath(), e.getLocalizedMessage());
            return new HashMap<>();
        }
    }

    /**
     * Parse the result String as JSON
     *
     * @param result String
     * @return Map of the results
     * @throws IOException if there are issues during the parsing
     */
    private Map parseResult(String result) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result, Map.class);
    }

    /**
     * Check the fetchers for results
     *
     * @return Optional<List   <   Map>> List of the results
     */
    public Optional<List<Map>> check() {
        List<Future<String>> futures;
        List<Map> results = new ArrayList<>();

        // Invoke all fetchers in the thread pool
        try {
            futures = pool.invokeAll(fetchers);
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
            return Optional.empty();
        }

        // Then retrieve the results as they come
        List<String> futuresResults = new ArrayList<>();
        for (Future<String> f : futures) {
            try {
                futuresResults.add(f.get());
            } catch (InterruptedException e) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
                return Optional.empty();
            } catch (ExecutionException ignored) {
                logger.info("Some stats results were not retrieved.");
            }
        }

        for (String result : futuresResults) {
            Map mapResult;
            try {
                mapResult = parseResult(result);
            } catch (IOException e) {
                logger.error("Error when parsing a JSON result : {}", e.getLocalizedMessage());
                continue;
            }
            results.add(mapResult);
        }
        return Optional.of(results);
    }

    /**
     * Check for results then return a JSON string from it
     *
     * @return String of the JSON result
     */
    public String checkAsJSON() {
        Optional<List<Map>> results = check();
        if (results.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(results.get());
            } catch (JsonProcessingException e) {
                logger.error("Error when processing output JSON : {}", e.getLocalizedMessage());
                return "{}";
            }
        } else {
            return "{}";
        }
    }
}
