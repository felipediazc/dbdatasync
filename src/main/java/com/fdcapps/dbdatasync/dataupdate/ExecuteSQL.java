package com.fdcapps.dbdatasync.dataupdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class ExecuteSQL {

    private static final Logger log = Logger.getLogger(ExecuteSQL.class.getName());

    public String getColumnType(int columnType) {
        if (columnType == java.sql.Types.NUMERIC || columnType == java.sql.Types.DECIMAL) {
            return "STRING";
        } else if (columnType == java.sql.Types.SMALLINT || columnType == java.sql.Types.INTEGER) {
            return "INTEGER";
        } else if (columnType == java.sql.Types.BIGINT) {
            return "INTEGER";
        } else if (columnType == java.sql.Types.CHAR || columnType == java.sql.Types.LONGVARCHAR
                || columnType == java.sql.Types.VARCHAR || columnType == 12) {
            return "STRING";
        } else if (columnType == java.sql.Types.REAL) {
            return "DOUBLE";
        } else if (columnType == java.sql.Types.FLOAT || columnType == java.sql.Types.DOUBLE) {
            return "DOUBLE";
        } else if (columnType == java.sql.Types.TIMESTAMP) {
            return "TIMESTAMP";
        } else if (columnType == java.sql.Types.DATE) {
            return "DATE";
        } else if (columnType == java.sql.Types.BOOLEAN || columnType == java.sql.Types.BIT) {
            return "BOOLEAN";
        } else {
            return "STRING";
        }
    }

    public static boolean isInteger(Object str) {
        try {
            Integer.parseInt(str.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(Object str) {
        try {
            Double.parseDouble(str.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isBoolean(Object str) {
        try {
            return Boolean.parseBoolean(str.toString());
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isLong(Object str) {
        try {
            Long.parseLong(str.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidTimestamp(String strDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(strDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public static boolean isString(JSONObject record, String columnName) {
        try {
            record.getString(columnName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static JSONArray getParameters(JSONObject record) {
        JSONArray parameters = new JSONArray();
        Iterator<?> keys = record.keys();
        while (keys.hasNext()) {
            String columnName = (String) keys.next();
            Object objData = record.get(columnName);
            JSONObject column = new JSONObject();
            column.put("key", columnName);
            if (isInteger(objData)) {
                column.put("value", record.get(columnName).toString());
                column.put("type", "INTEGER");
            } else if (isDouble(objData) || isLong(objData)) {
                column.put("value", record.get(columnName).toString());
                column.put("type", "DOUBLE");
            } else if (isBoolean(objData)) {
                column.put("value", record.get(columnName).toString());
                column.put("type", "BOOLEAN");
            } else if (isValidTimestamp(record.get(columnName).toString())) {
                column.put("value", record.get(columnName).toString());
                column.put("type", "TIMESTAMP");
            } else if (isString(record, columnName)) {
                column.put("value", record.getString(columnName));
                column.put("type", "STRING");
            } else {
                column.put("value", record.get(columnName).toString());
                column.put("type", "STRING");
            }
            parameters.put(column);
        }
        return parameters;
    }

    public static JSONArray getUpdateParameters(JSONArray parameters, String columnPk) {
        JSONArray updateParameters = new JSONArray();
        for (int i = 0; i < parameters.length(); i++) {
            JSONObject param = parameters.getJSONObject(i);
            updateParameters.put(param);
        }
        String[] parts = columnPk.split(",");
        for (int i = 0; i < parts.length; i++) {
            String primaryKey = parts[i].trim();
            for (int j = 0; j < parameters.length(); j++) {
                JSONObject param = parameters.getJSONObject(j);
                if (param.getString("key").equalsIgnoreCase(primaryKey)) {
                    updateParameters.put(param);
                    break;
                }
            }
        }
        return updateParameters;
    }

    public static JSONObject executeUpdate(String sql, JSONArray arrayParameters, Connection con) throws SQLException {
        JSONObject jsonObject = new JSONObject();
        String key = "";
        String value = null;
        try {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                int j = 1;
                for (int i = 0; i < arrayParameters.length(); i++) {
                    JSONObject param = arrayParameters.getJSONObject(i);
                    key = param.getString("key");
                    value = null;
                    if (param.has("value") && !param.isNull("value")) {
                        value = param.get("value").toString();
                    }
                    String type = param.getString("type");
                    switch (type) {
                    case "STRING":
                        ps.setString(j, value);
                        break;
                    case "DATE":
                        if (value == null) {
                            ps.setNull(j, java.sql.Types.TIMESTAMP);
                        } else {
                            long longDate = Long.parseLong(value);
                            ps.setDate(j, new java.sql.Date(longDate));
                        }
                        break;
                    case "TIMESTAMP":
                        if (value == null) {
                            ps.setNull(j, java.sql.Types.TIMESTAMP);
                        } else {
                            // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            Date date = sdf.parse(value);
                            ps.setTimestamp(j, new java.sql.Timestamp(date.getTime()));
                        }
                        break;
                    case "DOUBLE":
                        if (value == null) {
                            ps.setNull(1, java.sql.Types.DOUBLE);
                        } else {
                            ps.setDouble(j, Double.parseDouble(value));
                        }
                        break;
                    case "INTEGER":
                        if (value == null) {
                            ps.setNull(j, java.sql.Types.INTEGER);
                        } else {
                            ps.setInt(j, Integer.parseInt(value));
                        }
                        break;
                    case "BOOLEAN":
                        if (value == null) {
                            ps.setNull(j, java.sql.Types.BOOLEAN);
                        } else {
                            ps.setBoolean(j, Boolean.parseBoolean(value));
                        }
                        break;
                    default:
                        break;
                    }
                    j++;
                }
                Integer rowCount = ps.executeUpdate();
                jsonObject.put("totalrows", rowCount);
                jsonObject.put("error", "");
            }
        } catch (Exception e) {
            log.error("executeUpdate() : " + e);
            log.error("parameter: " + key + " value: " + value);
            jsonObject.put("data", "");
            jsonObject.put("error", e);
            throw new SQLException("" + e + ". parameter: " + key + " value: " + value);
        }
        return jsonObject;
    }
}
