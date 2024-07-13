package com.fdcapps.dbdatasync.exportbuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fdcapps.dbdatasync.exportbuilder.exceptions.ExportBuilderException;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Slf4j
public class DataToJsonImpl implements DataToJson {


    @Override
    public List<String> getParameters(String sql) {
        ArrayList<String> paramList = new ArrayList<>();
        Pattern p = Pattern.compile("(\\{[A-Za-z0-9]+\\})");
        Matcher matcher = p.matcher(sql);
        while (matcher.find()) {
            paramList.add(matcher.group(1));
        }
        return paramList;
    }

    @Override
    public Map<String, String> getParametersAndValues(String sql, List<String> values) throws ExportBuilderException {
        List<String> paramList = getParameters(sql);
        if (values.size() != paramList.size()) {
            throw new ExportBuilderException("Parameters and values length do not match");
        }
        HashMap<String, String> paramValues = new HashMap<>();
        for (int i = 0; i < values.size(); i++) {
            paramValues.put(paramList.get(i), values.get(i));
        }
        return paramValues;
    }

    @Override
    public String getFullSql(String sql, Map<String, String> paramValues) {
        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile("(\\{[A-Za-z0-9]+\\})");
        Matcher matcher = p.matcher(sql);
        while (matcher.find()) {
            String param = matcher.group(1);
            String value = paramValues.get(param);
            matcher.appendReplacement(sb, value);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @Override
    public JSONArray getJsonFromResultSet(ResultSet resultSet) throws JSONException, SQLException {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            int totalRows = resultSet.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 0; i < totalRows; i++) {
                String columnName = resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase();
                Object columnValue = resultSet.getObject(i + 1);

                if (columnValue instanceof Clob) {
                    columnValue = resultSet.getString(i + 1);
                }
                // if value in DB is null, then we set it to default value
                if (columnValue == null) {
                    columnValue = MetaDataUtil.getNullColumnType(resultSet.getMetaData().getColumnType(i + 1));
                }
                /*
                 * Next if block is a hack. In case when in db we have values like price and
                 * price1 there's a bug in jdbc - both this names are getting stored as price in
                 * ResulSet. Therefore when we store second column value, we overwrite original
                 * value of price. To avoid that, i simply add 1 to be consistent with DB.
                 */
                if (obj.has(columnName)) {
                    columnName += "1";
                }
                obj.put(columnName, columnValue);
            }
            jsonArray.put(obj);
        }
        return jsonArray;
    }

    @Override
    public JSONArray getDataFromSql(String sql, Connection con) throws SQLException {
        JSONArray data = new JSONArray();
        try (Statement st = con.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            data = getJsonFromResultSet(rs);
        } catch (SQLException e) {
            log.error("Error executing SQL sentence {} {}", sql, e.toString());
            throw e;
        }
        return data;
    }

}
