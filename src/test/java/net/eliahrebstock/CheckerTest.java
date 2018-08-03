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
 * Unit test for the Checker.
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

        Map<String, List<BackendResult>> results = null;
        try {
            results = checker.check();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.getFactory().createGenerator(System.out).useDefaultPrettyPrinter().writeObject(results);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
