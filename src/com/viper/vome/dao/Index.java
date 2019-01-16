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

public class Index extends Row {

    private String databaseName;
    private String tableSchema;
    private String tableCatalog;
    private String tableName;
    private String columnName;
    private String name;
    private IndexClassType indexClass;
    private IndexType indexType;
    private Boolean editable;
    private Boolean primary;
    private String description;
    private Boolean nonUnique;
    private String indexQualifier;
    private Integer type;

    /**
     * <p>
     * Each index column description has the following columns:
     * <ol>
     * <li><b>TABLE_CAT</b> String {@code =>} table catalog (may be <code>null</code>)
     * <li><b>TABLE_SCHEM</b> String {@code =>} table schema (may be <code>null</code>)
     * <li><b>TABLE_NAME</b> String {@code =>} table name
     * <li><b>NON_UNIQUE</b> boolean {@code =>} Can index values be non-unique. false when TYPE is
     * tableIndexStatistic
     * <li><b>INDEX_QUALIFIER</b> String {@code =>} index catalog (may be <code>null</code>);
     * <code>null</code> when TYPE is tableIndexStatistic
     * <li><b>INDEX_NAME</b> String {@code =>} index name; <code>null</code> when TYPE is
     * tableIndexStatistic
     * <li><b>TYPE</b> short {@code =>} index type:
     * <ul>
     * <li>tableIndexStatistic - this identifies table statistics that are returned in conjuction
     * with a table's index descriptions
     * <li>tableIndexClustered - this is a clustered index
     * <li>tableIndexHashed - this is a hashed index
     * <li>tableIndexOther - this is some other style of index
     * </ul>
     * <li><b>ORDINAL_POSITION</b> short {@code =>} column sequence number within index; zero when
     * TYPE is tableIndexStatistic
     * <li><b>COLUMN_NAME</b> String {@code =>} column name; <code>null</code> when TYPE is
     * tableIndexStatistic
     * <li><b>ASC_OR_DESC</b> String {@code =>} column sort sequence, "A" {@code =>} ascending, "D"
     * {@code =>} descending, may be <code>null</code> if sort sequence is not supported;
     * <code>null</code> when TYPE is tableIndexStatistic
     * <li><b>CARDINALITY</b> long {@code =>} When TYPE is tableIndexStatistic, then this is the
     * number of rows in the table; otherwise, it is the number of unique values in the index.
     * <li><b>PAGES</b> long {@code =>} When TYPE is tableIndexStatisic then this is the number of
     * pages used for the table, otherwise it is the number of pages used for the current index.
     * <li><b>FILTER_CONDITION</b> String {@code =>} Filter condition, if any. (may be
     * <code>null</code>)
     * </ol>
     */

    private List<IndexColumn> indexColumns = new ArrayList<IndexColumn>();

    public Index() {

    }

    public Index(Row row) {
        setInfo(row);
    }

    public void setInfo(Row info) {
        this.putAll(info);

        this.tableSchema = info.getString("TABLE_SCHEM");
        this.tableCatalog = info.getString("TABLE_CAT");
        this.tableName = info.getString("TABLE_NAME");
        this.columnName = info.getString("COLUMN_NAME");
        this.name = info.getString("INDEX_NAME");
        this.nonUnique = info.getBoolean("NON_UNIQUE");
        this.indexQualifier = info.getString("INDEX_QUALIFIER");
        this.type = info.getInteger("TYPE");

    }

    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public String getTableCatalog() {
        return tableCatalog;
    }

    public void setTableCatalog(String tableCatalog) {
        this.tableCatalog = tableCatalog;
    }

    public Boolean getNonUnique() {
        return nonUnique;
    }

    public void setNonUnique(Boolean nonUnique) {
        this.nonUnique = nonUnique;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public IndexClassType getIndexClass() {
        return indexClass;
    }

    public void setIndexClass(IndexClassType indexClass) {
        this.indexClass = indexClass;
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIndexQualifier() {
        return indexQualifier;
    }

    public void setIndexQualifier(String indexQualifier) {
        this.indexQualifier = indexQualifier;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<IndexColumn> getIndexColumns() {
        return indexColumns;
    }

    public void setIndexColumns(List<IndexColumn> indexColumns) {
        this.indexColumns = indexColumns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final List<IndexColumn> getColumns() {
        return indexColumns;
    }

    public final String toString() {
        return getName();
    }

}
