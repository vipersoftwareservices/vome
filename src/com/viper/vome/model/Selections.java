/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java    1.00 2003/06/15
 *
 * Copyright 1998-2018 by Viper Software Services
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Viper Software Services. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Viper Software Services.
 *
 * @author Tom Nevin (TomNevin@pacbell.net)
 *
 * @version 1.0, 06/15/2018 
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.vome.model;

import java.util.ArrayList;
import java.util.List;

public class Selections {

    private String filename;

    private FileFormat backupFormat;
    private DataScope dataScope;

    private List<String> databaseNames = new ArrayList<String>();
    private List<String> tableNames = new ArrayList<String>();

    public List<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<String> getDatabaseNames() {
        return databaseNames;
    }

    public void setDatabaseNames(List<String> databaseNames) {
        this.databaseNames = databaseNames;
    }

    public FileFormat getBackupFormat() {
        return backupFormat;
    }

    public void setBackupFormat(FileFormat backupFormat) {
        this.backupFormat = backupFormat;
    }

    public DataScope getDataScope() {
        return dataScope;
    }

    public void setDataScope(DataScope dataScope) {
        this.dataScope = dataScope;
    }
}
