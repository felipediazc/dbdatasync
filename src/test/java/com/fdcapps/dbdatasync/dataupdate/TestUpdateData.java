package com.fdcapps.dbdatasync.dataupdate;

import com.fdcapps.dbdatasync.context.ContextObj;
import com.fdcapps.dbdatasync.datadefgen.DataDefinitionGen;
import com.fdcapps.dbdatasync.datadefinition.DataDefinition;
import com.fdcapps.dbdatasync.db.SetupTestDatabase;
import com.fdcapps.dbdatasync.exportbuilder.ExportData;
import com.fdcapps.dbdatasync.input.DataInput;
import com.fdcapps.dbdatasync.input.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


public class TestUpdateData {

    ContextObj ctx = ContextObj.instance();
    UpdateData updateData = ctx.getUpdateData();
    DataDefinitionGen dataDefinitionGen = ctx.getGenerator();
    DataInput dataInput = ctx.getDataInput();
    ExportData exportData = ctx.getExportData();

    private static final Logger log = LoggerFactory.getLogger(TestUpdateData.class.getName());

    public static final String DATABASE_DEST = "database-dest";

    static {
        SetupTestDatabase.runDatabaseScripts(DATABASE_DEST);
        log.info("*************** startup - creating DB connection *************** ");
    }

    @Test
    public void testIsValidTimestamp() {
        String inDate = "2017-02-02 15:21:58.0";
        assertTrue(ExecuteSQL.isValidTimestamp(inDate));
    }

    @Test
    public void testUpdateDatabase() {
        InputStream in = dataInput.getDataInput("12-customers.json");
        String str = Utils.getStringFromInputStream(in);
        JSONObject jsonDataToSync = new JSONObject(str);
        boolean thrown = false;
        try {
            Connection conDestiny = SetupTestDatabase.getConnection(DATABASE_DEST);
            updateData.sync(jsonDataToSync, conDestiny);
            conDestiny.close();
        } catch (SQLException e) {
            thrown = true;
        }
        assertFalse(thrown);
    }

    @Test
    public void testSync() throws Exception {
        List<String> parameters = Collections.singletonList("12-vallein");
        InputStream in = dataInput.getDataInput("components.json");
        DataDefinition dataDefinition = dataDefinitionGen.getDataDefinition(in);
        Connection conOrigin = SetupTestDatabase.getConnection("database");
        JSONObject jsonDataToSync = exportData.getData(dataDefinition, parameters, conOrigin);
        log.info(jsonDataToSync.toString());
        conOrigin.close();
        boolean thrown = false;
        try {
            Connection conDestiny = SetupTestDatabase.getConnection(DATABASE_DEST);
            updateData.sync(jsonDataToSync, conDestiny);
            conDestiny.close();
        } catch (SQLException e) {
            thrown = true;
        }
        assertFalse(thrown);
    }
}
