/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdcapps.dbdatasync.datadefinition;

import java.util.List;

/**
 *
 * @author Felipe Diaz C <felipediazc@fdcapps.com>
 */
public class DataDefinition {

    String id;
    String table;
    String data;
    String columnpk;
    List<Object> dependencies;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getColumnpk() {
        return columnpk;
    }

    public void setColumnPk(String columnpk) {
        this.columnpk = columnpk;
    }

    public List<Object> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Object> dependencies) {
        this.dependencies = dependencies;
    }

}
