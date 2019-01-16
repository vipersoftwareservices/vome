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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.viper.vome.model.TableType;

/**
 * @author tnevin
 */

public class Table {

    private String tableSchema;
    private String tableCatalog;
    private String name;
    private String tableTypeStr;
    private TableType tableType;
    private String viewDefinition;

    private Row info;
    
    private String sql = null;

    private List<Column> columns = new ArrayList<Column>();
    private List<PrimaryKey> primaryKeys = new ArrayList<PrimaryKey>();
    private List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();
    private List<Index> indicies = new ArrayList<Index>();
    private List<Row> rows = new ArrayList<Row>();

    // Not persistent
    private Column filterColumn;
    private Object filterValue;
    private Column orderColumn;

    public Table() {

    }

    public Table(String databaseName, String tableName) {
        tableSchema = databaseName;
        name = tableName;
    }

    public Table(Row info) {
        setInfo(info);
    }

    public Row getInfo() {
        return info;
    }

    public void setInfo(Row info) {
        this.info = info;

        this.tableSchema = info.getString("TABLE_SCHEM");
        this.tableCatalog = info.getString("TABLE_CAT");
        this.name = info.getString("TABLE_NAME");
        this.tableTypeStr = info.getString("TABLE_TYPE");
        this.tableType = toTableType(tableTypeStr);
    }

    /**
     * <pre>
     * +----------------------+--------------+------+-----+---------+-------+
     * | Field                | Type         | Null | Key | Default | Extra |
     * +----------------------+--------------+------+-----+---------+-------+
     * | TABLE_CATALOG        | varchar(512) | YES  |     | NULL    |       |
     * | TABLE_SCHEMA         | varchar(64)  | NO   |     |         |       |
     * | TABLE_NAME           | varchar(64)  | NO   |     |         |       |
     * | VIEW_DEFINITION      | longtext     | NO   |     | NULL    |       |
     * | CHECK_OPTION         | varchar(8)   | NO   |     |         |       |
     * | IS_UPDATABLE         | varchar(3)   | NO   |     |         |       |
     * | DEFINER              | varchar(77)  | NO   |     |         |       |
     * | SECURITY_TYPE        | varchar(7)   | NO   |     |         |       |
     * | CHARACTER_SET_CLIENT | varchar(32)  | NO   |     |         |       |
     * | COLLATION_CONNECTION | varchar(32)  | NO   |     |         |       |
     * +----------------------+--------------+------+-----+---------+-------+
     * </pre>
     */

    public void updateInfo(Row info) {
        this.viewDefinition = info.getString("VIEW_DEFINITION");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatabaseName() {
        return tableCatalog;
    }

    public void setDatabaseName(String databaseName) {
        tableCatalog = databaseName;
    }

    public String getTableCatalog() {
        return tableCatalog;
    }

    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableCatalog(String tableCatalog) {
        this.tableCatalog = tableCatalog;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public TableType getTableType() {
        return tableType;
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

    public Column getFilterColumn() {
        return filterColumn;
    }

    public void setFilterColumn(Column filterColumn) {
        this.filterColumn = filterColumn;
    }

    public Object getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(Object filterValue) {
        this.filterValue = filterValue;
    }

    public Column getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(Column orderColumn) {
        this.orderColumn = orderColumn;
    }

    public String getViewDefinition() {
        return viewDefinition;
    }

    public void setViewDefinition(String viewDefinition) {
        this.viewDefinition = viewDefinition;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Row> getRows() {
        return rows;
    }

    public final List<Column> getColumns() {
        return columns;
    }

    public final Column findColumn(String name) {
        for (Column column : columns) {
            if (name.equalsIgnoreCase(column.getName())) {
                return column;
            }
        }
        return null;
    }

    public final List<String> listColumnNames() {
        List<String> list = new ArrayList<String>();
        for (Column column : columns) {
            list.add(column.getName());
        }
        return list;
    }

    public final List<PrimaryKey> getPrimaryKeys() {
        return primaryKeys;
    }

    public final boolean isPrimaryKey(String name) {
        return (findPrimaryKey(name) != null);
    }

    public final PrimaryKey findPrimaryKey(String name) {
        for (PrimaryKey primaryKey : primaryKeys) {
            if (name.equalsIgnoreCase(primaryKey.getName())) {
                return primaryKey;
            }
        }
        return null;
    }

    public final List<String> listPrimaryKeys() {
        List<String> list = new ArrayList<String>();
        for (PrimaryKey primaryKey : primaryKeys) {
            list.add(primaryKey.getName());
        }
        return list;
    }

    public final List<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public final ForeignKey findForeignKey(String name) {
        for (ForeignKey foreignKey : foreignKeys) {
            if (name.equalsIgnoreCase(foreignKey.getName())) {
                return foreignKey;
            }
        }
        return null;
    }

    public final List<Index> getIndicies() {
        return indicies;
    }

    public final Index findIndex(String name) {
        for (Index index : indicies) {
            if (name.equalsIgnoreCase(index.getName())) {
                return index;
            }
        }
        return null;
    }

    //

    public final Row findRow(String name, Object value) {
        for (Row row : rows) {
            if (row.getValue(name) == value) {
                return row;
            }
        }
        return null;
    }

    public static final Row find(List<Row> seekRows, Row seekRow) throws Exception {
        for (Row row : seekRows) {
            boolean found = true;
            for (String columnName : seekRow.keySet()) {
                Object valueExpect = seekRow.getValue(columnName);
                Object valueActual = row.getValue(columnName);
                if (!valueExpect.equals(valueActual)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return row;
            }
        }
        return null;
    }

    public static final List<Row> findAll(List<Row> seekRows, Row seekRow) throws Exception {
        List<Row> rows = new ArrayList<Row>();
        for (Row row : seekRows) {
            boolean found = true;
            for (String columnName : seekRow.keySet()) {
                Object valueExpect = seekRow.getValue(columnName);
                Object valueActual = row.getValue(columnName);
                if (!valueExpect.equals(valueActual)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                rows.add(row);
            }
        }
        return rows;
    }

    public final int count(Column column, Object value) throws Exception {

        int counter = 0;
        for (Row row : rows) {
            if (value.equals(row.getValue(column.getName()))) {
                counter = counter + 1;
            }
        }
        return counter;
    }

    public final int count(Row seekRow) throws Exception {
        int counter = 0;
        for (Row row : rows) {
            boolean found = true;
            for (String columnName : seekRow.keySet()) {
                Object valueExpect = seekRow.getValue(columnName);
                Object valueActual = row.getValue(columnName);
                if (!valueExpect.equals(valueActual)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                counter = counter + 1;
            }
        }
        return counter;
    }

    public final List<Object> uniqueValues(String name) {
        Map<Object, Object> values = new HashMap<Object, Object>();
        for (Row row : rows) {
            values.put(row.get(name), row.get(name));
        }
        return new ArrayList<Object>(values.values());
    }

    private final TableType toTableType(String str) {
        str = str.replace(' ', '-').toLowerCase();
        return TableType.fromValue(str);
    }

    public final String toString() {
        return getName();
    }

}
