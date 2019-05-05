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

package com.viper.test.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.viper.test.AbstractTestCase;
import com.viper.vome.Session;
import com.viper.vome.dao.Column;
import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Connections;
import com.viper.vome.dao.Database;
import com.viper.vome.dao.RandomGenerator;
import com.viper.vome.dao.Row;
import com.viper.vome.dao.Table;
import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.model.TableType;

public class TestConnection extends AbstractTestCase {

    private static final Logger log = Logger.getLogger(TestConnection.class.getName());

    private static final String CONNECTION_NAME = "test";
    private static final String DATABASE_TEST_NAME = "test";
    private static final String DATABASE_NAME = "vome";
    private static final String DATABASE2_NAME = "vome2";
    private static final String TABLE_NAME = "organization";

    @BeforeClass
    public static void initializeClass() throws Exception {

        Logger.getGlobal().setLevel(Level.INFO);
    }

    private Connection openConnection() throws Exception {
        Connection connection = Connections.openConnection(CONNECTION_NAME);
        connection.loadDatabases();

        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        connection.loadTables(database);

        Table table = new Table(DATABASE_TEST_NAME, TABLE_NAME);
        if (!database.tableExists(TABLE_NAME)) {
            connection.create(table);
        }
        return connection;
    }

    private List<Row> generateRandomData(Table table, int nrows) {
        List<Row> rows = new ArrayList<Row>();
        for (int i = 0; i < nrows; i++) {
            Row row = new Row();
            for (Column column : table.getColumns()) {
                if (column.isAutoIncrement()) {
                    continue;
                }
                Object value = RandomGenerator.generateRandomValue(column);
                row.put(column.getName(), value);
            }
            rows.add(row);
        }
        return rows;
    }

    private Connection storeRandomData(String databasename, String tablename, int nrows) throws Exception {

        Connections.openDatabaseConnections(Session.getInstance().getDatabasePropertyFilename());
        Connection connection = openConnection();

        connection.loadDatabases();
        Database database = connection.findDatabase(databasename);

        connection.loadTables(database);
        Table table = database.findTable(tablename);

        connection.loadTable(table);

        List<Row> rows = generateRandomData(table, nrows);

        connection.delete(database, table);
        connection.exeuteInsertUpdate(table, rows);

        return connection;
    }

    @Test
    public void testOpenConnection() throws Exception {
        Connection connection = Connections.openConnection(CONNECTION_NAME);

        Assert.assertNotNull(getCallerMethodName() + ", connection ", connection);

        connection.close();
    }

    @Test
    public void testInsertTable() throws Exception {
        Connection connection = Connections.openConnection(CONNECTION_NAME);

        Assert.assertNotNull(getCallerMethodName() + ", connection ", connection);

        connection.loadDatabases();
        Database database = connection.findDatabase(DATABASE_TEST_NAME);

        connection.loadTables(database);
        Table table = database.findTable(TABLE_NAME);

        connection.loadTable(table);

        int nrows = 50;
        List<Row> rows = generateRandomData(table, nrows);

        connection.clean(table);
        connection.exeuteInsertUpdate(table, rows);

        connection.loadRows(table);
        Assert.assertEquals(getCallerMethodName(), nrows, table.getRows().size());

        connection.close();
    }

    @Test
    public void testCleanTable() throws Exception {
        Connection connection = Connections.openConnection(CONNECTION_NAME);

        Assert.assertNotNull(getCallerMethodName() + ", connection ", connection);

        connection.loadDatabases();
        Database database = connection.findDatabase(DATABASE_TEST_NAME);

        connection.loadTables(database);
        Table table = database.findTable(TABLE_NAME);

        connection.loadTable(table);

        int nrows = 50;
        List<Row> rows = generateRandomData(table, nrows);

        connection.exeuteInsertUpdate(table, rows);

        connection.delete(database, table);

        connection.loadRows(table);
        Assert.assertEquals(getCallerMethodName(), 0, table.getRows().size());

        connection.close();
    }

