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

import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.security.Encryptor;

public class Connection {

    private java.sql.Connection connection;

    private final List<Database> databases = new ArrayList<Database>();
    private final List<User> users = new ArrayList<User>();
    private DatabaseConnection databaseConnection = new DatabaseConnection();

    public Connection(DatabaseConnection dbc) {
        this.databaseConnection = dbc;
    }

    public String getName() {
        return databaseConnection.getName();
    }

    public final List<Database> getDatabases() {
        return databases;
    }

    public final boolean databaseExists(String name) {
        return (findDatabase(name) != null);
    }

    public final Database findDatabase(String name) {
        for (Database database : databases) {
            if (name.equalsIgnoreCase(database.getName())) {
                return database;
            }
        }
        return null;
    }

    public final List<User> getUsers() {
        return users;
    }

    public final boolean userExists(String name) {
        return (findUser(name) != null);
    }

    public final User findUser(String name) {
        for (User user : users) {
            if (name.equalsIgnoreCase(user.getName())) {
                return user;
            }
        }
        return null;
    }

    public final List<String> listDatabaseNames() {
        List<String> list = new ArrayList<String>();
        for (Database database : databases) {
            list.add(database.getName());
        }
        return list;
    }

    /**
     * Open a connection to the database.
     * 
     * @param driver
     *            The database driver to use.
     * @param url
     *            The database connection URL to use.
     * @param username
     * @param password
     * @throws Exception
     *             Thrown if an error occurs while connecting.
     */
    public void connect() throws Exception {
        try {
            DatabaseConnection dbc = databaseConnection;
            Class.forName(dbc.getDriver()).newInstance();

            String decPassword = new Encryptor().decryptPassword(dbc.getPassword());
            connection = DriverManager.getConnection(dbc.getDatabaseUrl(), dbc.getUsername(), decPassword);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * Called to close the database.
     * 
     * @throws Exception
     *             Thrown if the connection cannot be closed.
     */
    public void close() throws Exception {
        try {
            connection.close();
        } catch (SQLException e) {
            throw (new Exception(e));
        }
    }

    /**
     * Check to see if the specified type is numeric.
     * 
     * @param type
     *            The type to check.
     * @return Returns true if the type is numeric.
     */
    public boolean isNumeric(int type) {
        if (type == java.sql.Types.BIGINT || type == java.sql.Types.DECIMAL || type == java.sql.Types.DOUBLE
                || type == java.sql.Types.FLOAT || type == java.sql.Types.INTEGER || type == java.sql.Types.NUMERIC
                || type == java.sql.Types.SMALLINT || type == java.sql.Types.TINYINT) {
            return true;
        }
        return false;

    }

    private List<String> convertResultSetToColumnNames(ResultSet rs) throws Exception {

        ResultSetMetaData rsmd = rs.getMetaData();

        List<String> columnNames = new ArrayList<String>(rsmd.getColumnCount());
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            columnNames.add(rsmd.getColumnName(i + 1));
        }

        return columnNames;
    }

    public static final List<Column> convertRowToColumns(Row row) throws Exception {

        List<Column> columns = new ArrayList<Column>();
        for (String name : row.keySet()) {
            Column column = new Column();
            column.setName(name);
            columns.add(column);
        }
        return columns;
    }

    private List<Row> convertResultSetToRows(ResultSet rs) throws Exception {
        List<Row> rows = new ArrayList<Row>();

        ResultSetMetaData rsmd = rs.getMetaData();

        List<String> columnNames = new ArrayList<String>(rsmd.getColumnCount());
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            columnNames.add(rsmd.getColumnName(i + 1));
        }

        while (rs.next()) {
            Row row = new Row();
            for (int i = 0; i < columnNames.size(); i++) {
                row.setValue(columnNames.get(i), rs.getObject(i + 1));
            }
            rows.add(row);
        }

        return rows;
    }

    /**
     * Execute a SQL query and return a ResultSet.
     * 
     * @param sql
     *            The SQL query to execute.
     * @return The ResultSet generated by the query.
     * @throws Exception
     *             If a database error occurs.
     */
    public ResultSet executeQuery(String sql) throws Exception {
        Statement stmt = null;

        try {
            stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            throw (new Exception(sql, e));
        }
    }

