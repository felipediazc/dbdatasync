/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdcapps.dbdatasync.datadefinition;

/**
 *
 * @author Felipe Diaz C <felipediazc@fdcapps.com>
 */
public class DataDependency {

    String table;
    String data;
    String columnpk;
    Boolean updateOnExist;

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

    public void setColumnpk(String columnpk) {
        this.columnpk = columnpk;
    }

    public Boolean getUpdateOnExist() {
        return updateOnExist;
    }

    public void setUpdateOnExist(Boolean updateOnExist) {
        this.updateOnExist = updateOnExist;
    }

}
