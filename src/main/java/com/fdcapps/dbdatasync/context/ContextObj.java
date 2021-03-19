/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdcapps.dbdatasync.context;

import com.fdcapps.dbdatasync.datadefgen.DataDefinitionGen;
import com.fdcapps.dbdatasync.datadefgen.DataDefinitionGenImpl;
import com.fdcapps.dbdatasync.dataupdate.UpdateData;
import com.fdcapps.dbdatasync.dataupdate.UpdateDataImpl;
import com.fdcapps.dbdatasync.exportbuilder.DataToJson;
import com.fdcapps.dbdatasync.exportbuilder.DataToJsonImpl;
import com.fdcapps.dbdatasync.exportbuilder.ExportData;
import com.fdcapps.dbdatasync.exportbuilder.ExportDataImpl;
import com.fdcapps.dbdatasync.input.DataInput;
import com.fdcapps.dbdatasync.input.DataInputResourceImpl;

/**
 * ContextObj is an object to manage spring IOC objects
 *
 * @author Felipe Diaz C <felipediazc@fdcapps.com>
 */
public class ContextObj {

    private static ContextObj instance = null;

    private ContextObj() {
    }

    public static ContextObj instance() {
        if (instance == null) {
            instance = new ContextObj();
        }
        return instance;
    }

    public DataInput getDataInput() {
        return new DataInputResourceImpl();
    }

    public ExportData getExportData() {
        return new ExportDataImpl();
    }

    public DataDefinitionGen getGenerator() {
        return new DataDefinitionGenImpl();
    }

    public DataToJson getDataToJson() {
        return new DataToJsonImpl();
    }

    public UpdateData getUpdateData() {
        return new UpdateDataImpl();
    }
}
