/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdcapps.dbdatasync.input;

import com.fdcapps.dbdatasync.context.ContextObj;
import com.fdcapps.dbdatasync.datadefgen.DataDefinitionGen;
import com.fdcapps.dbdatasync.datadefgen.ObjectParser;
import com.fdcapps.dbdatasync.datadefinition.DataDefinition;
import com.fdcapps.dbdatasync.datadefinition.DataDependency;
import com.fdcapps.dbdatasync.datadefinition.DefinitionDependency;
import com.google.gson.internal.LinkedTreeMap;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.io.InputStream;
import java.util.List;


/**
 *
 * @author Felipe Diaz C <felipediazc@fdcapps.com>
 */
public class TestDataInput {

    ContextObj ctx = ContextObj.instance();
    DataInput dataInput = ctx.getDataInput();
    DataDefinitionGen generator = ctx.getGenerator();

    @Test
    public void testDataInput() throws Exception {

        InputStream in = dataInput.getDataInput("pantalla.json");
        DataDefinition dataDefinition = generator.getDataDefinition(in);
        assertEquals("pantalla", dataDefinition.getId());
        assertEquals("tuga_roles", dataDefinition.getTable());
        assertEquals("rol", dataDefinition.getColumnpk());
        List<Object> depList = dataDefinition.getDependencies();
        DataDependency dependency = (DataDependency) ObjectParser.getDefinitionObject((LinkedTreeMap<?,?>) depList.get(0));
        assertEquals("tuga_conexiones", dependency.getTable());
        assertEquals("SELECT * FROM tuga_conexiones WHERE codigo IN (SELECT conexion FROM tuga_pantallas WHERE codigo = {codigo})", dependency.getData());
        assertEquals("codigo", dependency.getColumnpk());
        assertEquals(false, dependency.getUpdateOnExist());

        DefinitionDependency definitionDependency = (DefinitionDependency) ObjectParser.getDefinitionObject((LinkedTreeMap<?,?>) depList.get(3));
        assertEquals("importaexcel", definitionDependency.getDefinition());
        assertEquals("SELECT importaexcel FROM tuga_campos WHERE pantalla = {codigo} AND importaexcel is NOT NULL", definitionDependency.getParameter());
    }

}
