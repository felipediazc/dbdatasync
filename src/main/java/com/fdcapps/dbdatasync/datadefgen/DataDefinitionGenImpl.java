/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdcapps.dbdatasync.datadefgen;

import com.fdcapps.dbdatasync.datadefinition.DataDefinition;
import com.fdcapps.dbdatasync.input.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;


/**
 *
 * @author Felipe Diaz C <felipediazc@fdcapps.com>
 */
public class DataDefinitionGenImpl implements DataDefinitionGen {

    @Override
    public DataDefinition getDataDefinition(InputStream in) throws IOException {
        String str = Utils.getStringFromInputStream(in);
        Gson gson = new Gson();
        return gson.fromJson(str, DataDefinition.class);
    }

}
