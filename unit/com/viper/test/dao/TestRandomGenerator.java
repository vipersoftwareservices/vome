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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.viper.test.AbstractTestCase;
import com.viper.vome.dao.Column;
import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Connections;
import com.viper.vome.dao.Database;
import com.viper.vome.dao.RandomGenerator;
import com.viper.vome.dao.Table;

public class TestRandomGenerator extends AbstractTestCase {

    private static final String CONNECTION_NAME = "test";
    private static final String DATABASE_TEST_NAME = "test";
    private static final String TABLE_NAME = "organization";

    @BeforeClass
    public static void initializeClass() throws Exception {

        Logger.getGlobal().setLevel(Level.INFO);
    }

    private Table loadTestTable() throws Exception {

        Connection connection = Connections.openConnection(CONNECTION_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", connection ", connection);

        connection.loadDatabases();
        Assert.assertTrue(getCallerMethodName() + ", getDatabases ", connection.getDatabases().size() > 0);

        Database database = connection.findDatabase(DATABASE_TEST_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", connection ", database);

        connection.loadTables(database);
        Assert.assertTrue(getCallerMethodName() + ", tables ", database.getTables().size() > 0);

        Table table = database.findTable(TABLE_NAME);
        Assert.assertNotNull(getCallerMethodName() + ", table ", table);

        connection.loadTable(table);
        connection.loadRows(table);

        connection.close();

        return table;
    }

    @Test
    public void testRandomGenerator() throws Exception {

        Table table = loadTestTable();
        Assert.assertNotNull(getCallerMethodName() + ", table ", table);

        for (Column column : table.getColumns()) {
            if (column.isAutoIncrement()) {
                continue;
            }
            Object value = RandomGenerator.generateRandomValue(column);
            System.out.println(getCallerMethodName() + ", name=" + column.getName() + "=" + value);
            Assert.assertNotNull(getCallerMethodName() + ", value " + column.getName(), value);
        }
    }
}
