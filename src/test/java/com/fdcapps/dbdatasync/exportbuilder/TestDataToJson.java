package com.fdcapps.dbdatasync.exportbuilder;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fdcapps.dbdatasync.context.ContextObj;

import org.json.JSONArray;
import org.junit.Test;

public class TestDataToJson {

    ContextObj ctx = ContextObj.instance();
    DataToJson dataToJson = ctx.getDataToJson();

    @Test
    public void testGetParameters() throws Exception {

        String sql = "SELECT a,b,c FROM table WHERE a = {amr} AND b = '{b}' AND c = {codigo}";
        List<String> paramList = dataToJson.getParameters(sql);
        assertArrayEquals(new String[] { "{amr}", "{b}", "{codigo}" }, paramList.toArray());
    }

    @Test
    public void testGetParametersValues() throws Exception {

        String sql = "SELECT a,b,c FROM table WHERE a = {amr} AND b = '{b}' AND c = {codigo}";
        List<String> valuesList = Arrays.asList("1", "'2'", "'2020-10-02'");
        Map<String, String> paramValues = dataToJson.getParametersAndValues(sql, valuesList);
        assertEquals("1", paramValues.get("{amr}"));
        assertEquals("'2'", paramValues.get("{b}"));
        assertEquals("'2020-10-02'", paramValues.get("{codigo}"));
    }

    @Test
    public void testGetFullSql() throws Exception {

        String sql = "SELECT a,b,c FROM table WHERE a = {amr} AND b = '{b}' AND c = '{codigo}'";
        List<String> valuesList = Arrays.asList("1", "2", "2020-10-02");
        Map<String, String> paramValues = dataToJson.getParametersAndValues(sql, valuesList);
        sql = dataToJson.getFullSql(sql, paramValues);
        assertEquals("SELECT a,b,c FROM table WHERE a = 1 AND b = '2' AND c = '2020-10-02'", sql);
    }

    @Test
    public void testGetJsonFromResultSet() throws Exception {

        TestExportData exportData = new TestExportData();
        Connection con = exportData.getConnection();
        String sql = "SELECT * FROM tuga_campos WHERE pantalla = 104";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        JSONArray jsonArray = dataToJson.getJsonFromResultSet(rs);
        String data = "[{\"codigo\":119,\"tipo\":\"LISTAM\",\"tipolista\":\"E\",\"campo_valor_query\":\"\",\"importaexcel\":\"\",\"obligatorio\":\"S\",\"javascript\":\"\",\"reporte\":\"\",\"pantalla\":104,\"longitud\":\"\",\"depende_campo\":\"\",\"nombrecampo\":\"Roles\",\"variable\":\"roles\",\"orden\":3,\"colecciondato\":\"\",\"valoreslista\":\"\",\"valor_por_parametro\":\"\"},{\"codigo\":117,\"tipo\":\"INFO\",\"tipolista\":\"E\",\"campo_valor_query\":\"login\",\"importaexcel\":\"\",\"obligatorio\":\"S\",\"javascript\":\"\",\"reporte\":\"\",\"pantalla\":104,\"longitud\":100,\"depende_campo\":\"\",\"nombrecampo\":\"Login\",\"variable\":\"login\",\"orden\":1,\"colecciondato\":\"\",\"valoreslista\":\"\",\"valor_por_parametro\":\"\"},{\"codigo\":118,\"tipo\":\"LISTA\",\"tipolista\":\"E\",\"campo_valor_query\":\"habilitado\",\"importaexcel\":\"\",\"obligatorio\":\"S\",\"javascript\":\"\",\"reporte\":\"\",\"pantalla\":104,\"longitud\":\"\",\"depende_campo\":\"\",\"nombrecampo\":\"habilitado\",\"variable\":\"habilitado\",\"orden\":2,\"colecciondato\":\"\",\"valoreslista\":\"S,N\",\"valor_por_parametro\":\"\"}]";
        assertEquals(data, jsonArray.toString());
        rs.close();
        st.close();
        con.close();
    }

}
