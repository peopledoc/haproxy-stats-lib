package com.peopledoc.haproxystats.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class Fetcher {
    private static final Logger logger = LoggerFactory.getLogger(Fetcher.class);

    protected HttpURLConnection createGETConnection(URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        return con;
    }

    protected InputStream getInputStreamFromConnection(HttpURLConnection connection) throws IOException {
        logger.debug("HTTP request on {}.", connection.getURL());
        int responseCode = connection.getResponseCode();
        logger.debug("HTTP response from {} : {}.", connection.getURL(), responseCode);
        if (responseCode == 200) {
            return connection.getInputStream();
        } else {
            connection.disconnect();
            throw new IOException(String.format("Response code %d for %s", responseCode, connection.toString()));
        }
    }

    protected String inputStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }
}