    /**
     * 
     * @param sql
     *            The SQL query to execute.
     * @return
     * @throws Exception
     */

    public final List<Row> queryAll(Table table) throws Exception {
        return executeQueryRows(Driver.toSelectSQL(table));
    }

    /**
     * 
     * @param sql
     *            The SQL query to execute.
     * @return
     * @throws Exception
     */

    public final Row singleQuery(Table table, Column column, Object value) throws Exception {
        return executeQueryRow(Driver.toSelectSQL(table, column, value));
    }

    /**
     * 
     * @param sql
     *            The SQL query to execute.
     * @return
     * @throws Exception
     */

    public final List<Row> queryMany(Table table, Column column, Object value) throws Exception {
        return executeQueryRows(Driver.toSelectSQL(table, column, value));
    }

    /**
     * 
     * @param sql
     *            The SQL query to execute.
     * @return
     * @throws Exception
     */

    public Row executeQueryRow(String sql) throws Exception {
        ResultSet rs = null;
        try {
            rs = executeQuery(sql);
            List<Row> rows = convertResultSetToRows(rs);
            return (rows.size() == 0) ? new Row() : rows.get(0);
        } catch (SQLException e) {
            throw new Exception(e);
        } finally {
            close(rs);
        }
    }

    /**
     * 
     * @param sql
     *            The SQL query to execute.
     * @return
     * @throws Exception
     */

    public List<Row> executeQueryRows(String sql) throws Exception {
        List<Row> rows = new ArrayList<Row>();
        ResultSet rs = null;
        try {
            System.out.println("executeQueryRows: " + sql);
            rs = executeQuery(sql);
            rows = convertResultSetToRows(rs);
        } catch (SQLException e) {
            throw new Exception(e);
        } finally {
            close(rs);
        }
        return rows;
    }

