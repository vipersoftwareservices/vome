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

package com.viper.vome;

import java.util.ArrayList;
import java.util.List;

import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Row;
import com.viper.vome.dao.Table;
import com.viper.vome.jfx.UIUtil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class SQLEditor {

    private List<String> historySQL = new ArrayList<String>();

    public SQLEditor() {

    }

    public Tab createTab(String sql) {

        Session session = Session.getInstance();

        String tablename = "SQL";

        // Create the data table - right side
        TableView uiTable = UIUtil.newTableView("table-sql-editor");
        uiTable.setUserData(new Table());

        // UserDataModel.setTable(uiTable, table);
        // UserDataModel.setDatabase(uiTable, database);

        uiTable.setContextMenu(DatabaseViewer.createTablePopupMenu(session));
        uiTable.setRowFactory(new CustomRowFactory());

        // Create the message area.
        TextArea messageArea = UIUtil.newTextArea("editor.message.area", 60, 20, toString(historySQL));
        messageArea.setEditable(true);
        messageArea.setText(sql);
        messageArea.setContextMenu(createPopupMenu());

        ScrollPane tableScroller = UIUtil.newScrollPane(uiTable);
        ScrollPane messageScroller = UIUtil.newScrollPane(messageArea);

        // Create the Split Pane
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().addAll(tableScroller, messageScroller);
        splitPane.setDividerPositions(0.80, 0.20);

        Button runButton = UIUtil.newButton("Run", null);

        HBox bottomPane = new HBox();
        bottomPane.getStyleClass().add("modal-dialog-bottom");
        bottomPane.getChildren().add(runButton);

        BorderPane pane = new BorderPane();
        pane.setTop(DatabaseViewer.createTopTableToolbar(session, uiTable));
        pane.setCenter(splitPane);
        pane.setBottom(bottomPane);

        Tab tab = UIUtil.newTab(tablename, pane);
        tab.setUserData(uiTable);

        runButton.setOnAction(new RunAction(tab, uiTable));
        runButton.setUserData(messageArea);

        return tab;
    }

    public Pane createEditorPane(TableView uiTable, String sql) {

        Session session = Session.getInstance();

        // Create the message area.
        TextArea messageArea = UIUtil.newTextArea("editor.message.area", 60, 20, toString(historySQL));
        messageArea.setEditable(true);
        if (sql != null) {
            messageArea.setText(sql);
        }
        messageArea.setContextMenu(createPopupMenu());

        ScrollPane messageScroller = UIUtil.newScrollPane(messageArea);

        Button runButton = UIUtil.newButton("Run", null);

        HBox bottomPane = new HBox();
        bottomPane.getStyleClass().add("modal-dialog-bottom");
        bottomPane.getChildren().add(runButton);

        // TODO null is for the tab will fail.
        runButton.setOnAction(new RunAction(null, uiTable));
        runButton.setUserData(messageArea);

        return bottomPane;
    }

    /**
     * This method defines the popup menu for the sql query editor.
     * 
     * @param rowno
     * @param columnno
     * @return
     */
    public ContextMenu createPopupMenu() {
        ContextMenu popup = new ContextMenu();
        popup.getItems().add(UIUtil.newMenuItem("Cut", new CutListener()));
        popup.getItems().add(UIUtil.newMenuItem("Copy", new CopyListener()));
        popup.getItems().add(UIUtil.newMenuItem("Paste", new PasteListener()));
        popup.getItems().add(UIUtil.newMenuItem("Select All", new SelectAllListener()));
        return popup;
    }

    class SelectAllListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            ((TextArea) Actions.getUserData(e)).selectAll();
        }
    }

    class PasteListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            ((TextArea) Actions.getUserData(e)).paste();
        }
    }

    class CopyListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            ((TextArea) Actions.getUserData(e)).copy();
        }
    }

    class CutListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            ((TextArea) Actions.getUserData(e)).cut();
        }
    }

    private final String toString(List<String> lines) {
        StringBuilder buf = new StringBuilder();
        for (String line : lines) {
            buf.append(line);
            buf.append("\n");
        }
        return buf.toString();
    }

    class RunAction implements EventHandler<ActionEvent> {

        TableView uiTable = null;
        Tab tab = null;

        public RunAction(Tab tab, TableView uiTable) {
            this.uiTable = uiTable;
            this.tab = tab;
        }

        @Override
        public void handle(ActionEvent e) {
            String sql =  ((TextArea) Actions.getUserData(e)).getText();
            if (sql == null || sql.length() == 0) {
                UIUtil.showError("Please enter a sql command.");
                return;
            }

            try {
                Session session = Session.getInstance();

                Connection connection = session.openConnection();
                if (connection == null) {
                    UIUtil.showError("Please select a database connection.");
                    return;
                }

                List<Row> rows = connection.executeQueryRows(sql);

                uiTable.setRowFactory(new CustomRowFactory());
                uiTable.getColumns().setAll(getColumns(rows));
                uiTable.getItems().setAll(rows);

                tab.setText(sql);

            } catch (Exception ex) {
                UIUtil.showException("Unable to execute sql command: " + sql, ex);
            }
        }

        private Callback getCellMapFactory() {
            Callback<TableColumn<Row, Object>, TableCell<Row, Object>> cellFactoryForMap = new Callback<TableColumn<Row, Object>, TableCell<Row, Object>>() {
                @Override
                public TableCell<Row, Object> call(TableColumn<Row, Object> p) {
                    return new TextFieldTableCell<Row, Object>(new StringConverter<Object>() {
                        @Override
                        public String toString(Object t) {
                            return (t == null) ? null : t.toString();
                        }

                        @Override
                        public Object fromString(String string) {
                            return string;
                        }
                    });
                }
            };
            return cellFactoryForMap;
        }

        private List<TableColumn<Row, Object>> getColumns(List<Row> rows) {
            List<TableColumn<Row, Object>> columns = new ArrayList<TableColumn<Row, Object>>();
            Callback callback = getCellMapFactory();
            if (rows != null && rows.size() > 0) {
                for (String name : rows.get(0).keySet()) {
                    TableColumn<Row, Object> col = new TableColumn<Row, Object>(name);
                    col.setCellValueFactory(new MapValueFactory(name));
                    col.setCellFactory(callback);
                    columns.add(col);
                }
            }
            return columns;
        }
    }
}
