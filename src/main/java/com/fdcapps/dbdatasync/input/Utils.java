package com.fdcapps.dbdatasync.input;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
public class Utils {


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
