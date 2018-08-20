package com.peopledoc.haproxystats.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.novapost.lib.yaml.Yaml;
import fr.novapost.lib.yaml.exception.YamlParseException;

import java.io.File;
import java.util.List;

/**
 * POJO representing config.yml file
 */
public class Config {

    /**
     * List of configurations of HAProxy instances.
     */
    @JsonProperty("loadbalancers")
    private
    List<LoadBalancerConfig> loadBalancerConfigs;

    /**
     * List of the backend names to check (as configured in HAProxy).
     */
    @JsonProperty
    private String[] proxies;

    /**
     * Environment (root of the result)
     */
    @JsonProperty
    private String environment;

    /**
     * Duration of the cache validity in seconds.
     */
    @JsonProperty("cache_period")
    private int cachePeriod = 60;

    /**
     * Parse Yaml file and return a new Config instance.
     * @param config File configuration to parse
     * @return Config the parsed Configuration object
     * @throws YamlParseException if the File does not follow the Config class.
     */
    public static Config loadFromFile(File config) throws YamlParseException {
        return Yaml.parse(config, Config.class);
    }

    public List<LoadBalancerConfig> getLoadBalancerConfigs() {
        return loadBalancerConfigs;
    }

    public String[] getProxies() {
        return proxies;
    }

    public int getCachePeriod() {
        return cachePeriod;
    }

    public String getEnvironment() {
        return environment;
    }
}
