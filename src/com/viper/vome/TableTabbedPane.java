/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2008/01/15
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
 * @version 1.0, 01/15/2008
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.vome;

import java.util.List;

import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Database;
import com.viper.vome.dao.Row;
import com.viper.vome.dao.Table;
import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.model.DatabaseModel;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Pagination;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class TableTabbedPane extends TabPane {

    protected Session session = null;

    public TableTabbedPane(Session session) {
        super();

        setId("table.tab.pane");

        this.session = session;

        session.setTableTabbedPane(this);
        setContextMenu(DatabaseViewer.createTabPopupMenu(session));
    }

    // --------------------------------------------------------------------------

    public TableView<Row> createTableItem(Table table) throws Exception {
        TableView<Row> uiTable = null;

        Database database = session.getDatabase();
        if (database == null || table == null) {
            System.err.println("FYI: database=" + database + ", table=" + table);
            return null;
        }

        Tab tab = findTab(getTabs(), table.getName());
        System.err.println("Find tab: " + table.getName() + "," + getTabs().size() + ":" + tab);

        if (tab == null) {

            // Create the data table - right side
            uiTable = UIUtil.newTableView(table);

            uiTable.setContextMenu(DatabaseViewer.createTablePopupMenu(session));
            uiTable.setRowFactory(new CustomRowFactory());

            Connection connection = session.openConnection();
            Pagination pagination = DatabaseModel.createPagination(uiTable, connection, table);

            uiTable.getColumns().setAll(DatabaseModel.getColumns(uiTable, table));

            VBox bottomPane = new VBox();
            bottomPane.getChildren().add(DatabaseViewer.createTableEditToolbar(session));
            bottomPane.getChildren().add(pagination);

            BorderPane pane = new BorderPane();
            pane.setPadding(new Insets(10));
            pane.setTop(DatabaseViewer.createTopTableToolbar(session, uiTable));
            pane.setCenter(UIUtil.newScrollPane(uiTable));
            pane.setBottom(bottomPane);

            tab = UIUtil.newTab(table.getName(), pane);
            tab.setUserData(uiTable);

            getTabs().add(tab);

        } else {
            uiTable = (TableView) tab.getUserData();
        }

        System.err.println("Add tab: " + table.getName() + "," + getTabs().size() + ":" + tab);

        getSelectionModel().select(tab);
        return uiTable;
    }

    public <T> TableView<T> createTableItem(String tablename, Class<T> clazz, List<T> beans) throws Exception {
        TableView<T> uiTable = null;

        Session session = Session.getInstance();
        Table table = session.getTable();
        Database database = session.getDatabase();
        DatabaseConnection dbc = session.getDatabaseConnection();

        if (dbc == null || database == null || tablename == null) {
            System.err.println("FYI: dbc=" + dbc + ", database=" + database + ", table=" + tablename);
            return null;
        }

        Tab tab = findTab(getTabs(), tablename);
        System.err.println("Find tab: " + tablename + "," + getTabs().size() + ":" + tab);

        if (tab == null) {

            // Create the data table - right side
            uiTable = UIUtil.newTableView(tablename, clazz, beans);
            uiTable.setUserData(table);

            uiTable.setContextMenu(DatabaseViewer.createTablePopupMenu(session));
            uiTable.setRowFactory(new CustomRowFactory());

            BorderPane pane = new BorderPane();
            pane.setPadding(new Insets(10));
            // pane.setTop(DatabaseViewer.createTopTableToolbar(session, uiTable));
            pane.setCenter(UIUtil.newScrollPane(uiTable));
            pane.setBottom(DatabaseViewer.createTableEditToolbar(session));

            tab = UIUtil.newTab(tablename, pane);
            tab.setUserData(uiTable);

            getTabs().add(tab);

        } else {
            uiTable = (TableView) tab.getUserData();
        }

        System.err.println("Add tab: " + tablename + "," + getTabs().size() + ":" + tab);

        getSelectionModel().select(tab);
        return uiTable;
    }

    public TableView<? extends Row> createTableItem(Table table, String propertyName, List<? extends Row> beans)
            throws Exception {

        TableView<Row> uiTable = null;

        Session session = Session.getInstance();

        Tab tab = findTab(getTabs(), table.getName());

        if (tab == null) {

            // Create the data table - right side
            uiTable = UIUtil.newTableView(table.getName());

            uiTable.getColumns().setAll(DatabaseModel.getColumns(uiTable, table));
            uiTable.getItems().setAll(beans);

            uiTable.setUserData(table);

            uiTable.setContextMenu(DatabaseViewer.createTablePopupMenu(session));
            uiTable.setRowFactory(new CustomRowFactory());

            uiTable.getItems().setAll(FXCollections.observableList(beans));

            BorderPane pane = new BorderPane();
            pane.setPadding(new Insets(10));
            pane.setTop(DatabaseViewer.createTopTableToolbar(session, uiTable));
            pane.setCenter(UIUtil.newScrollPane(uiTable));
            pane.setBottom(DatabaseViewer.createEditToolbar(session, propertyName));

            tab = UIUtil.newTab(table.getName(), pane);
            tab.setUserData(uiTable);

            getTabs().add(tab);

        } else {
            uiTable = (TableView) tab.getUserData();
        }

        getSelectionModel().select(tab);
        return uiTable;
    }

    public void refresh(TableView<Row> uiTable) throws Exception {

        Table table = (Table) uiTable.getUserData();
        uiTable.getItems().setAll(FXCollections.observableList(table.getRows()));

    }

    private Tab findTab(List<Tab> tabs, String name) {
        if (tabs != null) {
            for (Tab tab : tabs) {
                if (tab.getId().equals(name)) {
                    return tab;
                }
            }
        }
        return null;
    }

    public TableView<Row> findTable(String name) {
        Tab tab = findTab(getTabs(), name);
        if (tab == null) {
            return null;
        }
        return (TableView) tab.getUserData();
    }

    public TableView getSelectedTable() {
        if (getSelectionModel().getSelectedItem() == null) {
            return null;
        }
        Tab tab = getSelectionModel().getSelectedItem();
        return (TableView) tab.getUserData();
    }

}
