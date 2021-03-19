package com.fdcapps.dbdatasync.input;

import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * DataInputResourceImpl is an implementation of the DataInput Interface
 *
 * @author Felipe Diaz C <felipediazc@fdcapps.com>
 */
public class DataInputResourceImpl implements DataInput {

    private static final Logger log = Logger.getLogger(DataInputResourceImpl.class.getName());

    @Override
    public InputStream getDataInput(String definitionFile){
        URL test1 = getClass().getResource("/");
        log.info(test1.getPath()); 
        return this.getClass().getClassLoader().getResourceAsStream(definitionFile);
    }

}
