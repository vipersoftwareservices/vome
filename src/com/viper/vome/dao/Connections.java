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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.viper.vome.beans.JAXBUtils;
import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.model.DatabaseConnections;

public class Connections { 

    private static final List<Connection> connections = new ArrayList<Connection>();
    private static DatabaseConnections databaseConnections = new DatabaseConnections();

    public static String getFilename() { 
        return databaseConnections.getFilename();
    }

    public static final void openDatabaseConnections(String filename) throws Exception {
        if (filename != null) {
            File file = new File(filename);
            databaseConnections = JAXBUtils.getObject(DatabaseConnections.class, file);
        } else {
            databaseConnections = new DatabaseConnections();
        }
        databaseConnections.setFilename(filename);
    }

    public static final void saveDatabaseConnections(String filename) throws Exception {
        if (filename == null) {
            filename = getFilename();
        }
        if (filename == null) {
            throw new Exception("Please select a data source connection.");
        }
        JAXBUtils.marshal(new File(filename), databaseConnections, null);
    }

    public static final void closeDatabaseConnections() throws Exception {
        if (databaseConnections.getFilename() == null) {
            throw new Exception("Please select a data source connection.");
        }
        String filename = databaseConnections.getFilename();
        JAXBUtils.marshal(new File(filename), databaseConnections, null);

        openDatabaseConnections(null);
    }

    public static final DatabaseConnections getDatabaseConnections() {
        return databaseConnections;
    }

    public static void setDatabaseConnections(DatabaseConnections databaseConnections) {
        Connections.databaseConnections = databaseConnections;
    }

    /**
     * 
     * @param dbc
     * @return
     * @throws Exception
     */

    public static final Connection openConnection(DatabaseConnection dbc) throws Exception {

        Connection connection = new Connection(dbc);
        connection.connect();

        connections.add(connection);
        return connection;
    }

    /**
     * 
     * @param dbc
     * @return
     * @throws Exception
     */

    public static final Connection openConnection(String name) throws Exception {

        DatabaseConnection dbc = findDatabaseConnection(name);

        Connection connection = new Connection(dbc);
        connection.connect();

        connections.add(connection);
        return connection;
    }

    /**
     * 
     * @return
     */
    public static final List<Connection> getConnections() {
        return connections;
    }

    public static final boolean connectionExists(String name) {
        return (findConnection(name) != null);
    }

    public static final Connection findConnection(String name) {
        for (Connection connection : connections) {
            if (name.equalsIgnoreCase(connection.getName())) {
                return connection;
            }
        }
        return null;
    }

    /**
     * 
     * @return
     */
    public static final List<DatabaseConnection> getDatabaseConnectionsList() {
        return databaseConnections.getConnections();
    }

    public static final boolean databaseConnectionExists(String name) {
        return (findDatabaseConnection(name) != null);
    }

    public static final DatabaseConnection findDatabaseConnection(String name) {
        for (DatabaseConnection databaseConnection : databaseConnections.getConnections()) {
            if (name.equalsIgnoreCase(databaseConnection.getName())) {
                return databaseConnection;
            }
        }
        return null;
    }
}
