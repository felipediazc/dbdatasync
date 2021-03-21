package com.fdcapps.dbdatasync.exportbuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.fdcapps.dbdatasync.context.ContextObj;
import com.fdcapps.dbdatasync.datadefgen.DataDefinitionGen;
import com.fdcapps.dbdatasync.datadefinition.DataDefinition;
import com.fdcapps.dbdatasync.input.DataInput;
import com.fdcapps.dbdatasync.input.Utils;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.Test;

public class TestExportData {

    private static final Logger log = Logger.getLogger(TestExportData.class.getName());
    ContextObj ctx = ContextObj.instance();

    ExportData exportData = ctx.getExportData();
    DataDefinitionGen generator = ctx.getGenerator();
    DataInput dataInput = ctx.getDataInput();

    public Connection getConnection() {
        Connection con = null;
        try {
            ResourceBundle resource;
            resource = ResourceBundle.getBundle("database", Locale.getDefault());
            Class.forName(resource.getString("driver"));
            con = DriverManager.getConnection(resource.getString("url"), resource.getString("username"),
                    resource.getString("password"));
        } catch (ClassNotFoundException | SQLException ex2) {
            log.error("ERROR al crear la conexion con la base de datos " + ex2);
        }
        return con;
    }

    @Test
    public void testDataInput() throws Exception {

        Connection con = getConnection();
        assertNotNull(con);
        con.close();
    }
/*
    @Test
    public void testGetData() throws Exception {
        List<String> parameters = Arrays.asList("4824");//113
        InputStream in = dataInput.getDataInput("pantalla.json");
        DataDefinition dataDefinition = generator.getDataDefinition(in);
        Connection con = getConnection();
        JSONObject jsonDataToSync = exportData.getData(dataDefinition, parameters, con);
        in = dataInput.getDataInput("out1.json");//test1.json -> cambiar a postgresql
        String str = Utils.getStringFromInputStream(in);
        //log.info(jsonDataToSync.toString());
        assertEquals(str, jsonDataToSync.toString());
        con.close();
    }*/
}
