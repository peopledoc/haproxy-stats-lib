package net.eliahrebstock;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.novapost.lib.yaml.exception.YamlParseException;
import net.eliahrebstock.config.Config;
import net.eliahrebstock.config.LoadBalancerConfig;
import net.eliahrebstock.results.BackendResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

// 1. Read configuration
// 2. Main loop
// 3. Fetch data
// 4. Interpret it
// 5. Send back result as JSON

/**
 * Checker used to poll statistics from HAProxy and converting part of it to JSON
 */
public class Checker implements Runnable
{
    private Config config;

    private Map<String, List<BackendResult>> results;

    public Checker(Config config) {
        this.config = config;
    }

    private static Iterable<CSVRecord> getRecords(InputStream is) throws IOException {
        long test = is.skip(2); // skip first #
        if (test < 2) {
            throw new IOException();
        }

        InputStreamReader in = new InputStreamReader(is);
        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
    }

    public Map<String, List<BackendResult>> check() throws IOException {
        List<String> proxies = Arrays.asList(config.getProxies());

        Map<String, List<BackendResult>> results = new HashMap<>();

        for(LoadBalancerConfig lbConfig : config.getLoadBalancerConfigs()) {
            StatsFetcher statsFetcher = new StatsFetcher(lbConfig);
            InputStream is = statsFetcher.fetch();
            Iterable<CSVRecord> records = getRecords(is);
            List<BackendResult> resultList = new ArrayList<>();
            for(CSVRecord record : records) {
                HAProxyRecord HARec = new HAProxyRecord(record);
                if (HARec.getType() == HAProxyRecord.ProxyType.SERVER && proxies.contains(HARec.getProxyName())) {
                    HAProxyRecord.HCStatus hcStatus = HARec.getCheckStatus();
                    boolean status = false;
                    String StatusString = "";
                    if (hcStatus != null) {
                        StatusString = hcStatus.toString();
                        status = hcStatus.getStatus();
                    }

                    BackendResult result = new BackendResult(HARec.getProxyName(), HARec.getServiceName(),
                            HARec.getWeight(), status, StatusString);
                    resultList.add(result);
                }
            }
            results.put(lbConfig.getEnvName(), resultList);
        }
        this.results = results;
        return results;
    }

    public static void main( String[] args ) throws IOException, YamlParseException {
        Config mainConfig = Config.loadFromFile(new File("config.yml"));
        Checker checker = new Checker(mainConfig);

        Map<String, List<BackendResult>> results = checker.check();

        ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().createGenerator(System.out).useDefaultPrettyPrinter().writeObject(results);
    }

    public Map<String, List<BackendResult>> getResults() {
        return results;
    }

    @Override
    public void run() {
        try {
            check();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
