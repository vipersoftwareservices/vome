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

public class ForeignKey extends Row {

    private static final List<String> columnNames = new ArrayList<String>();

    private String pkTableSchema;
    private String pkTableCatalog;
    private String pkTableName;
    private String pkColumnName;
    private String pkName;

    private String fkTableSchema;
    private String fkTableCatalog;
    private String fkTableName;
    private String fkColumnName;
    private String fkName;

    private int keySeq;
    private int updateRule;
    private int deleteRule;
    private int deferrability;

    public ForeignKey() {

    }

    public ForeignKey(Row info) {
        setInfo(info);
    }

    public void setInfo(Row info) {
        this.putAll(info);

        this.pkTableCatalog = info.getString("PKTABLE_CAT");
        this.pkTableSchema = info.getString("PKTABLE_SCHEM");
        this.pkTableName = info.getString("PKTABLE_NAME");
        this.pkColumnName = info.getString("PKCOLUMN_NAME");
        this.pkName = info.getString("PK_NAME");

        this.fkTableCatalog = info.getString("FKTABLE_CAT");
        this.fkTableSchema = info.getString("FKTABLE_SCHEM");
        this.fkTableName = info.getString("FKTABLE_NAME");
        this.fkColumnName = info.getString("FKCOLUMN_NAME");
        this.fkName = info.getString("FK_NAME");

        this.keySeq = info.getInteger("KEY_SEQ");
        this.updateRule = info.getInteger("UPDATE_RULE");
        this.deleteRule = info.getInteger("DELETE_RULE");
        this.deferrability = info.getInteger("DEFERRABILITY");

    }

    public String getPkTableSchema() {
        return pkTableSchema;
    }

    public void setPkTableSchema(String pkTableSchema) {
        this.pkTableSchema = pkTableSchema;
    }

    public String getPkTableCatalog() {
        return pkTableCatalog;
    }

    public void setPkTableCatalog(String pkTableCatalog) {
        this.pkTableCatalog = pkTableCatalog;
    }

    public String getPkTableDatabase() {
        return pkTableCatalog;
    }

    public void setPkTableDatabase(String pkTableDatabase) {
        this.pkTableCatalog = pkTableDatabase;
    }

    public String getPkTableName() {
        return pkTableName;
    }

    public void setPkTableName(String pkTableName) {
        this.pkTableName = pkTableName;
    }

    public String getPkColumnName() {
        return pkColumnName;
    }

    public void setPkColumnName(String pkColumnName) {
        this.pkColumnName = pkColumnName;
    }

    public String getPkName() {
        return pkName;
    }

    public void setPkName(String pkName) {
        this.pkName = pkName;
    }

    public String getFkTableSchema() {
        return fkTableSchema;
    }

    public void setFkTableSchema(String fkTableSchema) {
        this.fkTableSchema = fkTableSchema;
    }

    public String getFkTableCatalog() {
        return fkTableCatalog;
    }

    public void setFkTableCatalog(String fkTableCatalog) {
        this.fkTableCatalog = fkTableCatalog;
    }

    public String getFkTableDatabase() {
        return fkTableCatalog;
    }

    public void setFkTableDatabase(String fkTableDatabase) {
        this.fkTableCatalog = fkTableDatabase;
    }

    public String getFkTableName() {
        return fkTableName;
    }

    public void setFkTableName(String fkTableName) {
        this.fkTableName = fkTableName;
    }

    public String getFkColumnName() {
        return fkColumnName;
    }

    public void setFkColumnName(String fkColumnName) {
        this.fkColumnName = fkColumnName;
    }

    public String getFkName() {
        return fkName;
    }

    public void setFkName(String fkName) {
        this.fkName = fkName;
    }

    public int getKeySeq() {
        return keySeq;
    }

    public void setKeySeq(int keySeq) {
        this.keySeq = keySeq;
    }

    public int getUpdateRule() {
        return updateRule;
    }

    public void setUpdateRule(int updateRule) {
        this.updateRule = updateRule;
    }

    public int getDeleteRule() {
        return deleteRule;
    }

    public void setDeleteRule(int deleteRule) {
        this.deleteRule = deleteRule;
    }

    public int getDeferrability() {
        return deferrability;
    }

    public void setDeferrability(int deferrability) {
        this.deferrability = deferrability;
    }

    public String getName() {
        return fkName;
    }

    public final String toString() {
        return getName();
    }

    public static final List<String> getColumnNames() {
        return columnNames;
    }
}
