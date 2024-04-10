package com.fdcapps.dbdatasync.dataupdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.fdcapps.dbdatasync.exportbuilder.DataToJsonValues;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteSQL {

    private static final Logger log = LoggerFactory.getLogger(ExecuteSQL.class.getName());
    public static final String INTEGER = "INTEGER";
    public static final String NULL = "NULL";
    public static final String STRING = "STRING";
    public static final String DOUBLE = "DOUBLE";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String BOOLEAN = "BOOLEAN";
    public static final String DATE = "DATE";
    public static final String VALUE = "value";
    public static final String TYPE = "type";

    public String getColumnType(int columnType) {
        if (columnType == java.sql.Types.NUMERIC || columnType == java.sql.Types.DECIMAL) {
            return STRING;
        } else if (columnType == java.sql.Types.SMALLINT || columnType == java.sql.Types.INTEGER) {
            return INTEGER;
        } else if (columnType == java.sql.Types.BIGINT) {
            return INTEGER;
        } else if (columnType == java.sql.Types.CHAR || columnType == java.sql.Types.LONGVARCHAR
                || columnType == java.sql.Types.VARCHAR) {
            return STRING;
        } else if (columnType == java.sql.Types.REAL) {
            return DOUBLE;
        } else if (columnType == java.sql.Types.FLOAT || columnType == java.sql.Types.DOUBLE) {
            return DOUBLE;
        } else if (columnType == java.sql.Types.TIMESTAMP) {
            return TIMESTAMP;
        } else if (columnType == java.sql.Types.DATE) {
            return DATE;
        } else if (columnType == java.sql.Types.BOOLEAN || columnType == java.sql.Types.BIT) {
            return BOOLEAN;
        } else {
            return STRING;
        }
    }

    public static boolean isNull(Object str) {
        String value = str.toString();
        return (value.equals(DataToJsonValues.INT_NULL.toString()) || value.equals(DataToJsonValues.TIMESTAMP_NULL.toString()) || value.equals(DataToJsonValues.BIGINT_NULL.toString())
                || value.equals(DataToJsonValues.DOUBLE_NULL.toString()) || value.equals(DataToJsonValues.DATE_NULL.toString()) || value.equals(DataToJsonValues.BOOLEAN_NULL.toString())
                || value.equals(DataToJsonValues.STRING_NULL.toString()));
    }

    public static boolean isInteger(Object str) {
        if (Boolean.TRUE.equals(isNull(str))) {
            return false;
        }
        try {
            Integer.parseInt(str.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(Object str) {
        if (Boolean.TRUE.equals(isNull(str))) {
            return false;
        }
        try {
            Double.parseDouble(str.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isBoolean(Object str) {
        if (Boolean.TRUE.equals(isNull(str))) {
            return false;
        }
        try {
            String value = str.toString();
            if (value != null) {
                value = value.trim().toLowerCase();
                if (value.equals("true") || value.equals("false")) {
                    Boolean.parseBoolean(str.toString());
                    return true;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isLong(Object str) {
        if (Boolean.TRUE.equals(isNull(str))) {
            return false;
        }
        try {
            Long.parseLong(str.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidTimestamp(String strDate) {
        if (Boolean.TRUE.equals(isNull(strDate))) {
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(strDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public static boolean isString(JSONObject jsonObject, String columnName) {
        try {
            return !Boolean.TRUE.equals(isNull(jsonObject.getString(columnName)));
        } catch (Exception e) {
            return false;
        }
    }

    public static JSONArray getParameters(JSONObject jsonObject) {
        JSONArray parameters = new JSONArray();
        Iterator<?> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String columnName = (String) keys.next();
            Object objData = jsonObject.get(columnName);
            JSONObject column = new JSONObject();
            column.put("key", columnName);
            if (isInteger(objData)) {
                column.put(VALUE, jsonObject.get(columnName).toString());
                column.put(TYPE, INTEGER);
            } else if (isDouble(objData) || isLong(objData)) {
                column.put(VALUE, jsonObject.get(columnName).toString());
                column.put(TYPE, DOUBLE);
            } else if (isBoolean(objData)) {
                column.put(VALUE, jsonObject.get(columnName).toString());
                column.put(TYPE, BOOLEAN);
            } else if (isValidTimestamp(jsonObject.get(columnName).toString())) {
                column.put(VALUE, jsonObject.get(columnName).toString());
                column.put(TYPE, TIMESTAMP);
            } else if (isString(jsonObject, columnName)) {
                column.put(VALUE, jsonObject.getString(columnName));
                column.put(TYPE, STRING);
            } else if (Boolean.TRUE.equals(isNull(objData))) {
                column.put(VALUE, (jsonObject.get(columnName)).toString());
                column.put(TYPE, NULL);
            } else {
                column.put(VALUE, jsonObject.get(columnName).toString());
                column.put(TYPE, STRING);
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
        for (String part : parts) {
            String primaryKey = part.trim();
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

    public static String getParamValue(JSONObject param) {
        if (param.has(VALUE) && !param.isNull(VALUE)) {
            return param.get(VALUE).toString();
        }
        return null;
    }

    public static void setParamDate(PreparedStatement ps, int j, String value) throws SQLException {
        if (value == null) {
            ps.setNull(j, java.sql.Types.TIMESTAMP);
        } else {
            long longDate = Long.parseLong(value);
            ps.setDate(j, new java.sql.Date(longDate));
        }
    }

    public static void setParamTimestamp(PreparedStatement ps, int j, String value) throws SQLException, ParseException {
        if (value == null) {
            ps.setNull(j, java.sql.Types.TIMESTAMP);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date = sdf.parse(value);
            ps.setTimestamp(j, new java.sql.Timestamp(date.getTime()));
        }
    }

    public static void setParamDouble(PreparedStatement ps, int j, String value) throws SQLException {
        if (value == null) {
            ps.setNull(1, java.sql.Types.DOUBLE);
        } else {
            ps.setDouble(j, Double.parseDouble(value));
        }
    }

    public static void setParamInt(PreparedStatement ps, int j, String value) throws SQLException {
        if (value == null) {
            ps.setNull(j, java.sql.Types.INTEGER);
        } else {
            ps.setInt(j, Integer.parseInt(value));
        }
    }

    public static void setParamBoolean(PreparedStatement ps, int j, String value) throws SQLException {
        if (value == null) {
            ps.setNull(j, java.sql.Types.BOOLEAN);
        } else {
            ps.setBoolean(j, Boolean.parseBoolean(value));
        }
    }

    public static void setParamNull(PreparedStatement ps, int j, String value) throws SQLException {
        switch (value) {
            case "INT_NULL":
            case "BIGINT_NULL":
                ps.setNull(j, java.sql.Types.INTEGER);
                break;
            case "DOUBLE_NULL":
                ps.setNull(1, java.sql.Types.DOUBLE);
                break;
            case "TIMESTAMP_NULL":
            case "DATE_NULL":
                ps.setNull(j, java.sql.Types.TIMESTAMP);
                break;
            case "BOOLEAN_NULL":
                ps.setNull(j, java.sql.Types.BOOLEAN);
                break;
            default:
                ps.setString(j, null);
                break;
        }
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
                    value = getParamValue(param);
                    String type = param.getString(TYPE);
                    switch (type) {
                        case STRING:
                            ps.setString(j, value);
                            break;
                        case DATE:
                            setParamDate(ps, j, value);
                            break;
                        case TIMESTAMP:
                            setParamTimestamp(ps, j, value);
                            break;
                        case DOUBLE:
                            setParamDouble(ps, j, value);
                            break;
                        case INTEGER:
                            setParamInt(ps, j, value);
                            break;
                        case BOOLEAN:
                            setParamBoolean(ps, j, value);
                            break;
                        case NULL:
                            setParamNull(ps, j, value);
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
            log.error("executeUpdate() : {}", e.toString());
            log.error("parameter: {} value {}", key, value);
            jsonObject.put("data", "");
            jsonObject.put("error", e);
            throw new SQLException(e + ". parameter: " + key + " value: " + value);
        }
        return jsonObject;
    }
}
