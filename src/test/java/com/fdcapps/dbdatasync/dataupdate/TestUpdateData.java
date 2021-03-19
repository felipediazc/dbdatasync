package com.fdcapps.dbdatasync.dataupdate;

import com.fdcapps.dbdatasync.context.ContextObj;
import com.fdcapps.dbdatasync.datadefgen.DataDefinitionGen;
import com.fdcapps.dbdatasync.datadefinition.DataDefinition;
import com.fdcapps.dbdatasync.exportbuilder.ExportData;
import com.fdcapps.dbdatasync.input.DataInput;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

    /*
    @Test
    public void testInsertSentence() throws Exception {
        String json1 = "{\"data\":[{\"maxcon\":10,\"codigo\":7,\"password\":\"Dzx5KmEJPoRw3pGzSy7wFQ==\",\"driver\":\"org.postgresql.Driver\",\"nombre\":\"Conexión a DEMO (PostgreSQL)\",\"url\":\"jdbc:postgresql://localhost:5432/demo?charSet=UTF8\",\"username\":\"tugauser\"}],\"columnpk\":\"codigo\",\"updateOnExist\":false,\"table\":\"tuga_conexiones\"}";
        List<String> list = updateData.getInsertSentence(new JSONObject(json1));
        String insertSentence = list.get(0);
        String expectedResult1 = "INSERT INTO tuga_conexiones (maxcon,codigo,password,driver,nombre,url,username) VALUES (10,7,'Dzx5KmEJPoRw3pGzSy7wFQ==','org.postgresql.Driver','Conexión a DEMO (PostgreSQL)','jdbc:postgresql://localhost:5432/demo?charSet=UTF8','tugauser')";
        assertEquals(expectedResult1, insertSentence);
    }

    @Test
    public void testUpdateSentence() throws Exception {
        String json1 = "{\"data\":[{\"maxcon\":10,\"codigo\":7,\"password\":\"Dzx5KmEJPoRw3pGzSy7wFQ==\",\"driver\":\"org.postgresql.Driver\",\"nombre\":\"Conexión a DEMO (PostgreSQL)\",\"url\":\"jdbc:postgresql://localhost:5432/demo?charSet=UTF8\",\"username\":\"tugauser\"}],\"columnpk\":\"codigo\",\"updateOnExist\":false,\"table\":\"tuga_conexiones\"}";
        List<String> list = updateData.getUpdateSentence(new JSONObject(json1));
        String updateSentence = list.get(0);
        String expectedResult1 = "UPDATE tuga_conexiones SET maxcon = 10,codigo = 7,password = 'Dzx5KmEJPoRw3pGzSy7wFQ==',driver = 'org.postgresql.Driver',nombre = 'Conexión a DEMO (PostgreSQL)',url = 'jdbc:postgresql://localhost:5432/demo?charSet=UTF8',username = 'tugauser' WHERE codigo = 7";
        assertEquals(expectedResult1, updateSentence);
    }*/

    @Test
    public void testIsValidTimestamp() throws Exception {
        String inDate = "2017-02-02 15:21:58.0";
        assertEquals(true, ExecuteSQL.isValidTimestamp(inDate));
    }

    @Test
    public void testSync() throws Exception {
        //pantalla.json 4824
        //reporte.json 4265
        //menu.json 4497
        List<String> parameters = Arrays.asList("4497");// 4824
        InputStream in = dataInput.getDataInput("menu.json");//pantalla.json
        DataDefinition dataDefinition = dataDefinitionGen.getDataDefinition(in);
        Connection conOrigin = getConnection("database");
        JSONObject jsonDataToSync = exportData.getData(dataDefinition, parameters, conOrigin);
        conOrigin.close();
        Boolean thrown = false;
        try {
            Connection conDestiny = getConnection("database-dest");
            TriggerManagement.disableTriggers(conDestiny);
            updateData.sync(jsonDataToSync, conDestiny);
            TriggerManagement.enableTriggers(conDestiny);
            conDestiny.close();
        } catch (SQLException e) {
            thrown = true;
        }
        assertEquals(false, thrown);
    }
}