    @Test
    public void testCreateDropDatabase() throws Exception {
        Connection connection = Connections.openConnection(CONNECTION_NAME);

        Assert.assertNotNull(getCallerMethodName() + ", connection ", connection);

        connection.loadDatabases();
        Assert.assertTrue(getCallerMethodName(), connection.getDatabases().size() > 0);

        Database database = connection.findDatabase(DATABASE2_NAME);
        if (database != null) {
            connection.drop(database);
        }

        database = new Database();
        database.setName(DATABASE2_NAME);
        connection.create(database);

        connection.loadDatabases();

        assertNotNull(getCallerMethodName(), connection.findDatabase(DATABASE2_NAME));

        connection.drop(database);

        connection.loadDatabases();

        assertNull(getCallerMethodName(), connection.findDatabase(DATABASE2_NAME));

        connection.close();
    }

    @Test
    public void testLoadDatabaseConnections() throws Exception {

        Connections.openDatabaseConnections(Session.getInstance().getDatabasePropertyFilename());
        Assert.assertNotNull(getCallerMethodName() + ", DatabaseConnections ", Connections.getDatabaseConnections());
        Assert.assertTrue(getCallerMethodName() + ", #DatabaseConnections ", Connections.getDatabaseConnectionsList().size() > 0);

        DatabaseConnection databaseConnection = Connections.findDatabaseConnection(CONNECTION_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", DatabaseConnections (test) ", databaseConnection);

        System.out.println(
                getCallerMethodName() + " number of DatabaseConnections=" + Connections.getDatabaseConnectionsList().size());

    }

    @Test
    public void testLoadDatabases() throws Exception {

        Connection connection = Connections.openConnection(CONNECTION_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", connection ", connection);

        connection.loadDatabases();
        Assert.assertTrue(getCallerMethodName() + ", #databases ", connection.getDatabases().size() > 0);

        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", database (test) ", database);

        System.out.println(getCallerMethodName() + " number of databases=" + connection.getDatabases().size());

