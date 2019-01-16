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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Driver {
    
    private static final String InformationSchema = "information_schema";
    private static final String CollationsTable = "COLLATIONS";
    private static final String CharacterSetsTable = "CHARACTER_SETS";
    
    public static final String toName(String name) {
        return "`" + name + "`";
    }

    public static final String toField(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return "'" + value + "'";
        }
        return value.toString();
    }

    public static final String toSQLDatabaseName(Database database) {
        return toName(database.getName());
    }

    public static final String toSQLTableName(Table table) {
        return toName(table.getDatabaseName()) + "." + toName(table.getName());
    }

    public static final String toSQLTableName(Table table, String tablename) {
        return toName(table.getDatabaseName()) + "." + toName(tablename);
    }

    public static final String toSelectSQL(Table table) {
        if (table.getSql() != null) {
            return table.getSql();
        }
        return "select * from " + toSQLTableName(table);
    }

    public static final String toSelectSQL(Table table, Column column, Object value) {
        return "select * from " + toSQLTableName(table) + " where " + toName(column.getName()) + "="
                + getValueForSQL(column, value);
    }

    public static final String toSizeSQL(Table table) {
        return "select count(*) from " + toSQLTableName(table);
    }

    public static final String toSelectSQL(Table table, int startRow, int numRowsPerPage) {

        StringBuilder buf = new StringBuilder();

        buf.append("select * from " + toSQLTableName(table));
        if (table.getFilterColumn() != null) {
            buf.append(" where " + toName(table.getFilterColumn().getName()) + "="
                    + getValueForSQL(table.getFilterColumn(), table.getFilterValue()));
        }
        if (table.getOrderColumn() != null) {
            buf.append(" order by " + toName(table.getOrderColumn().getName()));
        }
        buf.append(" limit " + startRow + "," + numRowsPerPage);

        return buf.toString();
    }

    public static final String toProcedureSourceSQL(Procedure proc) {
        return "select * from mysql.proc where db = " + toField(proc.getDatabaseName()) + " and name = "
                + toField(proc.getName());
    }

    public static final String toTableViewSQL(Table table) {
        String sql = "";
        sql += "select * from information_schema.views ";
        sql += " where table_schema=" + toField(table.getDatabaseName());
        sql += " and table_name=" + toField(table.getName());

        return sql;
    }

    public static final String toExplainSQL(Table table) {
        return "explain " + toSelectSQL(table);
    }

    public static final String toPossibleCollationsSQL( ) {
        return "select * from " + toName(InformationSchema) + "." + toName(CollationsTable);
    }
    
    public static final String toPossibleCharacterSetsSQL( ) {
        return "select * from " + toName(InformationSchema) + "." + toName(CharacterSetsTable);
    }

    /**
     * Generate the DROP statement for a table.
     * 
     * @param database
     * @param table
     *            The name of the table to drop.
     * @return The SQL to drop a table.
     */
    public static final String create(User user) {
        StringBuffer result = new StringBuffer();
        return result.toString();
    }

    /**
     * Generate the DROP statement for a table.
     * 
     * @param database
     * @param table
     *            The name of the table to drop.
     * @return The SQL to drop a table.
     */
    public static final String create(Database database) {
        StringBuffer result = new StringBuffer();
        result.append("CREATE DATABASE ");
        result.append(toSQLDatabaseName(database));
        return result.toString();
    }

    /**
     * Generate the DROP statement for a table.
     * 
     * @param database
     * @param table
     *            The name of the table to drop.
     * @return The SQL to drop a table.
     */
    public static final String drop(Database database) {
        StringBuffer result = new StringBuffer();
        result.append("DROP DATABASE ");
        result.append(toSQLDatabaseName(database));
        return result.toString();
    }

    /**
     * Generate the DROP statement for a table.
     * 
     * @param database
     * @param table
     *            The name of the table to drop.
     * @return The SQL to drop a table.
     */
    public static final String drop(Procedure procedure) {
        StringBuffer result = new StringBuffer();
        result.append("DROP PROCEDURE IF EXISTS ");
        result.append(toName(procedure.getName()));
        return result.toString();
    }

    /**
     * Generate the DROP statement for a table.
     * 
     * @param database
     * @param table
     *            The name of the table to drop.
     * @return The SQL to drop a table.
     */
    public static final String drop(Table table) {
        StringBuffer result = new StringBuffer();
        result.append("DROP TABLE ");
        result.append(toSQLTableName(table));
        return result.toString();
    }

    /**
     * Generate the DROP statement for a table.
     * 
     * @param database
     * @param table
     *            The name of the table to drop.
     * @return The SQL to drop a table.
     */
    public static final String drop(Table table, Column column) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(toSQLTableName(table));
        result.append(" DROP COLUMN ");
        result.append(toName(column.getName()));
        return result.toString();
    }

    /**
     * Generate the DROP statement for a table.
     * 
     * @param database
     * @param table
     *            The name of the table to drop.
     * @return The SQL to drop a table.
     */
    public static final String drop(Table table, Index index) {
        StringBuffer result = new StringBuffer();
        result.append("DROP INDEX ");
        result.append(toName(index.getName()));
        result.append(" ON ");
        result.append(toSQLTableName(table));
        return result.toString();
    }

    /**
     * Generate the create statement to create the specified table.
     * 
     * @param database
     * @param table
     *            The table to generate a create statement for.
     * @return The create table statement.
     * @throws Exception
     *             If a database error occurs.
     */
    public static final String create(Table table) throws Exception {
        StringBuffer result = new StringBuffer();

        result.append("CREATE TABLE ");
        result.append(toSQLTableName(table));
        result.append(" ( ");

        for (int i = 0; i < table.getColumns().size(); i++) {
            if (i != 0) {
                result.append(',');
            }
            Column column = table.getColumns().get(i);

            String dataType = column.getTypeName();

            result.append(column.getName());
            result.append(' ');
            result.append(dataType);

            int precision = column.getColumnSize();
            int scale = column.getDecimalDigits();
            if (precision < 65535) {
                result.append('(');
                result.append(precision);
                if (scale > 0) {
                    result.append(',');
                    result.append(scale);
                }
                result.append(") ");
            } else {
                result.append(' ');
            }

            if (!column.isSigned()) {
                result.append("UNSIGNED ");
            }

            if (!column.isNullable()) {
                result.append("NOT NULL ");
            } else {
                result.append("NULL ");
            }
            if (column.isAutoIncrement()) {
                result.append(" auto_increment");
            }
        }

        boolean first = true;
        for (PrimaryKey key : table.getPrimaryKeys()) {
            if (first) {
                first = false;
                result.append(',');
                result.append("PRIMARY KEY(");
            } else {
                result.append(",");
            }
            result.append(key.getColumnName());
        }

        if (!first) {
            result.append(')');
        }

        result.append(" ); ");

        return result.toString();
    }

    /**
     * Generate the DROP statement for a table.
     * 
     * @param database
     * @param table
     *            The name of the table to drop.
     * @return The SQL to drop a table.
     */
    public static final String rename(Table table, String name) {
        StringBuffer result = new StringBuffer();
        result.append("RENAME TABLE ");
        result.append(toSQLTableName(table));
        result.append(" TO ");
        result.append(toSQLTableName(table, name));
        return result.toString();
    }

    /**
     * Generate the DROP statement for a table.
     * 
     * @param database
     * @param table
     *            The name of the table to drop.
     * @return The SQL to drop a table.
     */
    public static final String clean(Table table) {
        StringBuffer result = new StringBuffer();
        result.append("DELETE FROM ");
        result.append(toSQLTableName(table));
        return result.toString();
    }

    /**
     * Generate the DROP statement for a table.
     * 
     * @param database
     * @param table
     *            The name of the table to drop.
     * @return The SQL to drop a table.
     */

    // TODO fix this delete certain rows, not all.
    public static final String clean(Database database, Table table, List<Row> rows) {
        StringBuffer result = new StringBuffer();
        result.append("DELETE FROM ");
        result.append(toSQLTableName(table));
        return result.toString();
    }

    /**
     * Generate the DROP statement for a table.
     * 
     * @param database
     * @param table
     *            The name of the table to drop.
     * @return The SQL to drop a table.
     */
    public static final String delete(Table table, Row row) {
        StringBuffer result = new StringBuffer();
        result.append("DELETE FROM ");
        result.append(toSQLTableName(table));

        boolean isFirst = true;
        for (String primaryKey : table.listPrimaryKeys()) {
            Column column = table.findColumn(primaryKey);
            if (isFirst) {
                result.append(" WHERE ");
            } else {
                result.append(", ");
            }
            isFirst = false;

            result.append(primaryKey);
            result.append("=");
            result.append(getValueForSQL(column, row.get(primaryKey)));
        }
        return result.toString();
    }

    /**
     * Generate the DROP statement for a table.
     * 
     * @param database
     * @param table
     *            The name of the table to drop.
     * @return The SQL to drop a table.
     */
    public static final String delete(Table table, List<Row> rows) {
        StringBuffer result = new StringBuffer();
        result.append("DELETE FROM ");
        result.append(toSQLTableName(table));
        return result.toString();
    }

    public static final String insertSQL(Table table, Row row) throws Exception {

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(toSQLTableName(table));
        sql.append(" (");

        boolean first = true;
        for (String name : row.keySet()) {
            Column columnInfo = table.findColumn(name);
            if (columnInfo.isAutoIncrement() && row.get(name) == null) {
                continue;
            }
            if (!first) {
                sql.append(",");
            }
            first = false;
            sql.append(toName(name));
        }

        sql.append(") values (");

        first = true;
        for (String name : row.keySet()) {
            Column columnInfo = table.findColumn(name);
            if (columnInfo.isAutoIncrement() && row.get(name) == null) {
                continue;
            }
            if (!first) {
                sql.append(",");
            }
            first = false;

            sql.append(getValueForSQL(columnInfo, row.get(name)));
        }

        sql.append(") ON DUPLICATE KEY UPDATE ");

        first = true;
        for (String name : row.keySet()) {
            Column columnInfo = table.findColumn(name);
            if (columnInfo.isAutoIncrement() && row.get(name) == null) {
                continue;
            }
            if (!first) {
                sql.append(",");
            }
            first = false;

            sql.append(name);
            sql.append("=");
            sql.append(getValueForSQL(columnInfo, row.get(name)));
        }

        return sql.toString();
    }

    public static final List<String> insertSQL(Table table, List<Row> rows) throws Exception {

        List<String> sqls = new ArrayList<String>();

        for (Row row : rows) {
            sqls.add(insertSQL(table, row));
        }
        return sqls;
    }

    public static final List<String> persist(Table table) throws Exception {

        List<String> sqls = new ArrayList<String>();

        for (Row row : table.getRows()) {
            sqls.add(insertSQL(table, row));
        }
        return sqls;
    }

    /**
     * 
     * @param rows1
     * @param rows2
     * @param columnName1
     * @param columnName2
     * @return
     * @throws Exception
     */

    public static final List<Row> join(List<Row> rows1, List<Row> rows2, String columnName1, String columnName2)
            throws Exception {
        List<Row> rows = new ArrayList<Row>();
        for (Row row1 : rows1) {
            Object value1 = row1.getValue(columnName1);
            for (Row row2 : rows2) {
                Object value2 = row2.getValue(columnName2);
                if ((value1 == null && value2 == null) || (value1 != null && value1.equals(value2))) {
                    Row row = new Row();
                    row.putAll(row1);
                    row.putAll(row2);
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    public static final String getValueForSQL(Column column, Object value) {
        Object o = "";
        if (value == null) {
            o = "NULL";
        } else if (value instanceof Boolean) {
            o = fromBoolean(value);
        } else if (value instanceof Number) {
            o = fromNumber(column, value);
        } else if (value instanceof Byte[]) {
            o = fromBytes(value);
        } else if (value instanceof Date) {
            o = (value == null) ? null : "'" + value + "'";
        } else if (value instanceof Time) {
            o = (value == null) ? null : "'" + value + "'";
        } else if (value instanceof Timestamp) {
            o = (value == null) ? null : "'" + value + "'";
        } else if (value instanceof String) {
            String str = (String) value;
            if (column.getColumnSize() > 0 && str.length() > column.getColumnSize()) {
                // TODO Warning here
                str = str.substring(0, column.getColumnSize());
            }
            o = "'" + escape(str) + "'";
        } else {
            o = value;
        }
        return o.toString();
    }

    private static final Object fromBoolean(Object value) {
        if (value == null || !(value instanceof Boolean)) {
            return value;
        }
        if (value instanceof Boolean && (Boolean) value) {
            return 1;
        } else if (value instanceof Boolean && !(Boolean) value) {
            return 0;
        }
        return value;
    }

    private static final Object fromNumber(Column column, Object value) {
        if (value == null || !(value instanceof Number)) {
            return value;
        }
        return value;
    }

    private static final Object fromBytes(Object value) {
        if (value == null || !(value instanceof Byte[])) {
            return value;
        }
        Byte[] bytes = (Byte[]) value;
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(Integer.toHexString((int) (b & 0xff)));
        }
        return sb.toString();
    }

    private static final String escape(String s) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\'') {
                buf.append("\\'");
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    public static final List<String> listTableTypes() {
        String[] list = new String[] { "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS",
                "SYNONYM" };
        return new ArrayList<String>(Arrays.asList(list));
    }

    public static final void debug(String databasename, String tablename, List<Row> columnInfo) {
        if (columnInfo == null || columnInfo.size() == 0) {
            System.out.println("No rows found for table: " + tablename);
            return;
        }
        System.out.println("Start Code Snippet ---------------");
        for (Row column : columnInfo) {
            System.out.println(tablename + ".setValue(\"" + column.getValue("column_name") + "\", \"\");");
        }
        System.out.println("End Code Snippet -----------------");
    }
}
