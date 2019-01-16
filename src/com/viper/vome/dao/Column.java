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

public class Column extends Row {

    private static final List<String> columnNames = new ArrayList<String>();

    private String tableSchema;
    private String tableCatalog;
    private String tableName;
    private String name;
    private String typeName;
    private int dataType;
    private int columnSize = 0;
    private int decimalDigits = 0;
    private boolean signed = false;
    private boolean nullable = false;
    private boolean autoIncrement = false;
    private String remarks;

    // Added not part of the result set
    private boolean primaryKey = false;

    public Column() {

    }

    public Column(Row row) {
        setInfo(row);
    }

    public void setInfo(Row row) {

        this.putAll(row);

        this.tableSchema = row.getString("TABLE_SCHEM");
        this.tableCatalog = row.getString("TABLE_CAT");
        this.tableName = row.getString("TABLE_NAME");
        this.name = row.getString("COLUMN_NAME");
        this.dataType = row.getInt("DATA_TYPE");
        this.columnSize = row.getInt("COLUMN_SIZE");
        this.decimalDigits = row.getInt("DECIMAL_DIGITS");
        this.typeName = row.getString("TYPE_NAME");
        this.signed = row.getBoolean("IS_SIGNED");
        this.nullable = row.getBoolean("IS_NULLABLE");
        this.autoIncrement = row.getBoolean("IS_AUTOINCREMENT");
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

    public String getName() {
        return name;
    }

    public int getDataType() {
        return dataType;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isSigned() {
        return signed;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public void setColumnSize(Integer columnSize) {
        this.columnSize = (columnSize == null) ? 0 : columnSize;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public void setTableCatalog(String tableCatalog) {
        this.tableCatalog = tableCatalog;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public void setDecimalDigits(Integer decimalDigits) {
        this.decimalDigits = (decimalDigits == null) ? 0 : decimalDigits;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public static final List<String> getDataTypes() {
        List<String> items = new ArrayList<String>();
        items.add("array");
        items.add("bit");
        items.add("boolean");
        items.add("bigint");
        items.add("binary");
        items.add("blob");
        items.add("char");
        items.add("clob");
        items.add("datalink");
        items.add("date");
        items.add("datetime");
        items.add("decimal");
        items.add("distinct");
        items.add("double");
        items.add("enum");
        items.add("float");
        items.add("int");
        items.add("integer");
        items.add("longblob");
        items.add("longnvarchar");
        items.add("longtext");
        items.add("longvarchar");
        items.add("longvarbinary");
        items.add("mediumblob");
        items.add("mediumint");
        items.add("mediumtext");
        items.add("nchar");
        items.add("null");
        items.add("numeric");
        items.add("nvarchar");
        items.add("nclob");
        items.add("other");
        items.add("object");
        items.add("real");
        items.add("ref");
        items.add("rowid");
        items.add("set");
        items.add("smallint");
        items.add("sqlxml");
        items.add("struct");
        items.add("time");
        items.add("timestamp");
        items.add("tinyint");
        items.add("tinytext");
        items.add("text");
        items.add("varchar");
        items.add("varbinary");
        return items;
    }

    public final String toString() {
        return getName();
    }

    public static final List<String> getColumnNames() {
        return columnNames;
    }
}
