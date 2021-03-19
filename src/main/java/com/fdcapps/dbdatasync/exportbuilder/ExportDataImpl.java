/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdcapps.dbdatasync.exportbuilder;

import com.fdcapps.dbdatasync.context.ContextObj;
import com.fdcapps.dbdatasync.datadefgen.DataDefinitionGen;
import com.fdcapps.dbdatasync.datadefgen.ObjectParser;
import com.fdcapps.dbdatasync.datadefinition.DataDefinition;
import com.fdcapps.dbdatasync.datadefinition.DataDependency;
import com.fdcapps.dbdatasync.datadefinition.DefinitionDependency;
import com.fdcapps.dbdatasync.exportbuilder.exceptions.ExportBuilderException;
import com.fdcapps.dbdatasync.input.DataInput;
import com.google.gson.internal.LinkedTreeMap;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Felipe Diaz C <felipediazc@fdcapps.com>
 */
public class ExportDataImpl implements ExportData {

    ContextObj ctx = ContextObj.instance();
    DataToJson dataToJson = ctx.getDataToJson();
    DataInput dataInput = ctx.getDataInput();
    DataDefinitionGen generator = ctx.getGenerator();
    
    private static final Logger log = Logger.getLogger(ExportDataImpl.class.getName());

    public List<String> getListFromJSONArray(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject columnName = jsonArray.getJSONObject(i);
                Iterator<?> keys = columnName.keys();
                while(keys.hasNext()) {
                    String key = (String) keys.next();
                    list.add(columnName.get(key).toString());
                }
            }
        }
        return list;
    }

    @Override
    public JSONObject getData(DataDefinition dataDefinition, List<String> parameters, Connection con)
            throws ExportBuilderException, SQLException {
        String sql = dataDefinition.getData();
        Map<String, String> paramValues = dataToJson.getParametersAndValues(sql, parameters);
        sql = dataToJson.getFullSql(sql, paramValues);
        JSONObject jsonData = new JSONObject();
        jsonData.put("table", dataDefinition.getTable());
        JSONArray jsonArray = dataToJson.getDataFromSql(sql, con);
        jsonData.put("data", jsonArray);
        jsonData.put("columnpk", dataDefinition.getColumnpk());
        List<Object> dependenciesList = dataDefinition.getDependencies();
        JSONArray jsonDependencies = new JSONArray();
        dependenciesList.forEach(object -> {
            JSONObject dependency = new JSONObject();
            Object dependencyObj = ObjectParser.getDefinitionObject((LinkedTreeMap<?, ?>) object);
            String sqlDep;
            if (dependencyObj instanceof DataDependency) {
                DataDependency temp = (DataDependency) dependencyObj;
                dependency.put("table", temp.getTable());
                sqlDep = temp.getData();
                sqlDep = dataToJson.getFullSql(sqlDep, paramValues);
                try {
                    JSONArray jsonDataDependency = dataToJson.getDataFromSql(sqlDep, con);
                    dependency.put("data", jsonDataDependency);
                } catch (Exception e) {
                    log.error("Error getting JSONArray Data from SQL sentence ", e);
                }
                dependency.put("columnpk", temp.getColumnpk());
                dependency.put("updateOnExist", temp.getUpdateOnExist());
                jsonDependencies.put(dependency);
            } else if (dependencyObj instanceof DefinitionDependency) {
                DefinitionDependency temp = (DefinitionDependency) dependencyObj;
                // dependency.put("definition", temp.getDefinition());
                sqlDep = temp.getParameter();
                sqlDep = dataToJson.getFullSql(sqlDep, paramValues);
                //log.info("***** SQL IS " + sqlDep);
                try {
                    JSONArray jsonDataDependency = dataToJson.getDataFromSql(sqlDep, con);
                    //log.info("***** DEPENDENCY IS " + jsonDataDependency.toString());
                    // dependency.put("parameter", jsonDataDependency);
                    List<String> definitionParameters = getListFromJSONArray(jsonDataDependency);
                    //log.info("***** DEFINITION PARAMS IS " + definitionParameters.toString());
                    // jsonDataDependency
                    InputStream in = dataInput.getDataInput(temp.getDefinition() + ".json");
                    DataDefinition dataSubDefinition = generator.getDataDefinition(in);
                    //JSONObject json = exportData.getData(dataDefinition, definitionParameters, con);
                    JSONObject json = getData(dataSubDefinition, definitionParameters, con);
                    dependency.put("definition", json);
                    //log.info("***** PUT DEFINITION " + json.toString());
                    jsonDependencies.put(dependency);
                } catch (Exception e) {
                    log.error("Error getting JSONArray Data from SQL sentence ", e);
                }
                // jsonDependencies.put(dependency);
            }
        });
        jsonData.put("dependencies", jsonDependencies);
        return jsonData;
    }

}
