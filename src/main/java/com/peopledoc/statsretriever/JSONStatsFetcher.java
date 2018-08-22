package com.peopledoc.statsretriever;

import com.peopledoc.haproxystats.utils.Fetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

public class JSONStatsFetcher extends Fetcher implements Callable<String> {

    private static final Logger logger = LoggerFactory.getLogger(JSONStatsFetcher.class);

    /**
     * URL where to retrieve the JSON stats
     */
    private final URL jsonStatsURL;

    public JSONStatsFetcher(String jsonStatsURL) throws MalformedURLException {
        this(new URL(jsonStatsURL));
    }

    public JSONStatsFetcher(URL jsonStatsURL) {
        this.jsonStatsURL = jsonStatsURL;
    }

    /**
     * Fetch the result
     * @return InputStream of the content
     * @throws IOException if the connection can't be made
     */
    private InputStream fetch() throws IOException {
        HttpURLConnection con = createGETConnection(jsonStatsURL);
        return getInputStreamFromConnection(con);
    }

    /**
     * Fetch content and return it as a string
     * @return String result
     * @throws IOException when the fetching failed
     */
    @Override
    public String call() throws IOException {
        try {
            InputStream is = fetch();
            return inputStreamToString(is);
        } catch (IOException e) {
            logger.error("Error when fetching {}: {}", jsonStatsURL, e.getLocalizedMessage());
            throw e;
        }
    }
}