        connection.close();
    }

    @Test
    public void testLoadTables() throws Exception {

        Connection connection = Connections.openConnection(CONNECTION_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", connection ", connection);

        connection.loadDatabases();
        Assert.assertTrue(getCallerMethodName() + ", #databases ", connection.getDatabases().size() > 0);

        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", database (test) ", database);

        connection.loadTables(database);
        Assert.assertTrue(getCallerMethodName() + ", #tables ", database.getTables().size() > 0);

        System.out.println(getCallerMethodName() + " number of tables=" + database.getTables().size());

        connection.close();
    }

    @Test
    public void testLoadProcedures() throws Exception {

        Connection connection = Connections.openConnection(CONNECTION_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", connection ", connection);

        connection.loadDatabases();
        Assert.assertTrue(getCallerMethodName() + ", #databases ", connection.getDatabases().size() > 0);

        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", database (test) ", database);

        connection.loadProcedures(database);
        Assert.assertTrue(getCallerMethodName() + ", #procedures ", database.getProcedures().size() > 0);

        System.out.println(getCallerMethodName() + " number of procedures=" + database.getProcedures().size());

        connection.close();
    }

    @Test
    public void testLoadColumns() throws Exception {

        Connection connection = Connections.openConnection(CONNECTION_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", connection ", connection);

        connection.loadDatabases();
        Assert.assertTrue(getCallerMethodName() + ", #databases ", connection.getDatabases().size() > 0);

        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", databas ", database);

        connection.loadTables(database);
        Assert.assertTrue(getCallerMethodName() + ", #tables ", database.getTables().size() > 0);

        Table table = database.findTable(TABLE_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", table ", table);
        Assert.assertNotNull(getCallerMethodName() + ", table.getName() ", table.getName());
        Assert.assertNotNull(getCallerMethodName() + ", table.getDatabaseName() ", table.getDatabaseName());

        connection.loadColumns(table);
        Assert.assertTrue(getCallerMethodName() + ", #columns ", table.getColumns().size() > 0);

        System.out.println(getCallerMethodName() + " number of columns=" + table.getColumns().size());

        connection.close();
    }

    @Test
    public void testLoadForeignKeys() throws Exception {

        Connection connection = Connections.openConnection(CONNECTION_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", connection ", connection);

        connection.loadDatabases();
        Assert.assertTrue(getCallerMethodName() + ", #databases ", connection.getDatabases().size() > 0);

        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", databas ", database);

        connection.loadTables(database);
        Assert.assertTrue(getCallerMethodName() + ", #tables ", database.getTables().size() > 0);

        Table table = database.findTable("people");
        Assert.assertNotNull(getCallerMethodName() + ", table ", table);

        connection.loadForeignKeys(table);
        Assert.assertEquals(getCallerMethodName() + ", #foreign keys ", 1, table.getForeignKeys().size());

        System.out.println(getCallerMethodName() + " number of foreign keys=" + table.getForeignKeys().size());

        connection.close();
    }

    @Test
    public void testLoadPrimaryKeys() throws Exception {

        Connection connection = Connections.openConnection(CONNECTION_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", connection ", connection);

        connection.loadDatabases();
        Assert.assertTrue(getCallerMethodName() + ", #databases ", connection.getDatabases().size() > 0);

        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", databas ", database);

        connection.loadTables(database);
        Assert.assertTrue(getCallerMethodName() + ", #tables ", database.getTables().size() > 0);

        Table table = database.findTable(TABLE_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", table ", table);

        connection.loadPrimaryKeys(table);
        Assert.assertTrue(getCallerMethodName() + ", #primary key ", table.getPrimaryKeys().size() > 0);

        System.out.println(getCallerMethodName() + " number of primary keys=" + table.getPrimaryKeys().size());

        connection.close();
    }

    @Test
    public void testLoadIndicies() throws Exception {

        Connection connection = Connections.openConnection(CONNECTION_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", connection ", connection);

        connection.loadDatabases();
        Assert.assertTrue(getCallerMethodName() + ", #databases ", connection.getDatabases().size() > 0);

        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", databas ", database);

        connection.loadTables(database);
        Assert.assertTrue(getCallerMethodName() + ", #tables ", database.getTables().size() > 0);

        Table table = database.findTable(TABLE_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", table ", table);

        connection.loadIndicies(table);
        Assert.assertTrue(getCallerMethodName() + ", #indicies ", table.getIndicies().size() > 0);

        System.out.println(getCallerMethodName() + " number of indicies=" + table.getIndicies().size());

        connection.close();
    }

    @Test
    public void testCreate() throws Exception {

        int nrows = 100;
        Connection connection = storeRandomData(DATABASE_TEST_NAME, TABLE_NAME, nrows);
        connection.loadDatabases();
        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        connection.loadTables(database);
        Table table = database.findTable(TABLE_NAME);
        connection.loadRows(table);

        Assert.assertEquals("testCreate - ", nrows, table.getRows().size());

        for (Row row : table.getRows()) {
            Assert.assertNotNull(getCallerMethodName() + ", the organization id not set: " + row.getInt("id"), row.get("id"));
            Assert.assertTrue(getCallerMethodName() + ", the organization id not set: " + row.getInt("id"), row.getInt("id") > 0);
        }
    }

    @Test
    public void testPrimaryKey() throws Exception {

        int nrows = 100;

        Connection connection = storeRandomData(DATABASE_TEST_NAME, TABLE_NAME, nrows);
        Assert.assertNotNull(getCallerMethodName() + ", connection", connection);

        connection.loadDatabases();

        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", database", database);

        connection.loadTables(database);

        Table table = database.findTable(TABLE_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", table", table);

        connection.loadTable(table);
        Assert.assertNotNull(getCallerMethodName(), table.findColumn("id"));
        Assert.assertNotNull(getCallerMethodName(), table.findColumn("name"));

        connection.loadRows(table);
        Assert.assertEquals(getCallerMethodName() + ", rows.size=", nrows, table.getRows().size());

        Object id = table.getRows().get(nrows / 2).getValue("id");

        Row row = connection.singleQuery(table, table.findColumn("id"), id);
        Row expected = table.findRow("id", id);

        Assert.assertNotNull(getCallerMethodName() + ", Organization1", row);
        Assert.assertEquals(getCallerMethodName() + ", Organization2", expected.getString("name"), row.getString("name"));
    }

    @Test
    public void testQuery() throws Exception {

        int nrows = 100;
        Connection connection = storeRandomData(DATABASE_TEST_NAME, TABLE_NAME, nrows);
        connection.loadDatabases();

        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        connection.loadTables(database);

        Table table = database.findTable(TABLE_NAME);
        connection.loadTable(table);
        Assert.assertNotNull(getCallerMethodName(), table.findColumn("id"));
        Assert.assertNotNull(getCallerMethodName(), table.findColumn("name"));

        connection.loadTable(table);
        connection.loadRows(table);

        Object value = table.getRows().get(nrows / 2).get("id");
        Row expected = table.findRow("id", value);
        Row actual = connection.singleQuery(table, table.findColumn("name"), expected.get("name"));

        Assert.assertNotNull(getCallerMethodName() + ", could not find Organization", actual);
        Assert.assertEquals(getCallerMethodName() + ", could not find Organization2", expected.get("name"), actual.get("name"));
    }

    @Test
    public void testList() throws Exception {

        int nrows = 100;
        Connection connection = storeRandomData(DATABASE_TEST_NAME, TABLE_NAME, nrows);

        connection.loadDatabases();
        Database database = connection.findDatabase(DATABASE_TEST_NAME);

        connection.loadTables(database);
        Table table = database.findTable(TABLE_NAME);

        connection.loadTable(table);
        connection.loadRows(table);

        Row expected = table.findRow("id", table.getRows().get(nrows / 2).getValue("id"));
        System.out.println("TESTLIST: NAME=" + expected.get("name"));
        List<Row> actual = connection.queryMany(table, table.findColumn("name"), expected.get("name"));

        Assert.assertNotNull(getCallerMethodName() + ", could not find Organization", actual);
        Assert.assertEquals(getCallerMethodName() + ", could not find Organization", 1, actual.size());
        Assert.assertEquals(getCallerMethodName() + ", could not find Organization2", expected.get("name"),
                actual.get(0).get("name"));
    }

    @Test
    public void testListAll() throws Exception {

        int nrows = 100;
        Connection connection = storeRandomData(DATABASE_TEST_NAME, TABLE_NAME, nrows);
        connection.loadDatabases();
        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        connection.loadTables(database);
        Table table = database.findTable(TABLE_NAME);
        connection.loadRows(table);

        List<Row> actual = connection.queryAll(table);

        Assert.assertNotNull(getCallerMethodName() + ", could not find Organization", actual);
        Assert.assertTrue(getCallerMethodName() + ", could not find Organization: " + actual.size(), actual.size() >= nrows);
    }

    @Test
    public void testUpdateAll() throws Exception {

        int nrows = 100;
        Connection connection = storeRandomData(DATABASE_TEST_NAME, TABLE_NAME, nrows);

        connection.loadDatabases();
        Database database = connection.findDatabase(DATABASE_TEST_NAME);

        connection.loadTables(database);
        Table table = database.findTable(TABLE_NAME);

        connection.loadTable(table);
        connection.loadRows(table);

        List<Row> actual = connection.queryAll(table);

        Assert.assertNotNull(getCallerMethodName() + ", Organization", actual);
        Assert.assertEquals(getCallerMethodName() + ", Organization", actual.size(), nrows);
    }

    @Test
    public void testMasterUpdate() throws Exception {

        int nrows = 10;
        Connection connection = storeRandomData(DATABASE_TEST_NAME, "master_bean", nrows);

        connection.loadDatabases();
        Database database = connection.findDatabase(DATABASE_TEST_NAME);

        connection.loadTables(database);
        Table table = database.findTable("master_bean");

        connection.loadTable(table);
        connection.loadRows(table);

        List<Row> actual = connection.queryAll(table);

        Assert.assertNotNull(getCallerMethodName() + ", Organization", actual);
        Assert.assertEquals(getCallerMethodName() + ", Organization: " + actual.size(), actual.size(), nrows);
    }

    @Test
    public void testSize() throws Exception {

        int expectedSize = 125;
        Connection connection = storeRandomData(DATABASE_TEST_NAME, TABLE_NAME, expectedSize);

        connection.loadDatabases();
        Database database = connection.findDatabase(DATABASE_TEST_NAME);

        connection.loadTables(database);
        Table table = database.findTable(TABLE_NAME);

        connection.loadTable(table);
        connection.loadRows(table);
        Assert.assertEquals(getCallerMethodName() + ", Organization: " + expectedSize, expectedSize, table.getRows().size());

        List<Row> actual = connection.queryAll(table);

        Assert.assertNotNull(getCallerMethodName() + ", Organization", actual);
        Assert.assertEquals(getCallerMethodName() + ", Organization: " + expectedSize, expectedSize, actual.size());
    }

    @Test
    public void testUniqueValues() throws Exception {

        int expectedSize = 125;
        Connection connection = storeRandomData(DATABASE_TEST_NAME, TABLE_NAME, expectedSize);
        connection.loadDatabases();

        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        connection.loadTables(database);

        Table table = database.findTable(TABLE_NAME);
        connection.loadTable(table);
        connection.loadRows(table);

        List<Object> items = table.uniqueValues("name");

        Assert.assertNotNull(getCallerMethodName() + ", could not find Organization: " + items.size(), items);
        Assert.assertTrue(getCallerMethodName() + ", could not find Organization: " + items.size(), items.size() > 0);
    }

    @Test
    public void testQueryUser() throws Exception {

        int expectedSize = 125;
        Connection connection = storeRandomData(DATABASE_TEST_NAME, "user", expectedSize);

        connection.loadDatabases();
        Database database = connection.findDatabase(DATABASE_TEST_NAME);

        connection.loadTables(database);
        Table table = database.findTable("user");

        connection.loadTable(table);
        connection.loadRows(table);

        List<Row> users = generateRandomData(table, 1);
        connection.exeuteInsertUpdate(table, users);
        // connection.create(User.class);
        // connection.insert(expected);
        //
        // User actual = connection.query(User.class, "username", expected.getUsername());
        //
        // Assert.assertNotNull(getCallerMethodName() + ", could not find User", actual);
        // Assert.assertEquals(getCallerMethodName() + ", could not find User.name",
        // expected.getName(), actual.getName());
        // Assert.assertEquals(getCallerMethodName() + ", could not find User.friends.size",
        // expected.getFriends().size(),
        // actual.getFriends().size());
        // Assert.assertEquals("testQuery - could not find User.friends", expected.getFriends(),
        // actual.getFriends());
    }

    @Test
    public void testListDatabases() throws Exception {

        Connection connection = openConnection();
        connection.loadDatabases();
        List<String> items = connection.listDatabaseNames();

        for (String item : items) {
            log.info("testListDatabases: database Name=" + item);
        }

        Assert.assertTrue("testListDatabases - number of database must be > 0: " + items.size(), items.size() > 0);
        Assert.assertTrue("testListDatabases - test: ", items.contains(DATABASE_TEST_NAME));
    }

    @Test
    public void testListTables() throws Exception {

        Connection connection = openConnection();
        connection.loadDatabases();
        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        connection.loadTables(database);

        for (Table item : database.getTables()) {
            log.info("testListTables: table Name=" + item);
        }

        Assert.assertTrue("testListTables - number of tables must be > 0: " + database.getTables().size(),
                database.getTables().size() > 0);
    }

    @Test
    public void testListColumns() throws Exception {

        Connection connection = openConnection();
        connection.loadDatabases();
        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        connection.loadTables(database);
        Table table = database.findTable("Organization");
        connection.loadColumns(table);

        for (Column item : table.getColumns()) {
            log.info("testListColumns: column Name=" + item);
        }

        Assert.assertTrue("testListColumns - number of columns must be > 0: " + table.getColumns().size(),
                table.getColumns().size() > 0);
    }

    @Test
    public void testInitialize() throws Exception {

        DatabaseConnection dbc = new DatabaseConnection();
        dbc.setName("test1");
        dbc.setUsername("root");
        dbc.setPassword("ENC:lvZkFEUgMadIgqV4Kj77Uw==");
        dbc.setModel("etc/model/beans/bean1.xml");
        dbc.setDatabaseUrl("jdbc:mysql://localhost:3306");
        dbc.setDriver("com.mysql.jdbc.Driver");
        dbc.setVendor("mysql");

        Connection connection = openConnection();
        Assert.assertNotNull("testInitialize - the bean1 not set: ", connection);

        // TODO check for table "test1.bean1".
    }

    @Test
    public void testDeleteRow() throws Exception {

        int nrows = 100;
        Connection connection = storeRandomData(DATABASE_TEST_NAME, TABLE_NAME, nrows);

        connection.loadDatabases();
        Database database = connection.findDatabase(DATABASE_TEST_NAME);

        connection.loadTables(database);
        Table table = database.findTable(TABLE_NAME);

        connection.load(table);
        connection.loadRows(table);

        Row row = table.getRows().get(nrows / 3);

        connection.deleteRow(table, row);

        connection.load(table);
        connection.loadRows(table);

        Row actual = table.findRow("id", row.get("id"));

        Assert.assertNull("testQuery -  Organization", actual);
    }

    @Test
    public void testCreateTable() throws Exception {

        Connection connection = openConnection();
        connection.loadDatabases();

        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        connection.loadTables(database);

        Table table = database.findTable("UserTest");
        if (table != null) {
            connection.drop(database, table);
        }

        table = new Table();
        table.setDatabaseName(DATABASE_TEST_NAME);
        table.setName("UserTest");
        table.setTableType(TableType.TABLE);

        Column column = new Column();

    }

    // <?xml version='1.0' encoding='UTF-8'?>
    // <database xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    // xsi:noNamespaceSchemaLocation="http://www.vipersoftwareservices.com/schemas/database.xsd"
    // name='test' package-name="com.viper.unit.model">
    // <table name='User' table-type='table' is-rest-service="true" is-schema-updatable="true">
    // <column name='username' java-type='String' size='32' required='true' primary-key='true'
    // id-method="assigned" logical-type="email:email.txt" />
    // <column name='password' java-type='String' size='32' required='false'
    // logical-type="password:" />
    // <column name='name' java-type='String' size='255' required='false'
    // logical-type="name:firstnames.txt" />
    // <column name='cube' java-type='String' size='32' required='false' />
    // <column name='job' java-type='String' size='32' required='false' />
    // <column name='school' java-type='String' size='255' required='false' />
    // <column name='email' java-type='String' size='255' required='false'
    // logical-type="email:email.txt" />
    // <column name='lastProblem' java-type='String' size='255' required='false'
    // logical-type="name:nouns.csv" />
    // <column name='grade' java-type='int' required='true' logical-type="int:0,12" />
    // <column name='friends' java-type='java.util.List' generic-type="String" required='true'
    // logical-type="email:email.txt" />
    // </table>
    // </database>
}