    private void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
    }

    /**
     * 
     */

    public final void create(Database database) throws Exception {

        execute(Driver.create(database));

        getDatabases().add(database);
    }

    /**
     * 
     */

    public final void drop(Database database) throws Exception {

        execute(Driver.drop(database));

        getDatabases().remove(database);
    }

    /**
     * 
     */

    public final void create(Table table) throws Exception {

        execute(Driver.create(table));

        // getDatabases().remove(database);
    }

    /**
     * 
     */

    public final void drop(Database database, Table table) throws Exception {

        execute(Driver.drop(table));

        database.getTables().remove(table);
    }

    /**
     * 
     */

    public final void delete(Database database, Table table) throws Exception {

        execute(Driver.clean(table));
        table.getRows().clear();

    }

    /**
     * 
     */

    public final void delete(Database database, Table table, List<Row> rows) throws Exception {

        execute(Driver.clean(database, table, rows));

        // getDatabases().remove(database);
    }

    /**
     * 
     */

    public final void rename(Table table, String name) throws Exception {
        execute(Driver.rename(table, name));
    }

    public void clean(Table table) throws Exception {

        loadColumns(table);

        execute(Driver.clean(table));
    }

    public void clean(Table table, String idColumn, String parentColumn) throws Exception {

        loadColumns(table);

        try {
            execute(Driver.clean(table));
            return;
        } catch (Exception e) {
            ;
        }
    }

    /**
     * 
     */

    public final void addColumn(Table table, Column column) throws Exception {

    }

    /**
     * 
     */

    public final void createProcedure(Procedure procedure) throws Exception {

    }

    /**
     * 
     */

    public final void createIndex(Index index) throws Exception {

    }

    /**
     * 
     */

    public final void createForeignKey(ForeignKey foreignKey) throws Exception {

    }

    /**
     * 
     */

    public final void createUser(User user) throws Exception {
        execute(Driver.create(user));

        users.add(user);
    }

    /**
     * 
     */

    public final void dropColumn(Table table, Column column) throws Exception {
        execute(Driver.drop(table, column));
    }

    /**
     * 
     */

    public final void dropProcedure(Procedure procedure) throws Exception {

    }

    /**
     * 
     */

    public final void dropIndex(Table table, Index index) throws Exception {
        execute(Driver.drop(table, index));

        table.getIndicies().remove(index);
    }

    /**
     * 
     */

    public final void dropForeignKey(ForeignKey foreignKey) throws Exception {

    }

    /**
     * 
     */

    public final void dropUser(User user) throws Exception {

    }

    /**
     * 
     */

    public final void renameColumn(Column column, String name) {

    }

    /**
     * 
     */

    public final void renameIndex(Index index, String name) {

    }

    /**
     * 
     */

    public final void alterColumn(Table table, Column oldColumn, Column newColumn) {

    }

    /**
     * 
     */

    public final void deleteRow(Table table, Row row) throws Exception {

        execute(Driver.delete(table, row));
    }

    public List<Row> loadCollations() throws Exception {
        return load(Driver.toPossibleCollationsSQL());
    }

    public List<Row> loadCharacterSets() throws Exception {
        return load(Driver.toPossibleCharacterSetsSQL());
    }

    public void exeuteInsertUpdate(Table table, List<Row> rows) throws Exception {

        execute(Driver.insertSQL(table, rows));
    }

    /**
     * Execute a INSERT, DELETE, UPDATE, or other statement that does not return a ResultSet.
     * 
     * @param sql
     *            The query to execute.
     * @throws Exception
     *             If a database error occurs.
     */
    public void execute(String sql) throws Exception {
        Statement stmt = null;
        try {
            System.err.println("execute: " + sql);

            stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            throw (new Exception(sql, e));
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    /**
     * Execute a INSERT, DELETE, UPDATE, or other statement that does not return a ResultSet.
     * 
     * @param sql
     *            The query to execute.
     * @throws Exception
     *             If a database error occurs.
     */
    public void execute(List<String> sqls) throws Exception {
        Statement stmt = null;
        try {

            System.err.println("execute: batch of n commands: " + sqls.size());
            stmt = connection.createStatement();
            for (String sql : sqls) {
                System.err.println("execute: batch [" + sql);
                stmt.addBatch(sql);
            }
            stmt.executeBatch();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    /**
     * Execute a INSERT, DELETE, UPDATE, or other statement that does not return a ResultSet.
     * 
     * @throws Exception
     *             If a database error occurs.
     */
    public void commit() throws Exception {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw (new Exception(e));
        }
    }

    /**
     * Get a list of all tables in the database.
     * 
     * @return A list of all tables in the database.
     * @throws Exception
     *             If a database error occurs.
     */
    public final void loadDatabases() throws Exception {

        getDatabases().clear();

        ResultSet rs = null;
        try {
            DatabaseMetaData dbm = connection.getMetaData();

            rs = dbm.getSchemas();
            if (!rs.first()) {
                close(rs);
                rs = dbm.getCatalogs();
            }

            getDatabases().clear();
            List<Row> rows = convertResultSetToRows(rs);
            for (Row row : rows) {
                getDatabases().add(new Database(row));
            }

            // Driver.debug("databases", "<metadata>", rows);

        } finally {
            close(rs);
        }
    }

    /**
     * Get a list of all tables in the database.
     * 
     * @return A list of all tables in the database.
     * @throws Exception
     *             If a database error occurs.
     */
    public void loadTables(Database database) throws Exception {
        ResultSet rs = null;

        try {
            DatabaseMetaData dbm = connection.getMetaData();

            rs = dbm.getTables(database.getName(), null, "%", null);

            database.getTables().clear();

            List<Row> rows = convertResultSetToRows(rs);
            for (Row row : rows) {
                database.getTables().add(new Table(row));
            }
        } finally {
            close(rs);
        }
    }

    /**
     * Get a list of all of the columns on a table.
     * 
     * @param table
     *            The table to check.
     * @return A list of all of the columns.
     * @throws Exception
     *             If a database error occurs.
     */
    public void loadView(Table table) throws Exception {

        Row row = executeQueryRow(Driver.toTableViewSQL(table));

        table.updateInfo(row);
    }

    /**
     * Get a list of all tables in the database.
     * 
     * @return A list of all tables in the database.
     * @throws Exception
     *             If a database error occurs.
     */
    public void loadTable(Table table) throws Exception {
        loadColumns(table);
        loadPrimaryKeys(table);
        loadForeignKeys(table);
        loadIndicies(table);
        loadView(table);
    }

    /**
     * Get a list of all of the columns on a table.
     * 
     * @param table
     *            The table to check.
     * @return A list of all of the columns.
     * @throws Exception
     *             If a database error occurs.
     */
    public void loadColumns(Table table) throws Exception {
        ResultSet rs = null;

        try {
            DatabaseMetaData dbm = connection.getMetaData();
            rs = dbm.getColumns(table.getDatabaseName(), null, table.getName(), "%");

            Column.getColumnNames().clear();
            Column.getColumnNames().addAll(convertResultSetToColumnNames(rs));

            table.getColumns().clear();
            List<Row> rows = convertResultSetToRows(rs);
            for (Row row : rows) {
                table.getColumns().add(new Column(row));
            }
        } finally {
            close(rs);
        }
    }

    /**
     * Get a list of all of the columns on a table.
     * 
     * @param table
     *            The table to check.
     * @return A list of all of the columns.
     * @throws Exception
     *             If a database error occurs.
     */
    public void loadPrimaryKeys(Table table) throws Exception {
        ResultSet rs = null;
        try {

            DatabaseMetaData dbm = connection.getMetaData();
            rs = dbm.getPrimaryKeys(table.getDatabaseName(), null, table.getName());

            table.getPrimaryKeys().clear();
            List<Row> rows = convertResultSetToRows(rs);
            for (Row row : rows) {
                table.getPrimaryKeys().add(new PrimaryKey(row));
            }
        } finally {
            close(rs);
        }
    }

    /**
     * Get a list of all of the columns on a table.
     * 
     * @param table
     *            The table to check.
     * @return A list of all of the columns.
     * @throws Exception
     *             If a database error occurs.
     */
    public void loadForeignKeys(Table table) throws Exception {
        ResultSet rs = null;
        try {

            DatabaseMetaData dbm = connection.getMetaData();
            rs = dbm.getExportedKeys(table.getDatabaseName(), null, table.getName());

            ForeignKey.getColumnNames().clear();
            ForeignKey.getColumnNames().addAll(convertResultSetToColumnNames(rs));

            table.getForeignKeys().clear();
            List<Row> rows = convertResultSetToRows(rs);
            for (Row row : rows) {
                table.getForeignKeys().add(new ForeignKey(row));
            }
        } finally {
            close(rs);
        }
    }

    /**
     * Get a list of all of the columns on a table.
     * 
     * @param table
     *            The table to check.
     * @return A list of all of the columns.
     * @throws Exception
     *             If a database error occurs.
     */
    public void loadIndicies(Table table) throws Exception {
        ResultSet rs = null;
        try {

            DatabaseMetaData dbm = connection.getMetaData();
            rs = dbm.getIndexInfo(table.getDatabaseName(), null, table.getName(), false, false);

            table.getIndicies().clear();
            List<Row> rows = convertResultSetToRows(rs);
            for (Row row : rows) {
                table.getIndicies().add(new Index(row));
            }
            System.out.println("LoadIndicies: name/count=" + table.getName() + "," + rows.size());
        } finally {
            close(rs);
        }
    }

    /**
     * Get a list of all tables in the database.
     * 
     * @return A list of all tables in the database.
     * @throws Exception
     *             If a database error occurs.
     */
    public void loadUsers() throws Exception {
        ResultSet rs = null;

        try {

        } finally {
            close(rs);
        }
    }

    /**
     * Get a list of all of the columns on a table.
     * 
     * @param table
     *            The table to check.
     * @return A list of all of the columns.
     * @throws Exception
     *             If a database error occurs.
     */
    public void loadRows(Table table) throws Exception {
        ResultSet rs = null;
        table.getRows().clear();

        try {
            rs = executeQuery(Driver.toSelectSQL(table));

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            List<String> columnNames = new ArrayList<String>(rsmd.getColumnCount());
            for (int i = 0; i < columnCount; i++) {
                columnNames.add(rsmd.getColumnName(i + 1).toLowerCase());
            }

            while (rs.next()) {
                Row row = new Row();
                for (int i = 0; i < columnCount; i++) {
                    row.setValue(columnNames.get(i), rs.getObject(i + 1));
                }
                table.getRows().add(row);
            }
        } catch (SQLException e) {
            throw new Exception(e);
        } finally {
            close(rs);
        }
    }

    /**
     * Get a list of all of the columns on a table.
     * 
     * @param table
     *            The table to check.
     * @return A list of all of the columns.
     * @throws Exception
     *             If a database error occurs.
     */
    public void loadProcedures(Database database) throws Exception {
        ResultSet rs = null;
        try {

            DatabaseMetaData dbm = connection.getMetaData();
            rs = dbm.getProcedures(database.getName(), null, "%");

            database.getProcedures().clear();
            List<Row> rows = convertResultSetToRows(rs);
            for (Row row : rows) {
                database.getProcedures().add(new Procedure(row));
            }
        } finally {
            close(rs);
        }
    }

    /**
     * Get a list of all of the columns on a table.
     * 
     * @param table
     *            The table to check.
     * @return A list of all of the columns.
     * @throws Exception
     *             If a database error occurs.
     */
    public void loadProcedure(Procedure procedure) throws Exception {

        Row row = executeQueryRow(Driver.toProcedureSourceSQL(procedure));

        procedure.update(row);
    }

    /**
     * Execute a SQL query and return a ResultSet.
     * 
     * @param sql
     *            The SQL query to execute.
     * @return The ResultSet generated by the query.
     * @throws Exception
     *             If a database error occurs.
     */
    public long size(String sql) throws Exception {
        long size = -1L;
        ResultSet rs = null;
        try {
            rs = executeQuery(sql);
            while (rs.next()) {
                size = rs.getLong(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw (new Exception(ex));
        } finally {
            close(rs);
        }
        return size;
    }

    public long size(Table table) throws Exception {
        return size(Driver.toSizeSQL(table));
    }

    /**
     * Create a prepared statement.
     * 
     * @param sql
     *            The SQL of the prepared statement.
     * @return The PreparedStatement that was created.
     * @throws Exception
     *             If a database error occurs.
     */
    public PreparedStatement prepareStatement(String sql) throws Exception {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
        } catch (Exception ex) {
            throw ex;
        }
        return statement;
    }

    public List<Row> explain(Table table) throws Exception {
        List<Row> rows = load(Driver.toExplainSQL(table));
        if (rows != null && rows.size() > 0) {
            table.getColumns().addAll(convertRowToColumns(rows.get(0)));
        }
        return rows;
    }

    public List<Row> load(Table table) throws Exception {
        return load(Driver.toSelectSQL(table));
    }

    public List<Row> load(Table table, String whereClause, String selectClause) throws Exception {
        if (selectClause == null) {
            selectClause = "*";
        }
        if (whereClause == null) {
            whereClause = "";
        }
        String sql = "select " + selectClause + " from " + table.getName() + " " + whereClause;

        return load(sql);
    }

    public List<Row> loadPage(Table table, int startRow, int numRowsPerPage) {

        try {
            String sql = Driver.toSelectSQL(table, startRow, numRowsPerPage);
            List<Row> items = load(sql);
            table.getRows().clear();
            table.getRows().addAll(items);
            return items;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<Row>();
    }

    public final List<Row> load(String sql) throws Exception {

        Row perf = SQLHistory.startTimer(sql);

        List<Row> rows = new ArrayList<Row>();
        ResultSet rs = null;
        try {
            rs = executeQuery(sql);

            rows = convertResultSetToRows(rs);

            SQLHistory.stopTimer(perf);
        } catch (Exception e) {
            SQLHistory.stopTimer(perf, e);
            throw e;
        } finally {
            close(rs);
        }
        return rows;
    }

    public void persist(Table table) throws Exception {

        loadColumns(table);

        execute(Driver.persist(table));
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

    public List<Row> join(List<Row> rows1, List<Row> rows2, String columnName1, String columnName2) throws Exception {
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

    public final String toString() {
        return getName();
    }

}
