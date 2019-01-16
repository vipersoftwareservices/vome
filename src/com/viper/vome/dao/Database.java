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

public class Database {

    private String tableSchema;
    private String tableCatalog;
    private String charsetName;
    private String collationName;

    private Row info;

    private final List<Table> tables = new ArrayList<Table>();
    private final List<Procedure> procedures = new ArrayList<Procedure>();

    public Database() {

    }

    public Database(Row info) {
        setInfo(info);
    }

    public Row getInfo() {
        return info;
    }

    public void setInfo(Row info) {
        this.info = info;

        this.tableSchema = info.getString("TABLE_SCHEM");
        if (info.containsKey("TABLE_CATALOG")) {
            this.tableCatalog = info.getString("TABLE_CATALOG");
        }
        if (info.containsKey("TABLE_CAT")) {
            this.tableCatalog = info.getString("TABLE_CAT");
        }
    }

    public String getName() {
        return getTableCatalog();
    }

    public void setName(String name) {
        setTableCatalog(name);
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

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public String getCollationName() {
        return collationName;
    }

    public void setCollationName(String collationName) {
        this.collationName = collationName;
    }

    /**
     * 
     * @return
     */
    public List<Table> getTables() {
        return tables;
    }

    /**
     * 
     * @param name
     * @return
     */
    public final Table findTable(String name) {
        for (Table table : tables) {
            if (name.equalsIgnoreCase(table.getName())) {
                return table;
            }
        }
        return null;
    }

    /**
     * Determine if a table exists.
     * 
     * @param table
     *            The name of the table.
     * @return True if the table exists.
     * @throws Exception
     *             A database error occurred.
     */
    public boolean tableExists(String name) throws Exception {
        return (findTable(name) != null);
    }

    public final List<String> listTableNames() {
        List<String> list = new ArrayList<String>();
        for (Table table : tables) {
            list.add(table.getName());
        }
        return list;
    }

    public List<Procedure> getProcedures() {
        return procedures;
    }

    public final Procedure findProcedure(String name) {
        for (Procedure procedure : procedures) {
            if (name.equalsIgnoreCase(procedure.getName())) {
                return procedure;
            }
        }
        return null;
    }

    public boolean procedureExists(String name) throws Exception {
        return (findProcedure(name) != null);
    }

    public final List<String> listProcedureNames() {
        List<String> list = new ArrayList<String>();
        for (Procedure procedure : procedures) {
            list.add(procedure.getName());
        }
        return list;
    }
    
    public final String toString() {
        return getName();
    }

}
