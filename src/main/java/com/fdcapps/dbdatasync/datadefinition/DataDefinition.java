/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdcapps.dbdatasync.datadefinition;

import lombok.*;

import java.util.List;

/**
 * @author Felipe Diaz C <felipediazc@fdcapps.com>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataDefinition {

    String id;
    String table;
    String data;
    String columnpk;
    String version;
    List<Object> dependencies;

}
