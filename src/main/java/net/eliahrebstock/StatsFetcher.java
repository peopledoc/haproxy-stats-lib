package net.eliahrebstock;

import net.eliahrebstock.config.LoadBalancerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

/**
 * Utility class to fetch stats from a HAProxy loadbalancer
 * given the url to access it. HAProxy must be configured to have an accessible
 * stats page.
 */
class StatsFetcher {

    private static final Logger logger = LoggerFactory.getLogger(StatsFetcher.class);

    private URL statsURL;

    private String username;

    private String password;

    StatsFetcher(LoadBalancerConfig lbConfig) throws MalformedURLException {
        this(lbConfig.getUrl(), lbConfig.getUsername(), lbConfig.getPassword());
    }

    private StatsFetcher(String baseURL, String username, String password) throws MalformedURLException {
        statsURL = new URL(baseURL + "/;csv");
        this.username = username;
        this.password = password;
    }

    InputStream fetch() throws IOException {
        HttpURLConnection con = (HttpURLConnection) statsURL.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        String userPassword = username + ":" + password;
        byte[] encoded = Base64.getEncoder().encode(userPassword.getBytes());
        String encodedString = new String(encoded);
        con.setRequestProperty("Authorization", "Basic " + encodedString);
        logger.debug("HTTP request on {}.", statsURL);
        int responseCode = con.getResponseCode();
        logger.debug("HTTP response from {} : {}.", statsURL, responseCode);
        if (responseCode == 200) {
            return con.getInputStream();
        } else {
            con.disconnect();
            throw new IOException(String.format("Response code %d for %s", responseCode, statsURL.toString()));
        }
    }
}
