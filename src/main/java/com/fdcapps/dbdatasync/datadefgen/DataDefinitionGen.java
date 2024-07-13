/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdcapps.dbdatasync.datadefgen;

import com.fdcapps.dbdatasync.datadefinition.DataDefinition;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Felipe Diaz C <felipediazc@fdcapps.com>
 */
public interface DataDefinitionGen {

    DataDefinition getDataDefinition(InputStream in) throws IOException;

}
