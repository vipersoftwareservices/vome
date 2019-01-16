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
import java.util.ArrayList;
import java.util.List;

import com.viper.vome.converters.ConverterFactory;
import com.viper.vome.converters.ConverterInterface;
import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Database;
import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.DataScope;
import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.model.FileFormat;
import com.viper.vome.model.Selections;
import com.viper.vome.util.FileUtil;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class ExportWizard extends BorderPane implements PropertyChangeListener {

    protected Selections selector = new Selections();

    public ExportWizard() {

        try {
            Session session = Session.getInstance();

            Connection connection = session.openConnection();
            List<String> databaseNames = connection.listDatabaseNames();

            List<String> tableNames = new ArrayList<String>();

            String propertyFilename = LocaleUtil.getProperty("import.export.filenames");
            List<String> propertyFilenames = new ArrayList<String>();

            String filename = System.getProperty("user.dir") + "/" + LocaleUtil.getProperty("default.export.filename");
            FileUtil.mkPath(filename);

            FlowPane headerPane = new FlowPane(5, 5);
            headerPane.getChildren().add(UIUtil.newLabel("Connection Name:"));
            headerPane.getChildren().add(UIUtil.newLabel(session.getDatabaseConnection(), "Name"));

            GridPane centerPane = new GridPane();
            centerPane.setHgap(10); // horizontal gap in pixels => that's what you are asking for
            centerPane.setVgap(10); // vertical gap in pixels
            centerPane.setPadding(new Insets(10, 10, 10, 10));

            add(centerPane, 1, 1, 1, UIUtil.newLabel("Scope: "));
            add(centerPane, 2, 1, 1, UIUtil.newComboBox(selector, "DataScope", DataScope.values()));
            // add(centerPane, 3, 1, 1,
            // UIUtil.newLabel(LocaleUtil.getProperty("ExportWizard.scope.text")));

            add(centerPane, 1, 2, 1, UIUtil.newLabel("Database Selection:"));
            add(centerPane, 2, 2, 1, UIUtil.newCheckComboBox(connection.getDatabases(), "name", this::SelectDatabasesListener));
            // add(centerPane, 3, 2, 1,
            // UIUtil.newLabel(LocaleUtil.getProperty("ExportWizard.scope.text")));

            add(centerPane, 2, 3, 1, UIUtil.newLabel("Table Selection:"));
            add(centerPane, 2, 3, 1, UIUtil.newCheckComboBox(connection.getDatabases(), "name", this::SelectTablesListener));
            // add(centerPane, 3, 3, 1,
            // UIUtil.newLabel(LocaleUtil.getProperty("ExportWizard.scope.text")));

            add(centerPane, 1, 4, 1, UIUtil.newLabel("Format: "));
            add(centerPane, 2, 4, 1, UIUtil.newComboBox(selector, "BackupFormat", FileFormat.values()));
            // add(centerPane, 3, 4, 1,
            // UIUtil.newLabel(LocaleUtil.getProperty("ExportWizard.format.text")));

            add(centerPane, 1, 5, 1, UIUtil.newLabel("Export Filename"));
            add(centerPane, 2, 5, 1, UIUtil.newComboBox(selector, "Filename", propertyFilenames));
            add(centerPane, 3, 5, 1, UIUtil.newButton("Browse", this::BrowseFilename));

            FlowPane bottomPane = new FlowPane(5, 5);
            bottomPane.getChildren().add(UIUtil.newButton("Export", null, null, this::ExecuteExportAction));

            setStyle("-fx-padding: 10;");
            setTop(headerPane);
            setCenter(UIUtil.newScrollPane(centerPane));
            setBottom(bottomPane);

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
        try {
            Session session = Session.getInstance();

            if ("DatabaseName".equals(evt.getPropertyName())) {
                Connection connection = session.openConnection();

                for (String name : selector.getDatabaseNames()) {
                    Database database = connection.findDatabase(name);
                    if (database != null) {
                        List<String> list = database.listTableNames();
                        session.getChangeManager().fireChangeEvent(selector, "TableName", list.toArray());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ExecuteExportAction(ActionEvent e) {
        Session session = Session.getInstance(); 
        DatabaseConnection dbc = session.getDatabaseConnection();
        if (dbc != null) {
            try { 
                ConverterInterface converter = ConverterFactory.getConverter (selector.getBackupFormat());
                if (converter == null) {
                    UIUtil.showError("Converter not found for " + selector.getBackupFormat());
                    return;
                }

                Connection connection = session.getConnection();
                converter.exportDatabase(connection, selector); 

                UIUtil.showMessage("Export to " + selector.getBackupFormat() + " was successful!");
            } catch (Exception ex) {
                UIUtil.showException("Export to " + selector.getBackupFormat() + " failed.", ex);
            }
        }
    }

    public void BrowseFilename(ActionEvent e) {
        Session session = Session.getInstance();
        String filename = UIUtil.showSaveDialog(session.getStage(), "Browse Files", selector.getFilename());
        selector.setFilename(filename);

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
}
