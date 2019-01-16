/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2003/06/15
 *
 * Copyright 1998-2003 by Viper Software Services
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Viper Software Services. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Viper Software Services.
 *
 * @author Tom Nevin (TomNevin@pacbell.net)
 *
 * @version 1.0, 06/15/2003 
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.vome.converters;

import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Database;
import com.viper.vome.dao.Row;
import com.viper.vome.dao.Table;
import com.viper.vome.model.Selections;
import com.viper.vome.util.FileUtil;

public class ConverterCSV implements ConverterInterface {

    public void exportDatabase(Connection connection, Selections selections) throws Exception {

        for (String databaseName : selections.getDatabaseNames()) {
            for (String tableName : selections.getTableNames()) {

                Database database = connection.findDatabase(databaseName);
                if (database != null) {
                    Table table = database.findTable(tableName);
                    if (table != null) {

                        exportTableAsCSV(connection, table, selections);
                    }
                }
            }
        } 
    }

    public void importDatabase(Connection connection, Selections selections) throws Exception {

        for (String databaseName : selections.getDatabaseNames()) {
            for (String tableName : selections.getTableNames()) {

                Database database = connection.findDatabase(databaseName);
                if (database != null) {
                    Table table = database.findTable(tableName);
                    if (table != null) {

                        importTableAsCSV(connection, table, selections);
                    }
                }
            }
        }
    }

    /**
     * Given the dao interface, read the data corresponding to the class object, and write to a file
     * in the outdir directory.
     * 
     * @param dao
     *            the object for read/writing to/from the database.
     * @param clazz
     *            the class of the bean matching the CSV data, should contain database annotations.
     * @param filename
     *            the filename which will contain the CSV formatted data.
     * 
     * @throws Exception
     *             the transfer from database failed, failure to get table data, no database, no
     *             table, bad connection, etc. Or the transfer to file failed.
     * @note the org.apache.commons.csv is used to write the files.
     */

    private final void exportTableAsCSV(Connection connection, Table table, Selections selections) throws Exception {

        List<Row> items = connection.load(table);

        String filename = toFilename(table, selections);
        new File(filename).getAbsoluteFile().getParentFile().mkdirs();
        PrintStream out = new PrintStream(new File(filename));
        List<String> header = new ArrayList<String>();

        boolean isFirst = true;
        for (Row row : items) {
            if (row == null) {
                continue;
            }
            if (isFirst) {
                for (String columnName : row.keySet()) {
                    header.add(columnName);
                }
                out.println(CSVFormat.EXCEL.withIgnoreSurroundingSpaces().format(header.toArray()));
            }
            isFirst = false;

            List<Object> data = new ArrayList<>();
            for (String columnName : header) {
                data.add(row.get(columnName));
            }
            out.println(CSVFormat.EXCEL.format(data.toArray()));
        }
        out.flush();
        out.close();
    }

    /**
     * Given the dao interface, and the model of the database, transfer the data from the CSV file
     * to the database.
     * 
     * @param dao
     *            the object for read/writing to/from the database.
     * @param clazz
     *            the class of the bean matching the CSV data, should contain database annotations.
     * @param filename
     *            the filename of the location of the CSV data to import
     * 
     * @throws Exception
     *             the transfer to database failed, failure to get table data, no database, no
     *             table, bad connection, etc.
     * @note org.apache.commons.csv is used to read the files.
     */
    private final void importTableAsCSV(Connection connection, Table table, Selections selections) throws Exception {

        List<String> header = new ArrayList<String>();
 
            String filename = toFilename(table, selections);
            String str = FileUtil.readFile(filename);
            if (str == null || str.length() == 0) {
                return;
            }

            List<Row> rows = new ArrayList<Row>();
            StringReader reader = new StringReader(str);
            Iterator<CSVRecord> iterator = CSVFormat.DEFAULT.withIgnoreSurroundingSpaces().parse(reader).iterator();
            while (iterator.hasNext()) {
                CSVRecord result = iterator.next();

                if (header.size() == 0) {
                    for (int i = 0; i < result.size(); i++) {
                        header.add(result.get(i));
                    }

                } else {
                    Row row = new Row();
                    for (int i = 0; i < result.size(); i++) {
                        String key = header.get(i);
                        String value = result.get(i);
                        row.put(key, value);
                    }
                    rows.add(row);
                }
            }

            connection.exeuteInsertUpdate(table, rows);
 
    }

    private final String toFilename(Table table, Selections selections) {
        String basename = selections.getFilename();
        return basename + "/" + table.getDatabaseName() + "/" + table.getName();

    }

}
