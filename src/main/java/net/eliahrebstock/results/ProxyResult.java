package net.eliahrebstock.results;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.eliahrebstock.HAProxyRecord;

import java.util.ArrayList;
import java.util.List;

public class ProxyResult {
    /**
     * Name of a proxy (which contains multiple backends).
     */
    @JsonProperty
    private String name;

    /**
     * List of backends result for that proxy
     */
    @JsonProperty
    private List<BackendResult> backends;

    /**
     * Loadbalancer associated with the proxy
     */
    @JsonProperty
    private String loadbalancer;

    public ProxyResult(String loadbalancer, String proxyName, List<HAProxyRecord> backendsRecords) {
        name = proxyName;
        this.loadbalancer = loadbalancer;
        backends = new ArrayList<>();
        for (HAProxyRecord record : backendsRecords) {
            backends.add(new BackendResult(record));
        }
    }
}
