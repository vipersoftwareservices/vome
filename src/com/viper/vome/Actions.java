/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2012/01/15
 *
 * Copyright 1998-2012 by Viper Software Services
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
 * @version 1.0, 01/15/2012
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.vome;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.viper.vome.beans.JAXBUtils;
import com.viper.vome.dao.Column;
import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Connections;
import com.viper.vome.dao.Database;
import com.viper.vome.dao.ForeignKey;
import com.viper.vome.dao.Index;
import com.viper.vome.dao.Procedure;
import com.viper.vome.dao.Row;
import com.viper.vome.dao.SQLHistory;
import com.viper.vome.dao.Table;
import com.viper.vome.dao.User;
import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.model.DatabaseConnections;
import com.viper.vome.util.FileUtil;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.Pane;

@SuppressWarnings("unused")
public class Actions {

    private final static DataFormat CELLDATA = new DataFormat("CellData");

    public Actions() {

    }

    public static final Node getSource(ActionEvent event) {
        if (event != null && event.getSource() != null) {
            if (event.getSource() instanceof Node) {
                return (Node) event.getSource();
            }
        }

        return null;
    }

    public static final Object getUserData(ActionEvent event) {
        if (event != null && event.getSource() != null) {
            if (event.getSource() instanceof Node) {
                Node source = getSource(event);
                if (source != null && source.getUserData() != null && source.getUserData() instanceof Node) {
                    return source.getUserData();
                }
            }
        }

        return null;
    }

    // --------------------------------------------------------------------------
    // Listeners
    // --------------------------------------------------------------------------

    public void NoopAction(ActionEvent event) {
    }

    public void SetVisible(ActionEvent event) {
        Node source = getSource(event);
        if (source != null && source.getUserData() != null && source.getUserData() instanceof Node) {
            Node item = (Node) source.getUserData();
            if (item.isVisible()) {
                item.setVisible(false);
                item.setManaged(false);
            } else {
                item.setVisible(true);
                item.setManaged(true);
            }
        }
    }

    public void exitAction(ActionEvent event) {
        // TODO - check if any outstanding edits.
        System.exit(0);
    }

    public void helpAction(ActionEvent event) {
        // listener.actionPerformed(e);
    }

    public void aboutAction(ActionEvent event) {
        Dialogs.showHelpDialog("../about", null);
    }

    public void guideAction(ActionEvent event) {
        Dialogs.showHelpDialog("../index", null);
    }

    public void onItemAction(ActionEvent event) {
        // TODO not correct
        Dialogs.showHelpDialog("../about", null);
    }

    public void runScriptAction(ActionEvent event) {
        try {
            Session session = Session.getInstance();
            DatabaseConnection dbc = session.getDatabaseConnection();
            if (dbc == null) {
                return;
            }
            String filename = UIUtil.showOpenDialog(session.getStage(), "Script Filename", null);
            if (filename == null) {
                return;
            }

            FileUtil.mkPath(filename);
            String command = null;

            // command = dao.scriptCommand(dbc, filename);

            showStatus("Run script in process...");
            showStatus(command);

            String status = DatabaseViewer.exec(command, null);
            showStatus("Script finished: " + status);

        } catch (Exception ex) {
            showError("runScriptAction", ex);
        }
    }

    public void sqlEditorAction(ActionEvent event) {
        try {
            Session session = Session.getInstance();
            TableTabbedPane tabpane = session.getTableTabbedPane();
            Tab tab = new SQLEditor().createTab("");

            tabpane.getTabs().add(tab);
            tabpane.getSelectionModel().select(tab);

        } catch (Exception ex) {
            showError("sqlEditorAction", ex);
        }
    }

    public void saveAction(ActionEvent event) {
        try {
            Session session = Session.getInstance();
            JAXBUtils.marshal(new File(session.getDatabasePropertyFilename()), session.getDatabaseConnections(), null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void CloseTabAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableTabbedPane pane = session.getTableTabbedPane();
        Tab tab = pane.getSelectionModel().getSelectedItem();
        if (tab != null) {
            pane.getTabs().remove(tab);
        }
    }

    public void CloseOtherTabsAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableTabbedPane pane = session.getTableTabbedPane();
        Tab tab = pane.getSelectionModel().getSelectedItem();
        for (Tab item : pane.getTabs()) {
            if (tab != item) {
                pane.getTabs().remove(item);
            }
        }
    }

    public void CloseAllTabsAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableTabbedPane pane = session.getTableTabbedPane();
        pane.getTabs().removeAll(pane.getTabs());
    }

    public void NewProjectAction(ActionEvent event) {

        Session session = Session.getInstance();
        try {
            Connections.openDatabaseConnections(null);

            session.setDatabaseConnections(Connections.getDatabaseConnections());
        } catch (Exception ex) {
            String msg = "Connections unable to open new connections.";
            UIUtil.showException(msg, ex);
        }
    }

