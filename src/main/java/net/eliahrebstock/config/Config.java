package net.eliahrebstock.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.novapost.lib.yaml.Yaml;
import fr.novapost.lib.yaml.exception.YamlParseException;

import java.io.File;
import java.util.List;

public class Config {

    @JsonProperty("loadbalancers")
    private
    List<LoadBalancerConfig> loadBalancerConfigs;

    @JsonProperty
    private String[] proxies;

    @JsonProperty("cache_period")
    private int cachePeriod = 60;

    public static Config loadFromFile(File config) throws YamlParseException {
        return Yaml.parse(config, Config.class);
    }

    public List<LoadBalancerConfig> getLoadBalancerConfigs() {
        return loadBalancerConfigs;
    }

    public String[] getProxies() {
        return proxies;
    }
}
