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

    public ProxyResult(HAProxyRecord lbRecord, List<HAProxyRecord> backendsRecords) {
        name = lbRecord.getProxyName();
        backends = new ArrayList<>();
        for (HAProxyRecord record : backendsRecords) {
            backends.add(new BackendResult(record));
        }
    }
}
