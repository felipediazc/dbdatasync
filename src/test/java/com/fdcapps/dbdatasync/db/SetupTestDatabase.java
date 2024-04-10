package com.fdcapps.dbdatasync.db;


import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.io.FileMatchers.anExistingDirectory;

public class SetupTestDatabase {

    private static final Logger log = LoggerFactory.getLogger(SetupTestDatabase.class.getName());
    private static Boolean isDatabaseInstalled = false;

    @SneakyThrows
    private static List<File> getScriptFiles(String... paths) {
        LinkedList<File> result = new LinkedList<>();
        for (String path : paths) {
            File file;
            URL url = SetupTestDatabase.class.getClassLoader().getResource(path);
            if (url != null) {
                file = new File(url.toURI());
            } else {
                file = new File(path);
            }
            assertThat(file, anExistingDirectory());
            File[] sqlFiles = file.listFiles(f -> f.isFile() && f.getName().endsWith(".sql"));
            if (sqlFiles != null) {
                result.addAll(Arrays.asList(sqlFiles));
            }
        }
        result.sort(Comparator.comparing(File::getName));
        return result;
    }


    public static void runDatabaseScripts(String resourceName) {
        if (!isDatabaseInstalled) {
            log.info("********* INSTALLING H2 DATABASE FOR TESTING *******");
            List<File> sqlFiles = getScriptFiles("db");
            for (File sqlFile : sqlFiles) {
                executeDatabaseScript(sqlFile.getPath(), resourceName);
            }
            isDatabaseInstalled = true;
        }
    }


    public static void executeDatabaseScript(String filePath, String resourceName) {
        StringBuilder query = new StringBuilder();
        try {
            FileInputStream fstream = new FileInputStream(filePath);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            String strLine;
            String strtmp = "";
            List<String> fileList = new ArrayList<>();
            boolean comment = false;
            while ((strLine = br.readLine()) != null) {
                fileList.add(strLine);
            }
            in.close();
            Connection con = getConnection(resourceName);
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            for (String s : fileList) {
                strtmp = s;
                if (strtmp.length() > 0) {
                    if (strtmp.charAt(0) == '-' && strtmp.charAt(1) == '-') {
                        comment = true;
                    } else if (strtmp.indexOf("CREATE") == 0 || strtmp.indexOf("COMMENT") == 0 || strtmp.indexOf("ALTER") == 0 || strtmp.indexOf("SELECT") == 0 || strtmp.indexOf("INSERT INTO") == 0 && comment) {
                        if (query.toString().trim().compareTo("") != 0) {
                            st.execute(query.toString());
                        }
                        query = new StringBuilder();
                        comment = false;
                        query.append(strtmp).append("\n");
                    } else if (strtmp.indexOf("SET") != 0 && strtmp.indexOf("GRANT") != 0 && strtmp.indexOf("REVOKE") != 0) {
                        query.append(strtmp).append("\n");
                    }
                }
            }
            st.execute(query.toString());
            con.commit();
            con.setAutoCommit(true);
        } catch (Exception e) {
            log.error("ERROR on script execution {} Query was {}", e, query.toString());
        }
    }

    public static Connection getConnection(String resourceName) {
        Connection con = null;
        try {
            ResourceBundle resource;
            resource = ResourceBundle.getBundle(resourceName, Locale.getDefault());
            Class.forName(resource.getString("driver"));
            con = DriverManager.getConnection(resource.getString("url"), resource.getString("username"),
                    resource.getString("password"));
        } catch (ClassNotFoundException | SQLException ex2) {
            log.error("ERROR al crear la conexion con la base de datos {}", ex2.toString());
        }
        return con;
    }
}
