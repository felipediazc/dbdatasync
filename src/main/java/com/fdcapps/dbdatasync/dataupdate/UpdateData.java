package com.fdcapps.dbdatasync.dataupdate;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONObject;

public interface UpdateData {

    public void sync(JSONObject dataDefinition, Connection con) throws SQLException;

}
