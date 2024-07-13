/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdcapps.dbdatasync.datadefinition;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Felipe Diaz C <felipediazc@fdcapps.com>
 */
@Setter
@Getter
public class DataDependency {

    String table;
    String data;
    String delete;
    String columnpk;
    Boolean updateOnExist;

}
