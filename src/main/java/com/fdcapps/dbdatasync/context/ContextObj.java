/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdcapps.dbdatasync.context;

import com.fdcapps.dbdatasync.datadefgen.DataDefinitionGen;
import com.fdcapps.dbdatasync.dataupdate.UpdateData;
import com.fdcapps.dbdatasync.exportbuilder.DataToJson;
import com.fdcapps.dbdatasync.exportbuilder.ExportData;
import com.fdcapps.dbdatasync.input.DataInput;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * ContextObj is an object to manage spring IOC objects
 *
 * @author Felipe Diaz C <felipediazc@fdcapps.com>
 */
public class ContextObj {

    private static ContextObj instance = null;
    private static ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

    private ContextObj() {
    }

    public static ContextObj instance() {
        if (instance == null) {
            instance = new ContextObj();
        }
        return instance;
    }

    public DataInput getDataInput() {
        return (DataInput) context.getBean("datainput");
    }

    public ExportData getExportData() {
        return (ExportData) context.getBean("exportdata");
    }

    public DataDefinitionGen getGenerator() {
        return (DataDefinitionGen) context.getBean("datadefinitiongen");
    }

    public DataToJson getDataToJson() {
        return (DataToJson) context.getBean("datatojson");
    }

    public UpdateData getUpdateData() {
        return (UpdateData) context.getBean("updatedata");
    }
}
