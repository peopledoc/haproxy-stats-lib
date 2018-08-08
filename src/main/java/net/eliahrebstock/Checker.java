package net.eliahrebstock;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fr.novapost.lib.yaml.exception.YamlParseException;
import net.eliahrebstock.config.Config;
import net.eliahrebstock.config.LoadBalancerConfig;
import net.eliahrebstock.results.BackendResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class Checker implements Runnable {
    private Config config;

    private Logger logger = LoggerFactory.getLogger(Checker.class);

    private Map<String, List<BackendResult>> results;

    private Cache<UUID, Map<String, List<BackendResult>>> cache;

    private UUID key;

    public Checker(Config config) {
        this.config = config;
        key = UUID.randomUUID();
        cache = CacheBuilder.newBuilder().expireAfterWrite(config.getCachePeriod(), TimeUnit.SECONDS).build();
    }

    private static Iterable<CSVRecord> getRecords(InputStream is) throws IOException {
        long test = is.skip(2); // skip first #
        if (test < 2) {
            throw new IllegalArgumentException();
        }

        InputStreamReader in = new InputStreamReader(is);
        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
    }

    private Map<String, List<BackendResult>> doCheck() throws IOException {
        List<String> proxies = Arrays.asList(config.getProxies());

        Map<String, List<BackendResult>> resultsMap = new HashMap<>();

        for (LoadBalancerConfig lbConfig : config.getLoadBalancerConfigs()) {
            StatsFetcher statsFetcher = new StatsFetcher(lbConfig);
            InputStream is = statsFetcher.fetch();
            Iterable<CSVRecord> records = getRecords(is);
            List<BackendResult> resultList = new ArrayList<>();
            for (CSVRecord record : records) {
                HAProxyRecord lbRecord = new HAProxyRecord(record);
                if (lbRecord.getType() == HAProxyRecord.ProxyType.SERVER && proxies.contains(lbRecord.getProxyName())) {
                    HAProxyRecord.HCStatus hcStatus = lbRecord.getCheckStatus();
                    boolean status = false;
                    String statusString = "";
                    if (hcStatus != null) {
                        statusString = hcStatus.toString();
                        status = hcStatus.getStatus();
                    }

                    BackendResult result = new BackendResult(lbRecord.getProxyName(), lbRecord.getServiceName(),
                            lbRecord.getWeight(), status, statusString);
                    resultList.add(result);
                }
            }
            resultsMap.put(lbConfig.getEnvName(), resultList);
        }
        this.results = resultsMap;
        return resultsMap;
    }


    public Map<String, List<BackendResult>> check() {
        try {
            return cache.get(key, this::doCheck);
        } catch (ExecutionException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    public static void main(String[] args) throws IOException, YamlParseException {
        Config mainConfig = Config.loadFromFile(new File("config.yml"));
        Checker checker = new Checker(mainConfig);

        Map<String, List<BackendResult>> results = checker.check();

        ObjectMapper mapper = new ObjectMapper();
        try (JsonGenerator generator = mapper.getFactory().createGenerator(System.out)) {
            generator.useDefaultPrettyPrinter().writeObject(results);
        }
    }

    public Map<String, List<BackendResult>> getLastResults() {
        return results;
    }

    @Override
    public void run() {
        check();
    }
}
