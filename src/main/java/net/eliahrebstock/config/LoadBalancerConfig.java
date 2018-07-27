package net.eliahrebstock.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoadBalancerConfig {

    @JsonProperty("name")
    private String envName;

    @JsonProperty
    private String fqdn;

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;


    public String getEnvName() {
        return envName;
    }

    public String getFqdn() {
        return fqdn;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
