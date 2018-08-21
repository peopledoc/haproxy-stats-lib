package com.peopledoc.haproxystats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.peopledoc.haproxystats.config.LoadBalancerConfig;
import fr.novapost.lib.yaml.exception.YamlParseException;
import com.peopledoc.haproxystats.config.Config;
import com.peopledoc.haproxystats.results.ProxyResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Checker used to poll statistics from HAProxy and converting part of it to JSON
 */
public class HAProxyChecker implements Runnable {
    private final Config config;

    private static final Logger logger = LoggerFactory.getLogger(HAProxyChecker.class);

    private Map<String, List<ProxyResult>> results;

    private final Cache<UUID, Map<String, List<ProxyResult>>> cache;

    private final UUID key;

    /**
     * Construct a new HAProxyChecker with the given configuration file.
     * @param configFile File Main configuration file
     * @throws YamlParseException if the config file does not follow the format
     */
    public HAProxyChecker(File configFile) throws YamlParseException {
        this(Config.loadFromFile(configFile));
    }

    /**
     * Construct a new HAProxyChecker with the given configuration.
     * @param config Config Main configuration
     */
    public HAProxyChecker(Config config) {
        this.config = config;
        key = UUID.randomUUID();
        cache = CacheBuilder.newBuilder().expireAfterWrite(config.getCachePeriod(), TimeUnit.SECONDS).build();
    }

    /**
     * Parse stats CSV results from HAProxy
     * @param is InputStream of the stats CSV
     * @return Iterable of CSVRecord
     * @throws IOException if the InputStream is empty.
     */
    private static Iterable<CSVRecord> getRecords(InputStream is) throws IOException {
        long test = is.skip(2); // skip first #
        if (test < 2) {
            throw new IllegalArgumentException();
        }

        InputStreamReader in = new InputStreamReader(is);
        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
    }

    private Map<String, List<HAProxyRecord>> aggregateRecordsByProxyName(Iterable<CSVRecord> records, List<String> filteredProxies) {
        Map<String, List<HAProxyRecord>> proxyAggregate = new HashMap<>();
        // We aggregate results by proxy name
        for (CSVRecord record : records) {
            HAProxyRecord lbRecord = new HAProxyRecord(record);
            if (lbRecord.getType() == HAProxyRecord.ProxyType.SERVER && (filteredProxies.isEmpty() || filteredProxies.contains(lbRecord.getProxyName()))) {
                if (!proxyAggregate.containsKey(lbRecord.getProxyName())) {
                    proxyAggregate.put(lbRecord.getProxyName(), new ArrayList<>());
                }
                proxyAggregate.get(lbRecord.getProxyName()).add(lbRecord);
            }
        }
        return proxyAggregate;
    }

    private List<String> getFilteredProxies() {
        List<String> proxies;
        if (config.getProxies() == null) {
            proxies = new ArrayList<>();
        } else {
            proxies = Arrays.asList(config.getProxies());
        }
        return proxies;
    }

    /**
     * Check HAProxy stats and return a Map of results.
     * @return Map<String, List<BackendResult>> Map between environnment names and List of backends results.
     * @throws IOException if a connexion problem occurs.
     */
    private Map<String, List<ProxyResult>> doCheck() throws IOException {
        logger.info("Check for loadbalancers stats triggered.");

        List<String> proxies = getFilteredProxies();
        Map<String, List<ProxyResult>> resultsMap = new HashMap<>();
        List<ProxyResult> resultList = new ArrayList<>();

        for (LoadBalancerConfig lbConfig : config.getLoadBalancerConfigs()) {
            logger.info("Check on {} with {} started.", lbConfig.getEnvName(), lbConfig.getUrl());
            StatsFetcher statsFetcher = new StatsFetcher(lbConfig);
            InputStream is = statsFetcher.fetch();
            Iterable<CSVRecord> records = getRecords(is);

            Map<String, List<HAProxyRecord>> proxyAggregate = aggregateRecordsByProxyName(records, proxies);

            for (Map.Entry<String, List<HAProxyRecord>> entry : proxyAggregate.entrySet()) {
                resultList.add(new ProxyResult(lbConfig.getEnvName(), entry.getKey(), entry.getValue()));
            }
        }
        resultsMap.put(config.getEnvironment(), resultList);
        this.results = resultsMap;
        return resultsMap;
    }

    /**
     * Return cached results from HAProxy or trigger a new check if the cache is expired.
     * @return Map<String, List<BackendResult>> the results
     */
    @Nullable
    public Map<String, List<ProxyResult>> check() {
        try {
            return cache.get(key, this::doCheck);
        } catch (ExecutionException e) {
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * Main debug function. Log JSON results from given file on command line.
     * @param args String[] command line options. This function assume the first argument is the path to a config file.
     * @throws YamlParseException if the config file doesn't follow the YAML syntax of {@link Config} class.
     */
    public static void main(String[] args) throws YamlParseException {
        if (args.length < 1) {
            logger.error("Need a configuration file path.");
            return;
        }

        HAProxyChecker haProxyChecker = new HAProxyChecker(new File(args[0]));

        haProxyChecker.check();
        logger.info(haProxyChecker.getLastResultsAsJSON());
    }

    /**
     * Return last fetched results.
     * Does not trigger a new fetch.
     * @return Map<String, List<BackendResult>> results
     */
    public Map<String, List<ProxyResult>> getLastResults() {
        check();
        return results;
    }

    /**
     * Return last fetched results as a JSON String.
     * Does not trigger a new fetch.
     * @return String JSON results.
     */
    @Nullable
    public String getLastResultsAsJSON() {
        check();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(results);
        } catch (JsonProcessingException e) {
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * Run a new Check as a thread.
     */
    @Override
    public void run() {
        check();
    }
}
