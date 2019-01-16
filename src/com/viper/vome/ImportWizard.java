/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2008/06/15
 *
 * Copyright 1998-2008 by Viper Software Services
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
 * @version 1.0, 06/15/2008
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.vome;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.viper.vome.converters.ConverterFactory;
import com.viper.vome.converters.ConverterInterface;
import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Database;
import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.model.FileFormat;
import com.viper.vome.model.Selections;
import com.viper.vome.util.FileUtil;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class ImportWizard extends BorderPane implements PropertyChangeListener {

    protected Selections selector = new Selections();

    public ImportWizard() {

        try {

            Session session = Session.getInstance();

            session.getChangeManager().add(this);

            String filename = System.getProperty("user.dir") + "/" + LocaleUtil.getProperty("default-backup-filename");
            FileUtil.mkPath(filename);
            List<String> filenameList = new ArrayList<String>();

            Connection connection = session.openConnection();
            List<String> names = connection.listDatabaseNames();

            FlowPane headerPane = new FlowPane(5, 5);
            headerPane.getChildren().add(UIUtil.newLabel("Database:"));
            headerPane.getChildren().add(UIUtil.newLabel(session.getDatabaseConnection(), "Name"));

            GridPane centerPane = new GridPane();
            centerPane.setHgap(10); // horizontal gap in pixels => that's what you are asking for
            centerPane.setVgap(10); // vertical gap in pixels
            centerPane.setPadding(new Insets(10, 10, 10, 10));

            add(centerPane, 1, 1, 1, UIUtil.newLabel("Import Filename"));
            add(centerPane, 2, 1, 1, UIUtil.newComboBox(selector, "filename", filenameList, this::BrowseFilename));
            add(centerPane, 3, 1, 1, UIUtil.newButton("Browse", this::BrowseFilename));

            add(centerPane, 1, 2, 1, UIUtil.newLabel("Backup Format: "));
            add(centerPane, 2, 2, 2, UIUtil.newComboBox(selector, "BackupFormat", FileFormat.values()));

            add(centerPane, 1, 3, 1, UIUtil.newLabel("Database Selection:"));
            add(centerPane, 2, 3, 2, UIUtil.newCheckComboBox(connection.getDatabases(), "name", this::SelectDatabasesListener));
            // add(centerPane, 3, 3, 1,
            // UIUtil.newLabel(LocaleUtil.getProperty("ExportWizard.scope.text")));

            add(centerPane, 1, 4, 1, UIUtil.newLabel("Table Selection:"));
            add(centerPane, 2, 4, 2, UIUtil.newCheckComboBox(connection.getDatabases(), "name", this::SelectTablesListener));
            // add(centerPane, 3, 4, 1,
            // UIUtil.newLabel(LocaleUtil.getProperty("ExportWizard.scope.text")));

            FlowPane buttonPane = new FlowPane(5, 5);
            buttonPane.getChildren().add(UIUtil.newButton("Import", null, null, this::ExecuteBackupAction));

            setStyle("-fx-padding: 10;");
            setTop(headerPane);
            setCenter(UIUtil.newScrollPane(centerPane));
            setBottom(buttonPane);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private final void add(GridPane pane, int column, int row, int ncols, Control child) {
        GridPane.setConstraints(child, column, row, ncols, 1); // column row colspan rowspan
        pane.getChildren().add(child);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        Session session = Session.getInstance();
        if ("DatabaseName".equals(evt.getPropertyName())) {

            Connection connection = session.getConnection();

            for (String name : selector.getDatabaseNames()) {
                Database database = connection.findDatabase(name);
                if (database != null) {
                    List<String> list = database.listTableNames();
                    session.getChangeManager().fireChangeEvent(selector, "TableName", list);
                }
            }
        }
    }

    public void ExecuteBackupAction(ActionEvent e) {

        Session session = Session.getInstance();

        DatabaseConnection dbc = session.getDatabaseConnection();
        if (dbc != null) {
            try {

                ConverterInterface converter = ConverterFactory.getConverter(selector.getBackupFormat());
                if (converter == null) {
                    UIUtil.showError("Converter not found for " + selector.getBackupFormat());
                    return;
                }

                Connection connection = session.getConnection();
                converter.importDatabase(connection, selector);

                UIUtil.showInformationDialog("Import from " + selector.getBackupFormat() + " was successful!");
                setVisible(false);
            } catch (Exception ex) {
                UIUtil.showException("Import from " + selector.getBackupFormat() + " failed.", ex);
            }
        }
    }

    // -------------------------------------------------------------------------

    public void BrowseFilename(ActionEvent e) {
        Session session = Session.getInstance();
        String filename = UIUtil.showSaveDialog(session.getStage(), "Browse", selector.getFilename());
        selector.setFilename(filename);
        System.out.println("BrowseFilename: " + filename);
        if (filename != null) {
            session.getChangeManager().fireChangeEvent(selector, "Filename", filename);
        }
    }

    public void SelectDatabasesListener(ListChangeListener.Change<? extends String> c) {

        try {
            Session session = Session.getInstance();
            TableView<?> uiTable = session.getSelectedTableView();
            if (uiTable == null) {
                session.getStatusField().showError("Please select database to connect with.");
                return;
            }

            Connection connection = session.getConnection();

            List<String> names = (List<String>) c.getList();
            if (names.size() == 1) {
                if (names.get(0).equalsIgnoreCase("Select All")) {
                    names.clear();
                    // names.add("Select All");
                    names.addAll(connection.listDatabaseNames());
                }
                if (names.get(0).equalsIgnoreCase("Select None")) {
                    names.clear();
                    // names.add("Select None");
                }
            }

            selector.setDatabaseNames(names);
        } catch (Exception ex) {
            UIUtil.showException("Failed to load selected databases: ", ex);
        }
    }

    public void SelectTablesListener(ListChangeListener.Change<? extends String> c) {
        Session session = Session.getInstance();
        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            session.getStatusField().showError("Please select database to connect with.");
            return;
        }

        ObservableList names = c.getList();

        selector.setTableNames(names);

    }

    // -------------------------------------------------------------------------

    public String exec(String cmd, String inputStr) throws IOException, InterruptedException {
        String cwd = System.getProperty("user.dir");
        Process proc = Runtime.getRuntime().exec(cmd, null, new File(cwd));
        BufferedWriter stdin = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
        if (inputStr != null) {
            stdin.write(inputStr);
            stdin.newLine();
        }
        stdin.flush();
        stdin.close();
        StringBuffer stdout = new StringBuffer();
        StringBuffer stderr = new StringBuffer();
        new StreamThread(proc.getInputStream(), stdout).start();
        new StreamThread(proc.getErrorStream(), stderr).start();
        proc.waitFor();
        System.out.println("stdout: " + stdout);
        System.out.println("stderr: " + stderr);
        return stdout.toString();
    }

    class StreamThread extends Thread {
        BufferedReader in;
        StringBuffer str;

        public StreamThread(InputStream in, StringBuffer str) {
            this.in = new BufferedReader(new InputStreamReader(in));
            this.str = str;
        }

        public void run() {
            try {
                while (true) {
                    String line = in.readLine();
                    if (line == null) {
                        break;
                    }
                    str.append(line);
                    str.append("\n");
                }
                in.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
