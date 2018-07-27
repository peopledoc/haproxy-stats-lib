package net.eliahrebstock.config;

import fr.novapost.lib.test.ResourceLoader;
import fr.novapost.lib.yaml.exception.YamlParseException;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test on loading configuration file.
 */
class ConfigTest {

    @Test
    void parseTest() {
        File configFile = ResourceLoader.getFile("config.yml");
        Config config = null;
        try {
            config = Config.loadFromFile(configFile);
        } catch (YamlParseException e) {
            e.printStackTrace();
            fail();
        }

        String[] proxies = {"app1", "app2"};
        assertArrayEquals(proxies, config.getProxies());
    }

    @Test
    void invalidParseTest() {
        File configFile = ResourceLoader.getFile("invalid_config.yml");
        assertThrows(YamlParseException.class, () -> Config.loadFromFile(configFile));
    }
}
