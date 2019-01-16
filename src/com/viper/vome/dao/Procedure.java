/*
 * -----------------------------------------------------------------------------
 *                      VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * MIT License
 * 
 * Copyright (c) #{classname}.html #{util.YYYY()} Viper Software Services
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE
 *
 * -----------------------------------------------------------------------------
 */

package com.viper.vome.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tnevin
 */

public class Procedure extends Row {

    private String procedureSchema;
    private String procedureCatalog;
    private String procedureName;
    private String specificName;
    private Integer procedureType;
    private String remarks;
    private String source;

    // Mysql Extensions
    private String language;
    private String sqlDataAccess;
    private String isDeterministic;
    private String securityType;
    private String paramList;
    private String returns;

    private List<Column> columns = new ArrayList<Column>();

    public Procedure() {

    }

    public Procedure(Row row) {
        this.putAll(row);

        this.procedureSchema = row.getString("PROCEDURE_SCHEM");
        this.procedureCatalog = row.getString("PROCEDURE_CAT");
        this.procedureName = row.getString("PROCEDURE_NAME");
        this.specificName = row.getString("SPECIFIC_NAME");
        this.procedureType = row.getInteger("PROCEDURE_TYPE");
        this.remarks = row.getString("REMARKS");
    }

    public void update(Row row) {

        this.language = row.getString("LANGUAGE");
        this.sqlDataAccess = row.getString("SQL_DATA_ACCESS");
        this.isDeterministic = row.getString("IS_DETERMINISTIC");
        this.securityType = row.getString("SECURITY_TYPE");
        this.paramList = row.getString("PARAM_LIST");
        this.returns = row.getString("RETURNS");
        this.source = row.getString("body");
    }

    public String getProcedureSchema() {
        return procedureSchema;
    }

    public void setProcedureSchema(String procedureSchema) {
        this.procedureSchema = procedureSchema;
    }

    public String getDatabaseName() {
        return procedureCatalog;
    }

    public void setDatabaseName(String databaseName) {
        procedureCatalog = databaseName;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getSpecificName() {
        return specificName;
    }

    public void setSpecificName(String specificName) {
        this.specificName = specificName;
    }

    public Integer getProcedureType() {
        return procedureType;
    }

    public void setProcedureType(Integer procedureType) {
        this.procedureType = procedureType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getName() {
        return procedureName;
    }

    public void setName(String name) {
        this.procedureName = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public String getProcedureCatalog() {
        return procedureCatalog;
    }

    public void setProcedureCatalog(String procedureCatalog) {
        this.procedureCatalog = procedureCatalog;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSqlDataAccess() {
        return sqlDataAccess;
    }

    public void setSqlDataAccess(String sqlDataAccess) {
        this.sqlDataAccess = sqlDataAccess;
    }

    public String getIsDeterministic() {
        return isDeterministic;
    }

    public void setIsDeterministic(String isDeterministic) {
        this.isDeterministic = isDeterministic;
    }

    public String getSecurityType() {
        return securityType;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public String getParamList() {
        return paramList;
    }

    public void setParamList(String paramList) {
        this.paramList = paramList;
    }

    public String getReturns() {
        return returns;
    }

    public void setReturns(String returns) {
        this.returns = returns;
    }

    public final String toString() {
        return getName();
    }
}
