package com.fdcapps.dbdatasync.dataupdate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
@Slf4j
public class UpdateDataImpl implements UpdateData {

    public static final String SET = " SET ";
    public static final String VALUES = "VALUES (";
    public static final String TABLE = "table";
    public static final String DEFINITION = "definition";
    public static final String COLUMNPK = "columnpk";

    public static final String AND = " AND ";

    @Override
    public void sync(JSONObject jsonDataToSync, Connection con) throws SQLException {
        try {
            con.setAutoCommit(false);
            syncAll(jsonDataToSync, con);
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            log.error("Error on sync {}", e.toString());
            con.rollback();
            throw e;
        }
    }

    public void syncAll(JSONObject jsonDataToSync, Connection con) throws SQLException {
        try {
            JSONArray dependencies = jsonDataToSync.getJSONArray("dependencies");
            for (int i = 0; i < dependencies.length(); i++) {
                JSONObject dependency = dependencies.getJSONObject(i);
                syncDependency(dependency, con);
            }
            syncMainData(jsonDataToSync, con);
        } catch (SQLException e) {
            log.error("Error on syncAll {}", e.toString());
            throw e;
        }
    }

    public void syncMainData(JSONObject jsonDataToSync, Connection con) throws SQLException {
        String tableName = jsonDataToSync.getString(TABLE);
        String columnPk = jsonDataToSync.getString(COLUMNPK);
        JSONArray data = jsonDataToSync.getJSONArray("data");
        ResultSet rs;
        String updateSql = "";
        String selectSql = "";
        try (Statement st1 = con.createStatement()) {
            log.info("*** Updating {}  for {}  records ", tableName, data.length());
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonRecord = data.getJSONObject(i);
                selectSql = getSelectSentence(tableName, columnPk, jsonRecord);
                rs = st1.executeQuery(selectSql);
                if (rs.next()) {
                    updateSql = getUpdateSentence(tableName, columnPk, jsonRecord);
                    JSONArray parameters = ExecuteSQL.getParameters(jsonRecord);
                    parameters = ExecuteSQL.getUpdateParameters(parameters, columnPk);
                    ExecuteSQL.executeUpdate(updateSql, parameters, con);
                } else {
                    updateSql = getInsertSentence(tableName, jsonRecord);
                    JSONArray parameters = ExecuteSQL.getParameters(jsonRecord);
                    ExecuteSQL.executeUpdate(updateSql, parameters, con);
                }
            }
        } catch (SQLException e) {
            log.error("Error on syncMainData. SELECT_SQL: {} UPDATE_SQL: {} : {}", selectSql, updateSql, e.toString());
            throw e;
        }
    }

    public void syncDependency(JSONObject dependency, Connection con) throws SQLException {
        ResultSet rs;
        String updateSql = "";
        String selectSql = "";
        JSONArray parameters = new JSONArray();
        try (Statement st1 = con.createStatement()) {
            if (dependency.has(TABLE)) {
                String tableName = dependency.getString(TABLE);
                String columnPk = dependency.getString(COLUMNPK);
                boolean updateOnExist = dependency.getBoolean("updateOnExist");
                if (dependency.has("delete")) {
                    String deleteSql = dependency.getString("delete");
                    log.debug("DELETE SENTENCE IS: " + deleteSql);
                    ExecuteSQL.executeUpdate(deleteSql, con);
                }
                JSONArray data = dependency.getJSONArray("data");
                log.info("*** Updating " + tableName + " for " + data.length() + " records ");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonRecord = data.getJSONObject(i);
                    selectSql = getSelectSentence(tableName, columnPk, jsonRecord);
                    rs = st1.executeQuery(selectSql);
                    if (rs.next()) {
                        if (updateOnExist) {
                            updateSql = getUpdateSentence(tableName, columnPk, jsonRecord);
                            parameters = ExecuteSQL.getParameters(jsonRecord);
                            parameters = ExecuteSQL.getUpdateParameters(parameters, columnPk);
                            ExecuteSQL.executeUpdate(updateSql, parameters, con);
                        }
                    } else {
                        updateSql = getInsertSentence(tableName, jsonRecord);
                        parameters = ExecuteSQL.getParameters(jsonRecord);
                        ExecuteSQL.executeUpdate(updateSql, parameters, con);
                    }
                }
            } else if (dependency.has(DEFINITION)) {
                JSONObject definition = dependency.getJSONObject(DEFINITION);
                syncAll(definition, con);
            }
        } catch (SQLException e) {
            log.error("Error on syncDependency. SELECT_SQL: {} UPDATE_SQL: {} : {}", selectSql, updateSql, e.toString());
            throw e;
        }
    }

    public String escapeQuote(String str) {
        return str.replace("'", "''");
    }

    public String getInsertSentence(String tableName, JSONObject jsonRecord) {
        StringBuilder sql1 = new StringBuilder("INSERT INTO ").append(tableName);
        StringBuilder sql2 = new StringBuilder(" (");
        StringBuilder sql3 = new StringBuilder(VALUES);
        Iterator<?> keys = jsonRecord.keys();
        while (keys.hasNext()) {
            if (!sql2.toString().equals(" (")) {
                sql2.append(",");
            }
            if (!sql3.toString().equals(VALUES)) {
                sql3.append(",");
            }
            String columnName = (String) keys.next();
            sql2.append("\"" + columnName + "\"");
            sql3.append("?");
        }
        sql2.append(") ");
        sql3.append(")");
        return sql1.append(sql2).append(sql3).toString();
    }

    public String getUpdateSentence(String tableName, String columnPk, JSONObject jsonRecord) {
        StringBuilder sql1 = new StringBuilder("UPDATE ").append(tableName);
        StringBuilder sql2 = new StringBuilder(SET);

        StringBuilder sql3 = new StringBuilder(" WHERE ");
        String[] parts = columnPk.split(",");
        for (int i = 0; i < parts.length; i++) {
            String primaryKey = parts[i].trim();
            sql3.append(primaryKey).append(" = ?");
            if (i < (parts.length - 1)) {
                sql3.append(AND);
            }
        }
        Iterator<?> keys = jsonRecord.keys();
        while (keys.hasNext()) {
            if (!sql2.toString().equals(SET)) {
                sql2.append(",");
            }
            String columnName = (String) keys.next();
            sql2.append("\"").append(columnName).append("\"").append(" = ");
            sql2.append("?");
        }
        return sql1.append(sql2).append(sql3).toString();
    }

    public String getSelectSentence(String tableName, String columnPk, JSONObject jsonRecord) {
        StringBuilder sql1 = new StringBuilder("SELECT ");
        StringBuilder sql2 = new StringBuilder();
        String[] parts = columnPk.split(",");
        StringBuilder sql3 = new StringBuilder(" FROM ").append(tableName).append(" WHERE 1 = 1 ");
        Iterator<?> keys = jsonRecord.keys();

        String[] trimmedArray = Arrays.stream(parts).map(String::trim).toArray(String[]::new);
        List<String> primaryKeysList = Arrays.asList(trimmedArray);

        while (keys.hasNext()) {
            if (!sql2.toString().isEmpty()) {
                sql2.append(", ");
            }
            String columnName = (String) keys.next();
            sql2.append("\"").append(columnName).append("\"");
            Object objData = jsonRecord.get(columnName);
            if (ExecuteSQL.isInteger(objData) || ExecuteSQL.isDouble(objData) || ExecuteSQL.isBoolean(objData)
                    || ExecuteSQL.isLong(objData)) {
                if (primaryKeysList.contains(columnName)) {
                    sql3.append(AND).append(columnName).append(" = ").append(jsonRecord.get(columnName).toString());
                }
            } else if (ExecuteSQL.isString(jsonRecord, columnName)) {
                if (primaryKeysList.contains(columnName)) {
                    sql3.append(AND).append(columnName).append(" = ").append("'")
                            .append(escapeQuote(jsonRecord.getString(columnName))).append("'");
                }
            } else if (primaryKeysList.contains(columnName)) {
                sql3.append(AND).append(columnName).append(" = ").append("'")
                        .append(escapeQuote(jsonRecord.get(columnName).toString())).append("'");
            }
        }
        return sql1.append(sql2).append(sql3).toString();
    }
}
