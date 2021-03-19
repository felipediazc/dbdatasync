package com.fdcapps.dbdatasync.exportbuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.fdcapps.dbdatasync.exportbuilder.exceptions.ExportBuilderException;

import org.json.JSONArray;
import org.json.JSONException;

public interface DataToJson {

    public List<String> getParameters(String sql);

    public Map<String, String> getParametersAndValues(String sql, List<String> values) throws ExportBuilderException;

    public String getFullSql(String sql, Map<String, String> paramValues);

    public JSONArray getJsonFromResultSet(ResultSet resultSet) throws JSONException, SQLException;

    public JSONArray getDataFromSql(String sql, Connection con) throws SQLException;
}
