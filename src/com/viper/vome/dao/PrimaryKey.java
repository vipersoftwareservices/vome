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

/**
 * @author tnevin
 */

public class PrimaryKey extends Row {

    private String tableSchema;
    private String tableCatalog;
    private String tableName;
    private String columnName;
    private int keySequence;
    private String name;

    public PrimaryKey(Row row) {
        setInfo(row);
    }

    public void setInfo(Row info) {
        this.putAll(info);

        this.tableSchema = info.getString("TABLE_SCHEM");
        this.tableCatalog = info.getString("TABLE_CAT");
        this.tableName = info.getString("TABLE_NAME");
        this.columnName = info.getString("COLUMN_NAME");
        this.keySequence = info.getInt("KEY_SEQ");
        this.name = info.getString("PK_NAME");
    }

    public String getTableSchema() {
        return tableSchema;
    }

    public String getTableCatalog() {
        return tableCatalog;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getKeySequence() {
        return keySequence;
    }

    public String getName() {
        return name;
    }

    public final String toString() {
        return getName();
    }

}
