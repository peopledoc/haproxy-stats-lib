package net.eliahrebstock.results;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LoadBalancerResult {
    @JsonProperty
    List<BackendResult> proxies;
}
