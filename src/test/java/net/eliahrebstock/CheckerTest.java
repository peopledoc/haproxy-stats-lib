package net.eliahrebstock;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.novapost.lib.test.ResourceLoader;
import fr.novapost.lib.yaml.exception.YamlParseException;
import net.eliahrebstock.config.Config;
import net.eliahrebstock.results.BackendResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for the Checker. These tests need the main_config.yml to be correct
 * and to have a proper connection to the load balancer.
 */
class CheckerTest {
    @Test
    void testMain() {
        Config mainConfig = null;
        try {
            mainConfig = Config.loadFromFile(ResourceLoader.getFile("main_config.yml"));
        } catch (YamlParseException e) {
            e.printStackTrace();
            fail();
        }

        Checker checker = new Checker(mainConfig);

        Map<String, List<BackendResult>> results = checker.check();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.getFactory().createGenerator(System.out).useDefaultPrettyPrinter().writeObject(results);
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

        Checker checker = new Checker(mainConfig);
        Thread t = new Thread(checker);
        t.run();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        if (checker.getLastResults() == null) {
            fail();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.getFactory().createGenerator(System.out).useDefaultPrettyPrinter().writeObject(checker.getLastResults());
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

        Checker checker = new Checker(emptyMainConfig);

        Map<String, List<BackendResult>> results = checker.check();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.getFactory().createGenerator(System.out).useDefaultPrettyPrinter().writeObject(results);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
