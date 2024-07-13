/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdcapps.dbdatasync.exportbuilder;

import com.fdcapps.dbdatasync.datadefinition.DataDefinition;
import com.fdcapps.dbdatasync.exportbuilder.exceptions.ExportBuilderException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONObject;

/**
 *
 * @author Felipe Diaz C <felipediazc@fdcapps.com>
 */
public interface ExportData {
    
    
     JSONObject getData(DataDefinition dataDefinition, List<String> parameters, Connection con) throws ExportBuilderException, SQLException;
    
}
