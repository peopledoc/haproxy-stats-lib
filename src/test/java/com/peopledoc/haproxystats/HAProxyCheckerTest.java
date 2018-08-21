package com.peopledoc.haproxystats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peopledoc.haproxystats.config.Config;
import com.peopledoc.haproxystats.results.ProxyResult;
import fr.novapost.lib.test.ResourceLoader;
import fr.novapost.lib.yaml.exception.YamlParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for the HAProxyChecker. These tests need the main_config.yml to be correct
 * and to have a proper connection to the load balancer.
 */
class HAProxyCheckerTest {
    @Test
    void testMain() {
        Config mainConfig = null;
        try {
            mainConfig = Config.loadFromFile(ResourceLoader.getFile("main_config.yml"));
        } catch (YamlParseException e) {
            e.printStackTrace();
            fail();
        }

        HAProxyChecker haProxyChecker = new HAProxyChecker(mainConfig);

        Optional<Map<String, List<ProxyResult>>> results = haProxyChecker.check();
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (results.isPresent()) {
                mapper.getFactory().createGenerator(System.out).useDefaultPrettyPrinter().writeObject(results.get());
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testRun() {
        Config mainConfig = null;
        try {
            mainConfig = Config.loadFromFile(ResourceLoader.getFile("main_config.yml"));
        } catch (YamlParseException e) {
            e.printStackTrace();
            fail();
        }

        HAProxyChecker haProxyChecker = new HAProxyChecker(mainConfig);
        Thread t = new Thread(haProxyChecker);
        t.run();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        if (!haProxyChecker.getLastResults().isPresent()) {
            fail();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            Optional<Map<String, List<ProxyResult>>> results = haProxyChecker.getLastResults();
            if (results.isPresent()) {
                mapper.getFactory().createGenerator(System.out).useDefaultPrettyPrinter().writeObject(results);
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void emptyProxiesMainTest() {
        Config emptyMainConfig = null;
        try {
            emptyMainConfig = Config.loadFromFile(ResourceLoader.getFile("empty_main_config.yml"));
        } catch (YamlParseException e) {
            e.printStackTrace();
            fail();
        }

        HAProxyChecker haProxyChecker = new HAProxyChecker(emptyMainConfig);

        Optional<Map<String, List<ProxyResult>>> results = haProxyChecker.check();
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (results.isPresent()) {
                mapper.getFactory().createGenerator(System.out).useDefaultPrettyPrinter().writeObject(results.get());
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