    public void OpenProjectAction(ActionEvent event) {
        Session session = Session.getInstance();

        String defaultFilename = session.getConnections().getFilename();
        String filename = UIUtil.showOpenDialog(session.getStage(), "Project Filename", defaultFilename);
        if (filename != null) {
            try {
                Connections.openDatabaseConnections(filename);
                session.setDatabaseConnections(Connections.getDatabaseConnections());

            } catch (Exception dberr) {
                showError("Failed to open database connections file " + filename);
            }
        }
    }

    public void CloseProjectAction(ActionEvent event) {

        try {
            Session session = Session.getInstance();
            Connections.closeDatabaseConnections();
            session.setDatabaseConnections(Connections.getDatabaseConnections());

            SQLHistory.save();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void SaveProjectAction(ActionEvent event) {
        Session session = Session.getInstance();
        try {
            String filename = Connections.getFilename();
            if (filename == null || filename.equals(Session.NEW_DATA_SOURCE_FILENAME_LABEL)) {
                filename = UIUtil.showSaveDialog(session.getStage(), "New Project Filename", null);
            }
            Connections.saveDatabaseConnections(filename);
            SQLHistory.save();

        } catch (Exception ex) {
            showException("Unable to save database registry to  " + Connections.getFilename() + ".", ex);
        }
    }

    public void ImportAction(ActionEvent event) {
        Session session = Session.getInstance();
        if (session.getDatabaseConnection() == null) {
            showError("No connection has been opened on which to backup, open a connection.");
            return;
        }
        TableTabbedPane tabpane = session.getTableTabbedPane();

        ImportWizard pane = new ImportWizard();

        Tab tab = UIUtil.newTab("Import", pane);
        // tab.setUserData(uiTable);

        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    public void ImportHostsAction(ActionEvent event) {
        Session session = Session.getInstance();

        HostsListPane pane = new HostsListPane();
        ButtonType result = Dialogs.showAsDialog("Generate Data", pane, null, ButtonType.APPLY, ButtonType.CANCEL);
        if (result == ButtonType.APPLY) {
            pane.saveHostsAction();
            return;
        }
        if (result == ButtonType.CANCEL) {
            return;
        }
    }

    public void ExportAction(ActionEvent event) {
        Session session = Session.getInstance();
        if (session.getDatabaseConnection() == null) {
            showError("No connection has been opened on which to backup, open a connection.");
            return;
        }
        TableTabbedPane tabpane = session.getTableTabbedPane();

        ExportWizard pane = new ExportWizard();

        Tab tab = UIUtil.newTab("Export", pane);
        // tab.setUserData(uiTable);

        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    public void SaveTableAction(ActionEvent event) {
        try {
            Session session = Session.getInstance();
            TableView<Row> tableView = session.getSelectedTableView();
            if (tableView == null) {
                showError("Please select a tab entry.");
                return;
            }
            if (session.getDatabaseConnection() == null) {
                showError("Please select a database connection.");
                return;
            }
            Table table = (Table) tableView.getUserData();

            Connection connection = session.openConnection();
            connection.exeuteInsertUpdate(table, tableView.getItems());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ExplainTableAction(ActionEvent event) {
        try {
            Session session = Session.getInstance();
            TableView<Row> tableView = session.getSelectedTableView();
            if (tableView == null) {
                showError("Please select a tab entry.");
                return;
            }
            if (session.getDatabaseConnection() == null) {
                showError("Please select a database connection.");
                return;
            }

            Table table = (Table) tableView.getUserData();
            String title = "Explain:" + table.getName();

            Table explainTable = new Table();
            explainTable.setDatabaseName(table.getDatabaseName());
            explainTable.setName(title);

            Connection connection = session.openConnection();
            List<Row> items = connection.explain(table);

            TableTabbedPane tabpane = session.getTableTabbedPane();
            tabpane.createTableItem(explainTable, Session.PROPERTIES_COLUMN, items);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void BackupDatabaseAction(ActionEvent event) {
        Session session = Session.getInstance();
        if (session.getDatabaseConnection() == null) {
            showError("No connection has been opened on which to backup, open a connection.");
            return;
        }
        TableTabbedPane tabpane = session.getTableTabbedPane();

        BackupWizard pane = new BackupWizard();

        Tab tab = UIUtil.newTab("Backup", pane);
        // tab.setUserData(uiTable);

        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    public void RecoverDatabaseAction(ActionEvent event) {
        Session session = Session.getInstance();
        if (session.getDatabaseConnection() == null) {
            showError("No connection has been opened on which to backup, open a connection.");
            return;
        }
        TableTabbedPane tabpane = session.getTableTabbedPane();

        RecoverWizard pane = new RecoverWizard();

        Tab tab = UIUtil.newTab("Recovery", pane);
        // tab.setUserData(uiTable);

        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    public void MetaDataAction(ActionEvent event) {

    }

    public void MigrateAction(ActionEvent event) {

    }

    public void CompareAction(ActionEvent event) {

    }

    public void GenerateAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableView<?> dataTable = session.getSelectedTableView();
        if (dataTable == null) {
            showError("Please select database to connect with.");
            return;
        }
        Vector<String> list = new Vector<String>();
        for (Object column : dataTable.getColumns()) {
            list.add(((TableColumn) column).getText());
        }

        GeneratePane generatePane = new GeneratePane(session);
        if (!generatePane.setColumnNames(list)) {
            showError("Failed to set column names.");
            return;
        }
        // generatePane.setTableModel(UserDataModel.getTable(dataTable));
        ButtonType result = Dialogs.showAsDialog("Generate Data", generatePane, null, ButtonType.APPLY, ButtonType.CANCEL);
        if (result == ButtonType.CANCEL) {
            return;
        }
    }

    public void FormatTableAction(ActionEvent event) {
        FormatTablePane pane = new FormatTablePane();
        ButtonType result = Dialogs.showAsDialog("Format Table Properties", pane, null, ButtonType.APPLY, ButtonType.CANCEL);
        if (result == ButtonType.CANCEL) {
            return;
        }
        // TODO Apply settings
    }

    public void FormatColumnAction(ActionEvent event) {
        Session session = Session.getInstance();
        FormatColumnPane pane = new FormatColumnPane(session);
        ButtonType result = Dialogs.showAsDialog("Format Column Properties", pane, null, ButtonType.APPLY, ButtonType.CANCEL);
        if (result == ButtonType.CANCEL) {
            return;
        }
        // TODO Apply settings
    }

    public void AddRowAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            showError("Please select database to connect with.");
            return;
        }

        uiTable.getItems().add(null); // TODO
    }

    public void DeleteRowsAction(ActionEvent event) {
        Session session = Session.getInstance();
        Database database = session.getDatabase();
        Table table = session.getTable();

        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            showError("Please select database connection.");
            return;
        }

        if (!UIUtil.askForConfirmation("Confirm deletion of rows " + database.getName() + "." + table.getName() + "?")) {
            return;
        }

        try {
            List<Row> items = (List<Row>) uiTable.getSelectionModel().getSelectedItems();

            Connection connection = session.openConnection();
            connection.delete(database, table, items);

            session.setTable(table);

        } catch (Exception ex) {
            showException("Delete table " + table.getDatabaseName() + "." + table.getName(), ex);
        }
    }

    public void SelectColumnAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            showError("Please select database to connect with.");
            return;
        }
        int colno = uiTable.getSelectionModel().getSelectedIndex();
        // uiTable.setRowSelectionInterval(0, uiTable.getRowCount() - 1);
        // uiTable.setColumnSelectionInterval(colno, colno);
    }

    public void ToggleTableEditModeAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            showError("Please select table.");
            return;
        }

        uiTable.setEditable(!uiTable.isEditable());
    }

    public void SelectColumnVisibilityListener(ListChangeListener.Change<? extends String> c) {
        Session session = Session.getInstance();
        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            showError("Please select database to connect with.");
            return;
        }

        ObservableList names = c.getList();

        for (TableColumn column : uiTable.getColumns()) {
            String name = column.getText();
            column.setVisible(names.contains(name));
        }
    }

    public void SelectRowAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            showError("Please select database to connect with.");
            return;
        }
        int rowno = uiTable.getSelectionModel().getSelectedIndex();
        // uiTable.setRowSelectionInterval(rowno, rowno);
        // uiTable.setColumnSelectionInterval(0, uiTable.getColumnCount() - 1);
    }

    public void SelectAllAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            showError("Please select database to connect with.");
            return;
        }
        // uiTable.setRowSelectionInterval(0, uiTable.getRowCount() - 1);
        // uiTable.setColumnSelectionInterval(0, uiTable.getColumnCount() - 1);
    }

    public void PasteSpecialAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            showError("Please select database to connect with.");
            return;
        }
    }

    public void PasteAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            showError("Please select database to connect with.");
            return;
        }

        ObservableList selectedCells = uiTable.getSelectionModel().getSelectedCells();

        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (!clipboard.hasContent(CELLDATA)) {
            showError("The Clipboard format is not compatile: " + clipboard.getContentTypes());
            return;
        }
        Object transfer = clipboard.getContent(CELLDATA);
        List<String> list = (List<String>) transfer;

        int index = 0;
        for (String item : list) {
            selectedCells.set(index++, item);
        }
    }

    public void CopyAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            showError("Please select database to connect with.");
            return;
        }

        ObservableList selectedCells = uiTable.getSelectionModel().getSelectedCells();
        List<String> list = new ArrayList<String>();
        list.addAll(selectedCells);

        Clipboard clipboard = Clipboard.getSystemClipboard();

        Map<DataFormat, Object> map = new HashMap<DataFormat, Object>();
        map.put(CELLDATA, list);
        clipboard.setContent(map);
    }

    public void CutAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            showError("Please select database to connect with.");
            return;
        }

        ObservableList selectedCells = uiTable.getSelectionModel().getSelectedCells();
        List<String> list = new ArrayList<String>();
        list.addAll(selectedCells);

        Clipboard clipboard = Clipboard.getSystemClipboard();

        Map<DataFormat, Object> map = new HashMap<DataFormat, Object>();
        map.put(CELLDATA, list);
        clipboard.setContent(map);

        // TODO add the remove part
    }

    public void RowNumberAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableView<?> uiTable = session.getSelectedTableView();
        if (uiTable == null) {
            showError("Please select database to connect with.");
            return;
        }

        try {
            session.setShowLineNumbers(!session.isShowLineNumbers());
            // uiTable.getItems().setAll(DatabaseModel.getData(UserDataModel.getTable(uiTable)));
        } catch (Exception ex) {
            showException("Unable to show numbers.", ex);
        }
    }

    public void SpellCheckerAction(ActionEvent event) {
    }

    public void PrintAction(ActionEvent event) {
    }

    public void AlignCenterAction(ActionEvent event) {
        Session session = Session.getInstance();
        Pos alignment = Pos.CENTER;
        TableView<?> dataTable = session.getSelectedTableView();
        if (dataTable == null) {
            showError("Please select database to connect with.");
            return;
        }
        // dataTable.setCellAlignment(alignment);
        // dataTable.repaint();
    }

    public void AlignLeftAction(ActionEvent event) {
        Session session = Session.getInstance();
        Pos alignment = Pos.CENTER_LEFT;
        TableView<?> dataTable = session.getSelectedTableView();
        if (dataTable == null) {
            showError("Please select database to connect with.");
            return;
        }
        // dataTable.setCellAlignment(alignment);
        // dataTable.repaint();
    }

    public void AlignRightAction(ActionEvent event) {
        Session session = Session.getInstance();
        Pos alignment = Pos.CENTER_RIGHT;

        TableView<?> dataTable = session.getSelectedTableView();
        if (dataTable == null) {
            showError("Please select database to connect with.");
            return;
        }
        // dataTable.setCellAlignment(alignment);
        // dataTable.repaint();
    }

    /**
     * Sets the current L&F on each demo module
     */
    public void LookAndFeelAction(ActionEvent event) {
        try {

            // Set the skin

        } catch (Exception ex) {
            String msg = "Failed loading L&F: " + "skin";
            showException(msg, ex);
        }
    }

    public void DatabaseConnectionsPropertiesAction(ActionEvent event) {

        Session session = Session.getInstance();
        TableTabbedPane tabpane = session.getTableTabbedPane();

        DataSourcePropertiesPane pane = new DataSourcePropertiesPane();

        Tab tab = UIUtil.newTab("Connections", pane);
        // tab.setUserData(uiTable);

        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    public void ImportDatabaseConnectionsAction(ActionEvent event) {
        Session session = Session.getInstance();
        TableTabbedPane tabpane = session.getTableTabbedPane();

        DataResourceWizard pane = new DataResourceWizard();

        Tab tab = UIUtil.newTab("Import Database", pane);
        // tab.setUserData(uiTable);

        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    // -------------------------------------------------------------------------
    // Database Connection
    // -------------------------------------------------------------------------

    public void NewConnectionAction(ActionEvent event) {
        DataSourcePane.showAsDialog("New Connection", new DatabaseConnection());
    }

    public void EditConnectionAction(ActionEvent event) {
        Session session = Session.getInstance();
        DataSourcePane.showAsDialog("Edit Connection", session.getDatabaseConnection());
    }

    public void OpenConnectionAction(ActionEvent event) {

        Session session = Session.getInstance();
        DatabaseConnection dbc = session.getDatabaseConnection();
        if (dbc == null) {
            showError("A database connection has not been selected.");
            return;
        }

        try {
            session.setDatabaseConnection(dbc);

            Connection connection = session.openConnection();
            connection.loadDatabases();

        } catch (Exception ex) {
            showException("Unable to show data source edit pane.", ex);
        }
    }

    public void DeleteConnectionAction(ActionEvent event) {

        try {
            Session session = Session.getInstance();

            DatabaseConnection dbc = session.getDatabaseConnection();
            if (dbc == null || dbc.getName() == null) {
                showError("DatabaseConnection not selected.");
            }
            if (!UIUtil.askForConfirmation("Remove datasource " + dbc.getName() + " from list?")) {
                return;
            }

            DatabaseConnections databaseConnections = session.getDatabaseConnections();
            databaseConnections.getConnections().remove(dbc);

            Connections.saveDatabaseConnections(null);

            session.setDatabaseConnections(Connections.getDatabaseConnections());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void RenameDatabaseConnectionAction(ActionEvent event) {
        try {
            Session session = Session.getInstance();

            DatabaseConnection dbc = session.getDatabaseConnection();

            String toname = Dialogs.showRenameDialog("databaseconnection", dbc.getName());
            if (toname == null) {
                return;
            }

            dbc.setName(toname);
            Connections.saveDatabaseConnections(null);
            session.setDatabaseConnection(dbc);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void AddProcedureAction(ActionEvent event) {

        Session session = Session.getInstance();
        Database database = session.getDatabase();

        Procedure proc = Dialogs.showNewProcedureDialog(database);
        if (proc == null) {
            return;
        }
        try {
            Connection connection = session.openConnection();
            connection.loadProcedures(database);

            if (database.getProcedures().size() == 0) {
                showError("Procedure " + database.getName() + " already exists.");
                return;
            }
            if (database.findProcedure(proc.getName()) != null) {
                showError("Procedure already exists: " + proc.getName());
                return;
            }
            if (!UIUtil.askForConfirmation("Add Procedure " + database.getName() + "." + proc.getName() + "?")) {
                return;
            }
            connection.createProcedure(proc);

            database.getProcedures().add(proc);
            session.setProcedure(proc);

        } catch (Exception ex) {
            showException("Add procedure " + database.getName() + "." + proc.getName(), ex);
        }
    }

    public void AddDatabaseAction(ActionEvent event) {

        try {
            Session session = Session.getInstance();
            DatabaseWizard pane = new DatabaseWizard();

            Tab tab = UIUtil.newTab("Add Database", pane);
            tab.setUserData(pane);

            TableTabbedPane tabpane = session.getTableTabbedPane();
            tabpane.getTabs().add(tab);
            tabpane.getSelectionModel().select(tab);

        } catch (Exception ex) {
            showException("Add database ", ex);
        }
    }

    public void DeleteDatabaseAction(ActionEvent event) {
        Session session = Session.getInstance();
        Database database = session.getDatabase();

        if (!UIUtil.askForConfirmation("Delete database " + database.getName() + "?")) {
            return;
        }
        try {
            Connection connection = session.openConnection();
            connection.drop(database);

        } catch (Exception ex) {
            showException("Delete database " + database.getName(), ex);
        }
    }

    public void RenameDatabaseAction(ActionEvent event) {
        Session session = Session.getInstance();

        Database database = session.getDatabase();

        try {

            Connection connection = session.openConnection();

            String toname = Dialogs.showRenameDialog("database", database.getName());
            if (toname == null) {
                return;
            }
            if (connection.databaseExists(toname)) {
                showError("Database " + toname + " already exists.");
                return;
            }

            // DriverFactory.getDriver(dbc).renameDatabase(database, toname);
            session.setDatabase(database);
        } catch (Exception ex) {
            showException("Failed to rename database " + database.getName(), ex);
        }
    }

    public void DatabaseParametersAction(ActionEvent event) {
        Session session = Session.getInstance();

        DatabaseParameters pane = new DatabaseParameters();
        Tab tab = UIUtil.newTab("Parameters", pane);
        tab.setUserData(pane);

        TableTabbedPane tabpane = session.getTableTabbedPane();
        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    public void AddColumnAction(ActionEvent event) {
        Session session = Session.getInstance();
        ColumnWizard pane = new ColumnWizard(null);

        Tab tab = UIUtil.newTab("Add Column", pane);
        tab.setUserData(pane);

        TableTabbedPane tabpane = session.getTableTabbedPane();
        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    public void AddIndexAction(ActionEvent event) {
        Session session = Session.getInstance();
        IndexWizard pane = new IndexWizard();

        Tab tab = UIUtil.newTab("Add Index", pane);
        tab.setUserData(pane);

        TableTabbedPane tabpane = session.getTableTabbedPane();
        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    public void ViewSQLHistoryAction(ActionEvent event) {

        Session session = Session.getInstance();
        String title = "SQL History";
        String databaseName = session.getDatabase().getName();

        try {

            Table columnsTable = new Table();
            columnsTable.setDatabaseName(databaseName);
            columnsTable.setName(title);
            columnsTable.getColumns().addAll(Connection.convertRowToColumns(SQLHistory.getHistory().get(0)));

            TableTabbedPane tabpane = session.getTableTabbedPane();
            TableView<? extends Row> tableView = tabpane.createTableItem(columnsTable, Session.PROPERTIES_INDEX,
                    SQLHistory.getHistory());

        } catch (Exception ex) {
            showError("Unable to select table: " + session.getDatabase().getName() + "." + title, ex);
        }
    }

    public void PropertiesIndiciesAction(ActionEvent event) {

        Session session = Session.getInstance();
        Table table = session.getTable();

        try {
            String title = "Indicies:" + table.getName();

            Table columnsTable = new Table();
            columnsTable.setDatabaseName(table.getDatabaseName());
            columnsTable.setName(title);
            columnsTable.getColumns().addAll(Connection.convertRowToColumns(table.getIndicies().get(0)));

            TableTabbedPane tabpane = session.getTableTabbedPane();
            TableView<? extends Row> tableView = tabpane.createTableItem(columnsTable, Session.PROPERTIES_INDEX,
                    table.getIndicies());

        } catch (Exception ex) {
            showError("Unable to select table: " + session.getDatabase().getName() + "." + table.getName(), ex);
        }
    }

    public void AddForeignKeyAction(ActionEvent event) {

        Session session = Session.getInstance();
        ForeignKeyWizard pane = new ForeignKeyWizard();

        Tab tab = UIUtil.newTab("Add Index", pane);
        tab.setUserData(pane);

        TableTabbedPane tabpane = session.getTableTabbedPane();
        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);

    }

    public void DeleteForeignKeyAction(ActionEvent event) {
        Session session = Session.getInstance();

        Table table = session.getTable();
        ForeignKey foreignKey = session.getForeignKey();

        if (!UIUtil.askForConfirmation("Delete index " + foreignKey.getName() + "?")) {
            return;
        }

        try {
            Connection connection = session.openConnection();

            connection.dropForeignKey(foreignKey);
            table.getForeignKeys().remove(foreignKey);

            session.setTable(table);
        } catch (Exception ex) {
            showException("Unable to delete foreign key " + foreignKey.getName(), ex);
        }
    }

    public void RenameForeignKeyAction(ActionEvent event) {
        Session session = Session.getInstance();

        Table table = session.getTable();
        ForeignKey foreignKey = session.getForeignKey();
        Database database = session.getDatabase();

        String toname = Dialogs.showRenameDialog("index ", foreignKey.getName());
        if (toname == null) {
            return;
        }

        try {
            Connection connection = session.openConnection();
            connection.loadForeignKeys(table);

            if (table.findForeignKey(foreignKey.getName()) == null) {
                showError("Foreign Key " + foreignKey.getName() + " already exists.");
                return;
            }

            // getDriver(dbc).renameForeignKey(null, table, index, toname,
            // DONT_DROP_IF_EXISTS);
            foreignKey.setPkName(toname);
            session.setForeignKey(foreignKey);
        } catch (Exception ex) {
            showException("Unable to rename index " + foreignKey.getName() + " to " + toname, ex);
        }
    }

    public void PropertiesForeignKeyAction(ActionEvent event) {

        Session session = Session.getInstance();
        Table table = session.getTable();

        try {
            String title = "ForeignKey:" + table.getName();

            Table columnsTable = new Table();
            columnsTable.setDatabaseName(table.getDatabaseName());
            columnsTable.setName(title);
            columnsTable.getColumns().addAll(Connection.convertRowToColumns(table.getForeignKeys().get(0)));

            TableTabbedPane tabpane = session.getTableTabbedPane();
            TableView<? extends Row> tableView = tabpane.createTableItem(columnsTable, Session.PROPERTIES_FOREIGN_KEY,
                    table.getForeignKeys());

        } catch (Exception ex) {
            showError("Unable to select table: " + session.getDatabase().getName() + "." + table.getName(), ex);
        }
    }

    public void PropertiesPrimaryKeyAction(ActionEvent event) {

        Session session = Session.getInstance();
        Table table = session.getTable();

        try {
            String title = "PrimaryKey:" + table.getName();

            Table columnsTable = new Table();
            columnsTable.setDatabaseName(table.getDatabaseName());
            columnsTable.setName(title);
            columnsTable.getColumns().addAll(Connection.convertRowToColumns(table.getPrimaryKeys().get(0)));

            TableTabbedPane tabpane = session.getTableTabbedPane();
            TableView<? extends Row> tableView = tabpane.createTableItem(columnsTable, Session.PROPERTIES_PRIMARY_KEY,
                    table.getPrimaryKeys());

        } catch (Exception ex) {
            showError("Unable to select table: " + session.getDatabase().getName() + "." + table.getName(), ex);
        }
    }

    public void AddTableAction(ActionEvent event) {

        Session session = Session.getInstance();
        TableWizard pane = new TableWizard(session.getTable());

        Tab tab = UIUtil.newTab("Add Index", pane);
        tab.setUserData(pane);

        TableTabbedPane tabpane = session.getTableTabbedPane();
        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);

    }

    public void DeleteTableAction(ActionEvent event) {
        Session session = Session.getInstance();

        Table table = session.getTable();
        Database database = session.getDatabase();

        if (!UIUtil.askForConfirmation("Confirm deletion of table " + database.getName() + "." + table.getName() + "?")) {
            return;
        }
        try {
            Connection connection = session.openConnection();
            connection.drop(database, table);

            session.setDatabase(database);

        } catch (Exception ex) {
            showException("Delete table " + table.getDatabaseName() + "." + table.getName(), ex);
        }
    }

    public void RenameTableAction(ActionEvent event) {
        Session session = Session.getInstance();

        Table table = session.getTable();

        Node source = getSource(event);
        if (source == null && !(source instanceof TextField)) {
            return;
        }

        String oldName = table.getName();
        String newName = ((TextField) source).getText();

        try {
            Connection connection = session.openConnection();

            String errmsg = null; // validationMgr.validateTableName(loadMetaData(dbc), toname);
            if (errmsg != null) {
                showError(errmsg);
                return;
            }

            connection.rename(table, newName);

            table.setName(newName);
            session.setTable(table);
        } catch (Exception ex) {
            showException("Rename table " + oldName + " to " + newName, ex);
        }
    }

    public void TablePrivilegesAction(ActionEvent event) {
        Session session = Session.getInstance();

        Database database = session.getDatabase();

        try {
            // Dialogs.showTableDialog(session, "Privileges",
            // loadMetaData(dbc).getTablePrivileges());
        } catch (Exception ex) {
            showException("Unable to show data source properties, " + database.getName(), ex);
        }
    }

    public void DatabasePropertiesAction(ActionEvent event) {

        Session session = Session.getInstance();
        String title = "Tables:" + session.getDatabase().getName();

        Pane pane = new TableTablePane();
        Tab tab = UIUtil.newTab(title, pane);
        tab.setUserData(pane);

        TableTabbedPane tabpane = session.getTableTabbedPane();
        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    public void PropertiesColumnsAction(ActionEvent event) {
        Session session = Session.getInstance();
        Table table = session.getTable();

        try {
            String title = "Columns:" + table.getName();

            Table columnsTable = new Table();
            columnsTable.setDatabaseName(table.getDatabaseName());
            columnsTable.setName(title);
            columnsTable.getColumns().addAll(Connection.convertRowToColumns(table.getColumns().get(0)));

            TableTabbedPane tabpane = session.getTableTabbedPane();
            TableView<? extends Row> tableView = tabpane.createTableItem(columnsTable, Session.PROPERTIES_COLUMN,
                    table.getColumns());

        } catch (Exception ex) {
            showError("Unable to disokay columns: " + session.getDatabase().getName() + "." + table.getName(), ex);
        }
    }

    public void AddViewAction(ActionEvent event) {

        Session session = Session.getInstance();
        ViewWizard pane = new ViewWizard(null);

        Tab tab = UIUtil.newTab("View Table", pane);
        tab.setUserData(pane);

        TableTabbedPane tabpane = session.getTableTabbedPane();
        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    public void AddTableColumnAction(ActionEvent event) {

        Session session = Session.getInstance();
        ColumnWizard pane = new ColumnWizard(session.getTable());

        Tab tab = UIUtil.newTab("Add Index", pane);
        tab.setUserData(pane);

        TableTabbedPane tabpane = session.getTableTabbedPane();
        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);

    }

    public void RenameColumnAction(ActionEvent event) {
        Session session = Session.getInstance();

        Column column = session.getColumn();
        Table table = session.getTable();
        Database database = session.getDatabase();

        String toname = Dialogs.showRenameDialog("column", column.getName());
        if (toname == null) {
            return;
        }
        try {
            String errmsg = null; // validationMgr.validateColumnName(loadMetaData(dbc),
                                  // column.getName());
            if (errmsg != null) {
                showError(errmsg);
                return;
            }

            Connection connection = session.openConnection();
            connection.renameColumn(column, toname);

            session.setColumn(column);
        } catch (Exception ex) {
            showException("Unable to rename column " + column.getName() + " to " + toname, ex);
        }
    }

    public void DeleteColumnAction(ActionEvent event) {
        try {
            Session session = Session.getInstance();

            Column column = session.getColumn();
            Table table = session.getTable();
            Database database = session.getDatabase();
            DatabaseConnection dbc = session.getDatabaseConnection();

            if (!UIUtil.askForConfirmation("Delete column " + column.getName() + "?")) {
                return;
            }

            Connection connection = session.openConnection();
            connection.dropColumn(table, column);
            session.setTable(table);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void EditColumnAction(ActionEvent event) {
        Session session = Session.getInstance();
        ColumnWizard pane = new ColumnWizard(session.getTable());

        Tab tab = UIUtil.newTab("Modify Column", pane);
        tab.setUserData(pane);

        TableTabbedPane tabpane = session.getTableTabbedPane();
        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    public void DeleteProcedureAction(ActionEvent event) {
        Session session = Session.getInstance();

        Procedure proc = session.getProcedure();
        Database database = session.getDatabase();

        if (!UIUtil.askForConfirmation("Delete procedure " + database.getName() + "." + proc.getName() + "?")) {
            return;
        }
        try {
            Connection connection = session.openConnection();
            connection.dropProcedure(proc);

            database.getProcedures().remove(proc);
            session.setDatabase(database);
        } catch (Exception ex) {
            showException("Delete procedure " + database.getName() + "." + proc.getName(), ex);
        }
    }

    public void PropertiesProcedureAction(ActionEvent event) {
        // TODO show procedure properties (including source)
    }

    public void AddUserAction(ActionEvent event) {

        try {
            Session session = Session.getInstance();

            Connection connection = session.openConnection();
            Database database = session.getDatabase();

            User user = Dialogs.showNewUserDialog(database);
            if (user == null) {
                return;
            }
            String errmsg = null; // validationMgr.validateUserName(loadMetaData(dbc),
                                  // user.getName());
            if (errmsg != null) {
                showError(errmsg);
                return;
            }
            if (!UIUtil.askForConfirmation("Add User " + user.getName() + "?")) {
                return;
            }
            if (connection.findUser(user.getName()) != null) {
                showError("User is already defined: " + user.getName());
                return;
            }

            connection.createUser(user);

            session.setUser(user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void RenameUserAction(ActionEvent event) {
        Session session = Session.getInstance();

        User user = session.getUser();
        Database database = session.getDatabase();

        String toname = Dialogs.showRenameDialog("user", user.getName());
        if (toname == null) {
            return;
        }
        try {
            String errmsg = null; // validationMgr.validateUserName(loadMetaData(dbc), toname);
            if (errmsg != null) {
                showError(errmsg);
                return;
            }

            Connection connection = session.openConnection();
            // TODO getDriver(dbc).renameUser(user, toname);

            session.setUser(user);
        } catch (Exception ex) {
            showException("Failed to rename user " + user.getName(), ex);
        }
    }

    public void DeleteUserAction(ActionEvent event) {
        Session session = Session.getInstance();

        User user = session.getUser();
        Database database = session.getDatabase();

        if (!UIUtil.askForConfirmation("Delete user " + user.getName() + "?")) {
            return;
        }
        try {
            Connection connection = session.openConnection();
            connection.dropUser(user);

            session.setDatabase(database);

        } catch (Exception ex) {
            showException("Delete user " + user.getName(), ex);
        }
    }

    public void PropertiesUserAction(ActionEvent event) {

    }

    public void RenameIndexAction(ActionEvent event) {
        Session session = Session.getInstance();

        Index index = session.getIndex();
        Table table = session.getTable();
        Database database = session.getDatabase();

        String toname = Dialogs.showRenameDialog("index ", index.getName());
        if (toname == null) {
            return;
        }

        try {
            String errmsg = null; // validationMgr.validateName(loadMetaData(dbc), toname);
            if (errmsg != null) {
                showError(errmsg);
                return;
            }

            Connection connection = session.openConnection();
            connection.renameIndex(index, toname);

            index.setName(toname);
            session.setIndex(index);

        } catch (Exception ex) {
            showException("Unable to rename index " + index.getName() + " to " + toname, ex);
        }
    }

    public void DeleteIndexAction(ActionEvent event) {
        Session session = Session.getInstance();

        Index index = session.getIndex();
        Table table = session.getTable();
        Database database = session.getDatabase();

        if (!UIUtil.askForConfirmation("Delete index " + index.getName() + "?")) {
            return;
        }
        try {
            Connection connection = session.openConnection();
            connection.dropIndex(table, index);
            session.setTable(table);
        } catch (Exception ex) {
            showException("Unable to drop index " + index.getName(), ex);
        }
    }

    public void PropertiesIndexAction(ActionEvent event) {
        Session session = Session.getInstance();

        Index index = session.getIndex();
        Table table = session.getTable();
        Database database = session.getDatabase();

        try {
            if (Dialogs.showEditIndexDialog(table, index) != null) {
                // TODO, save index bean
            }
        } catch (Exception ex) {
            showException("Show properties of column " + index.getName(), ex);
        }
    }

    public void ViewSourceAction(ActionEvent event) {
        Session session = Session.getInstance();
        Table table = session.getTable();
        String sql = table.getViewDefinition();

        System.out.println("SQL : ViewSourceAction: " + sql);

        TableTabbedPane tabpane = session.getTableTabbedPane();

        Tab tab = new SQLEditor().createTab(sql);

        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    // --------------------------------------------------------------------------

    private void showInfo(String msg) {
        Session.getInstance().getStatusField().showInfo(msg);
    }

    private void showStatus(String msg) {
        Session.getInstance().getStatusField().showInfo(msg);
    }

    private void showError(String msg) {
        Session.getInstance().getStatusField().showError(msg);
    }

    private void showError(String msg, Throwable t) {
        Session.getInstance().getStatusField().showError(msg, t);
    }

    private void showException(String msg, Throwable t) {
        Session.getInstance().getStatusField().showError(msg, t);
    }
}
