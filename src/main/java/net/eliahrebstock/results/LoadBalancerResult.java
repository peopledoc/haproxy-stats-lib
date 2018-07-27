package net.eliahrebstock.results;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Global result for a loadbalancer. Not used yet.
 */
public class LoadBalancerResult {
    @JsonProperty
    List<BackendResult> proxies;
}
