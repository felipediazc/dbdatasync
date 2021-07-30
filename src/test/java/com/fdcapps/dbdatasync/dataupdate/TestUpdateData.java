package com.fdcapps.dbdatasync.dataupdate;

import com.fdcapps.dbdatasync.context.ContextObj;
import com.fdcapps.dbdatasync.datadefgen.DataDefinitionGen;
import com.fdcapps.dbdatasync.datadefinition.DataDefinition;
import com.fdcapps.dbdatasync.exportbuilder.ExportData;
import com.fdcapps.dbdatasync.input.DataInput;
import com.fdcapps.dbdatasync.input.Utils;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TestUpdateData {

    ContextObj ctx = ContextObj.instance();
    UpdateData updateData = ctx.getUpdateData();
    DataDefinitionGen dataDefinitionGen = ctx.getGenerator();
    DataInput dataInput = ctx.getDataInput();
    ExportData exportData = ctx.getExportData();

    private static final Logger log = Logger.getLogger(TestUpdateData.class.getName());

    public Connection getConnection(String resourceName) {
        Connection con = null;
        try {
            ResourceBundle resource;
            resource = ResourceBundle.getBundle(resourceName, Locale.getDefault());
            Class.forName(resource.getString("driver"));
            con = DriverManager.getConnection(resource.getString("url"), resource.getString("username"),
                    resource.getString("password"));
        } catch (ClassNotFoundException | SQLException ex2) {
            log.error("ERROR al crear la conexion con la base de datos " + ex2);
        }
        return con;
    }

    @Test
    public void testIsValidTimestamp() throws Exception {
        String inDate = "2017-02-02 15:21:58.0";
        assertEquals(true, ExecuteSQL.isValidTimestamp(inDate));
    }

    @Test
    public void testUpdateDatabase() throws Exception {
        InputStream in = dataInput.getDataInput("component_12-vallein.json");
        String str = Utils.getStringFromInputStream(in);
        JSONObject jsonDataToSync = new JSONObject(str);
        Boolean thrown = false;
        try {
            Connection conDestiny = getConnection("database-dest");
            /*Use TriggerManagement only for oracle*/
            /*TriggerManagement.disableTriggers(conDestiny);*/
            updateData.sync(jsonDataToSync, conDestiny);
            /*TriggerManagement.enableTriggers(conDestiny);*/
            conDestiny.close();
        } catch (SQLException e) {
            thrown = true;
        }
        assertEquals(false, thrown);
    }

    @Test
    public void testSync() throws Exception {
        List<String> parameters = Arrays.asList("12-vallein");
        InputStream in = dataInput.getDataInput("components.json");
        DataDefinition dataDefinition = dataDefinitionGen.getDataDefinition(in);
        Connection conOrigin = getConnection("database");
        JSONObject jsonDataToSync = exportData.getData(dataDefinition, parameters, conOrigin);
        log.info(jsonDataToSync.toString());
        conOrigin.close();
        Boolean thrown = false;
        try {
            Connection conDestiny = getConnection("database-dest");
            updateData.sync(jsonDataToSync, conDestiny);
            conDestiny.close();
        } catch (SQLException e) {
            thrown = true;
        }
        assertEquals(false, thrown);
    }
}
