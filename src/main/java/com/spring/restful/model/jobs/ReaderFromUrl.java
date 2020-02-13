package com.spring.restful.model.jobs;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ReaderFromUrl {

    private static final Logger logger = Logger.getLogger(ReaderFromUrl.class);

    public static String readContentFromUrl(String url) throws IOException {


             InputStream is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            return readAll(rd);


    }

    private static String readAll(Reader rd)  throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();

    }

}
