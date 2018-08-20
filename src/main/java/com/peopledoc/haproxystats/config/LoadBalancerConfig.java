package com.peopledoc.haproxystats.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO representing part of config.yml dedicated to loadbalancer
 */
public class LoadBalancerConfig {

    /**
     * Name of the environnment (prod, staging...)
     */
    @JsonProperty("name")
    private String envName;

    /**
     * Url of the HAProxy stats page, like "http://haproxy.example.com:1916/stats"
     */
    @JsonProperty
    private String url;

    /**
     * Username for HTTP authentification
     */
    @JsonProperty
    private String username;

    /**
     * Password for HTTP authentification
     */
    @JsonProperty
    private String password;


    public String getEnvName() {
        return envName;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
