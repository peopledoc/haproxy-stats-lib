package com.peopledoc.statsretriever;

import fr.novapost.lib.test.ResourceLoader;
import org.junit.jupiter.api.Test;

public class StatsRetrieverTest {
    @Test
    void integrationTest() {
        StatsRetriever retriever = new StatsRetriever(ResourceLoader.getFile("stats_json_config.yml"));
        System.out.println(retriever.checkAsJSON());
    }
}
