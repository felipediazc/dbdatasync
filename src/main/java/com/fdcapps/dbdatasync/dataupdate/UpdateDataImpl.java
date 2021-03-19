package com.fdcapps.dbdatasync.dataupdate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class UpdateDataImpl implements UpdateData {

    public static final String SET = " SET ";
    public static final String VALUES = "VALUES (";
    public static final String TABLE = "table";
    public static final String DEFINITION = "definition";
    public static final String COLUMNPK = "columnpk";

    private static final Logger log = Logger.getLogger(UpdateDataImpl.class.getName());

    @Override
    public void sync(JSONObject jsonDataToSync, Connection con) throws SQLException {
        try {
            con.setAutoCommit(false);
            syncAll(jsonDataToSync, con);
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            log.error("Error on sync " + e);
            con.rollback();
            throw e;
        }
    }

    public void syncAll(JSONObject jsonDataToSync, Connection con) throws SQLException {
        try {
            syncMainData(jsonDataToSync, con);
            JSONArray dependencies = jsonDataToSync.getJSONArray("dependencies");
            for (int i = 0; i < dependencies.length(); i++) {
                JSONObject dependency = dependencies.getJSONObject(i);
                syncDependency(dependency, con);
            }
        } catch (SQLException e) {
            log.error("Error on syncAll " + e);
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
            for (int i = 0; i < data.length(); i++) {
                JSONObject record = data.getJSONObject(i);
                selectSql = getSelectSentence(tableName, columnPk, record);
                rs = st1.executeQuery(selectSql);
                if (rs.next()) {
                    updateSql = getUpdateSentence(tableName, columnPk, record);
                    JSONArray parameters = ExecuteSQL.getParameters(record);
                    parameters = ExecuteSQL.getUpdateParameters(parameters, columnPk);
                    ExecuteSQL.executeUpdate(updateSql, parameters, con);
                } else {
                    updateSql = getInsertSentence(tableName, record);
                    JSONArray parameters = ExecuteSQL.getParameters(record);
                    ExecuteSQL.executeUpdate(updateSql, parameters, con);
                }
            }
        } catch (SQLException e) {
            log.error("Error on syncMainData. SELECT_SQL: " + selectSql + " UPDATE_SQL: " + updateSql + " : " + e);
            throw e;
        }
    }

    public void syncDependency(JSONObject dependency, Connection con) throws SQLException {
        ResultSet rs;
        String updateSql = "";
        String selectSql = "";
        try (Statement st1 = con.createStatement()) {
            if (dependency.has(TABLE)) {
                String tableName = dependency.getString(TABLE);
                String columnPk = dependency.getString(COLUMNPK);
                Boolean updateOnExist = dependency.getBoolean("updateOnExist");
                JSONArray data = dependency.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject record = data.getJSONObject(i);
                    selectSql = getSelectSentence(tableName, columnPk, record);
                    rs = st1.executeQuery(selectSql);
                    if (rs.next()) {
                        if (Boolean.TRUE.equals(updateOnExist)) {
                            updateSql = getUpdateSentence(tableName, columnPk, record);
                            JSONArray parameters = ExecuteSQL.getParameters(record);
                            parameters = ExecuteSQL.getUpdateParameters(parameters, columnPk);
                            ExecuteSQL.executeUpdate(updateSql, parameters, con);
                        }
                    } else {
                        updateSql = getInsertSentence(tableName, record);
                        JSONArray parameters = ExecuteSQL.getParameters(record);
                        ExecuteSQL.executeUpdate(updateSql, parameters, con);
                    }
                }
            } else if (dependency.has(DEFINITION)) {
                JSONObject definition = dependency.getJSONObject(DEFINITION);
                syncAll(definition, con);
            }
        } catch (SQLException e) {
            log.error("Error on syncDependency. SELECT_SQL: " + selectSql + " UPDATE_SQL: " + updateSql + " : " + e);
            throw e;
        }
    }

    public String escapeQuote(String str) {
        return str.replace("'", "''");
    }

    public String getInsertSentence(String tableName, JSONObject record) {
        StringBuilder sql1 = new StringBuilder("INSERT INTO ").append(tableName);
        StringBuilder sql2 = new StringBuilder(" (");
        StringBuilder sql3 = new StringBuilder(VALUES);
        Iterator<?> keys = record.keys();
        while (keys.hasNext()) {
            if (!sql2.toString().equals(" (")) {
                sql2.append(",");
            }
            if (!sql3.toString().equals(VALUES)) {
                sql3.append(",");
            }
            String columnName = (String) keys.next();
            sql2.append(columnName);
            sql3.append("?");
        }
        sql2.append(") ");
        sql3.append(")");
        return sql1.append(sql2).append(sql3).toString();
    }

    public String getUpdateSentence(String tableName, String columnPk, JSONObject record) {
        StringBuilder sql1 = new StringBuilder("UPDATE ").append(tableName);
        StringBuilder sql2 = new StringBuilder(SET);

        StringBuilder sql3 = new StringBuilder(" WHERE ");
        String[] parts = columnPk.split(",");
        for (int i = 0; i < parts.length; i++) {
            String primaryKey = parts[i].trim();
            sql3.append(primaryKey).append(" = ?");
            if (i < (parts.length - 1)) {
                sql3.append(" AND ");
            }
        }
        Iterator<?> keys = record.keys();
        while (keys.hasNext()) {
            if (!sql2.toString().equals(SET)) {
                sql2.append(",");
            }
            String columnName = (String) keys.next();
            sql2.append(columnName).append(" = ");
            sql2.append("?");
        }
        return sql1.append(sql2).append(sql3).toString();
    }

    public String getSelectSentence(String tableName, String columnPk, JSONObject record) {
        StringBuilder sql1 = new StringBuilder("SELECT ");
        StringBuilder sql2 = new StringBuilder("");
        String[] parts = columnPk.split(",");
        StringBuilder sql3 = new StringBuilder(" FROM ").append(tableName).append(" WHERE 1 = 1 ");
        Iterator<?> keys = record.keys();

        while (keys.hasNext()) {
            if (!sql2.toString().equals("")) {
                sql2.append(", ");
            }
            String columnName = (String) keys.next();
            sql2.append(columnName);
            Object objData = record.get(columnName);
            if (ExecuteSQL.isInteger(objData) || ExecuteSQL.isDouble(objData) || ExecuteSQL.isBoolean(objData)
                    || ExecuteSQL.isLong(objData)) {
                if (Arrays.asList(parts).contains(columnName)) {
                    sql3.append(" AND ").append(columnName).append(" = ").append(record.get(columnName).toString());
                }
            } else if (ExecuteSQL.isString(record, columnName)) {
                if (Arrays.asList(parts).contains(columnName)) {
                    sql3.append(" AND ").append(columnName).append(" = ").append("'")
                            .append(escapeQuote(record.getString(columnName))).append("'");
                }
            } else {
                if (Arrays.asList(parts).contains(columnName)) {
                    sql3.append(" AND ").append(columnName).append(" = ").append("'")
                            .append(escapeQuote(record.get(columnName).toString())).append("'");
                }
            }
        }
        return sql1.append(sql2).append(sql3).toString();
    }
}
