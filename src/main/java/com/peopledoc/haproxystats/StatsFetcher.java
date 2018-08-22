package com.peopledoc.haproxystats;

import com.peopledoc.haproxystats.config.LoadBalancerConfig;
import com.peopledoc.haproxystats.utils.Fetcher;

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
class StatsFetcher extends Fetcher {

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

    private void addAuthorization(HttpURLConnection con, String username, String password) {
        String userPassword = username + ":" + password;
        byte[] encoded = Base64.getEncoder().encode(userPassword.getBytes());
        String encodedString = new String(encoded);
        con.setRequestProperty("Authorization", "Basic " + encodedString);
    }

    InputStream fetch() throws IOException {
        HttpURLConnection con = createGETConnection(statsURL);
        addAuthorization(con, username, password);
        return getInputStreamFromConnection(con);
    }
}
