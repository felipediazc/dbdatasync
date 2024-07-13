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

    List<String> getParameters(String sql);

    Map<String, String> getParametersAndValues(String sql, List<String> values) throws ExportBuilderException;

    String getFullSql(String sql, Map<String, String> paramValues);

    JSONArray getJsonFromResultSet(ResultSet resultSet) throws JSONException, SQLException;

    JSONArray getDataFromSql(String sql, Connection con) throws SQLException;
}
