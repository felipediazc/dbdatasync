package com.fdcapps.dbdatasync.input;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class.getName());

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getStringFromInputStream(InputStream in) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                text.append(strLine);
            }
            in.close();
        } catch (Exception e) {
            log.error("getStringFromInputStream : {}", e.toString());
        }
        return text.toString();
    }

}
