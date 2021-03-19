package com.fdcapps.dbdatasync.input;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

public class Utils {

    private static final Logger log = Logger.getLogger(Utils.class.getName());

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getStringFromInputStream(InputStream in) {
        StringBuilder texto = new StringBuilder("");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                texto.append(strLine);
            }
            in.close();
        } catch (Exception e) {
            log.error("getStringFromInputStream : " + e);
        }
        return texto.toString();
    }

}
