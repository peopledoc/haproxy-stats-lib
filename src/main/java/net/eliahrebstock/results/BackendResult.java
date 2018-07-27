package net.eliahrebstock.results;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result generated for each backend on HAProxy
 */
public class BackendResult {

    @JsonProperty
    private String name;

    @JsonProperty
    private String backend;

    @JsonProperty
    private int weight;

    @JsonProperty
    private Boolean status;

    @JsonProperty
    private String HCstatus;

    public BackendResult(String name, String backend, int weight, Boolean status, String HCstatus) {
        this.name = name;
        this.backend = backend;
        this.weight = weight;
        this.status = status;
        this.HCstatus = HCstatus;
    }
}
