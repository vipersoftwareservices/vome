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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Database;
import com.viper.vome.dao.Table;
import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.model.FileFormat;
import com.viper.vome.model.Selections;
import com.viper.vome.util.FileUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class BackupWizard extends BorderPane {

    protected Selections selector = new Selections();

    private final List<Table> tables = new ArrayList<Table>();
    private final List<BooleanProperty> booleanProperties = new ArrayList<BooleanProperty>();
    protected StringProperty nameProperty = null;

    public BackupWizard() {

        Session session = Session.getInstance();

        DatabaseConnection dbc = session.getDatabaseConnection();
        if (dbc == null) {
            UIUtil.showError("No Database Connection opened.");
            return;
        }

        nameProperty = UIUtil.buildStringProperty(session.getDatabaseConnection(), "name");

        String propertiesFilename = LocaleUtil.getProperty("backup.filename");
        String filename = System.getProperty("user.dir") + "/" + LocaleUtil.getProperty("default-backup-filename");
        FileUtil.mkPath(filename);

        List<FileFormat> formats = Arrays.asList(FileFormat.values());

        FlowPane headerPane = new FlowPane(5, 5);
        headerPane.getChildren().add(UIUtil.newLabel("Database:"));
        headerPane.getChildren().add(UIUtil.newLabel(nameProperty));

        Connection connection = session.getConnection();

        GridPane formPane = new GridPane();
        formPane.setHgap(10); // horizontal gap in pixels => that's what you are asking for
        formPane.setVgap(10); // vertical gap in pixels
        formPane.setPadding(new Insets(10, 10, 10, 10));

        add(formPane, 1, 1, 1, UIUtil.newLabel("Database Selection"));
        add(formPane, 2, 1, 2, UIUtil.newCheckComboBox(connection.getDatabases(), "name", this::SelectDatabasesListener));

        add(formPane, 1, 2, 1, UIUtil.newLabel("Table Selection"));
        add(formPane, 2, 2, 2, UIUtil.newCheckComboBox(tables, "name", this::SelectTablesListener));

        add(formPane, 1, 3, 1, UIUtil.newLabel("Filename Selection"));
        add(formPane, 2, 3, 1, UIUtil.newComboBox(selector, "filename", null, this::SelectFilenameListener));
        add(formPane, 3, 3, 1, UIUtil.newButton("Browse", this::BrowseFilename));

        add(formPane, 1, 4, 1, UIUtil.newLabel("Backup Format: "));
        add(formPane, 2, 4, 2, UIUtil.newComboBoxBeans(selector, "backupFormat", formats, null));

        TilePane optionPane = new TilePane();
        optionPane.setPadding(new Insets(5, 0, 5, 0));
        optionPane.setVgap(4);
        optionPane.setHgap(4);
        optionPane.setPrefColumns(3);
        optionPane.setTileAlignment(Pos.CENTER_LEFT);
        optionPane.setStyle("-fx-background-color: DAE6F3;");

        Properties bundle = LocaleUtil.getBundle();
        for (Object key : bundle.keySet()) {
            String name = key.toString();
            if (name.startsWith("backup-wizard.") && name.endsWith(".Name")) {
                String prefix = name.substring(0, name.length() - ".Name".length());
                String cbname = name.substring("backup-wizard.".length(), name.length() - ".Name".length());

                SimpleBooleanProperty property = new SimpleBooleanProperty(null, cbname, false);
                optionPane.getChildren().add(UIUtil.newCheckBox(property));

                booleanProperties.add(property);
            }
        }

        VBox centerPane = new VBox();
        centerPane.getChildren().add(formPane);
        centerPane.getChildren().add(UIUtil.newBorderedTitlePane("Backup Options", optionPane));

        FlowPane buttonPane = new FlowPane(5, 5);
        buttonPane.getChildren().add(UIUtil.newButton("Execute Backup", null, null, this::ExecuteBackupAction));

        setStyle("-fx-padding: 10;");
        setTop(headerPane);
        setCenter(UIUtil.newScrollPane(centerPane));
        setBottom(buttonPane);
    }

    private final void add(GridPane pane, int column, int row, int ncols, Control child) {
        GridPane.setConstraints(child, column, row, ncols, 1); // column row colspan rowspan
        pane.getChildren().add(child);
    }

    // -------------------------------------------------------------------------

    public void ExecuteBackupAction(ActionEvent e) {

        Session session = Session.getInstance();

        FileFormat format = selector.getBackupFormat();
        if (format == null) {
            UIUtil.showError("Please select a format for the backup file");
        }

        String name = selector.getFilename();
        if (name == null || name.length() == 0) {
            UIUtil.showError("Please enter the export filename");
        }

        DatabaseConnection dbc = session.getDatabaseConnection();
        if (dbc != null) {
            String filename = selector.getFilename();
            FileUtil.mkPath(filename);
            String command = null;
            try {
                // TODO command = session.getDriver(dbc).backupDatabase(dbc, selector);

                UIUtil.showStatus("Backup in process...");
                UIUtil.showStatus(command);

                String status = DatabaseViewer.exec(command, null);

                UIUtil.showStatus("Backup finished: " + status);
            } catch (Exception ex) {
                UIUtil.showException("Failed to execute backup command: " + command, ex);
            }
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

            tables.clear();
            for (String name : names) {
                Database database = connection.findDatabase(name);
                connection.loadTables(database);
                tables.addAll(database.getTables());
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

    public void SelectFilenameListener(ActionEvent event) {
        Session session = Session.getInstance();
        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            session.getStatusField().showError("Please select database to connect with.");
            return;
        }

    }

    // -------------------------------------------------------------------------

    public void BrowseFilename(ActionEvent e) {
        Session session = Session.getInstance();
        String filename = UIUtil.showSaveDialog(session.getStage(), "Title", selector.getFilename());
        if (filename != null) {
            selector.setFilename(filename);
        }
    }
}
