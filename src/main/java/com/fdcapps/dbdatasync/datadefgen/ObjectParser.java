package com.fdcapps.dbdatasync.datadefgen;

import com.fdcapps.dbdatasync.datadefinition.DataDependency;
import com.fdcapps.dbdatasync.datadefinition.DefinitionDependency;
import com.google.gson.internal.LinkedTreeMap;

public class ObjectParser {

    private ObjectParser() {
    }

    public static Object getDefinitionObject(LinkedTreeMap<?, ?> obj) {

        if (obj.containsKey("definition")) {
            DefinitionDependency dependency = new DefinitionDependency();
            dependency.setDefinition(obj.get("definition").toString());
            dependency.setParameter(obj.get("parameter").toString());
            return dependency;
        } else {
            DataDependency data = new DataDependency();
            data.setTable(obj.get("table").toString());
            data.setData(obj.get("data").toString());
            data.setColumnpk(obj.get("columnpk").toString());
            data.setDelete(obj.get("delete").toString());
            data.setUpdateOnExist(Boolean.parseBoolean(obj.get("updateonexist").toString()));
            return data;
        }

    }

}
