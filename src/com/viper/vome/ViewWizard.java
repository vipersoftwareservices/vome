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

import java.util.List;

import com.viper.vome.dao.Column;
import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Database;
import com.viper.vome.dao.Driver;
import com.viper.vome.dao.Table;
import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.TableType;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class ViewWizard extends BorderPane {

    protected Table table = new Table();

    public ViewWizard(Table current) {

        this.table.setDatabaseName("");
        this.table.setName("");
        this.table.setTableType(TableType.VIEW);

        if (current != null) {
            this.table = current;
        }

        Session session = Session.getInstance();
        Database database = session.getDatabase();
        List<String> names = database.listTableNames();
        List<String> list = Driver.listTableTypes();

        FlowPane headerPane = new FlowPane();
        headerPane.getChildren().add(UIUtil.newLabel("Database:"));
        headerPane.getChildren().add(UIUtil.newLabel(session.getDatabase(), "name"));

        GridPane centerPane = new GridPane();
        centerPane.setHgap(10); // horizontal gap in pixels => that's what you are asking for
        centerPane.setVgap(10); // vertical gap in pixels
        centerPane.setPadding(new Insets(10, 10, 10, 10));

        add(centerPane, 1, 1, 1, UIUtil.newLabel("View Name"));
        add(centerPane, 2, 1, 1, UIUtil.newComboBox(table, "name", names));

        add(centerPane, 1, 2, 2, UIUtil.newLabel("SQL:"));
        add(centerPane, 1, 3, 2, UIUtil.newTextArea(table, "sql", 40, 8));

        FlowPane bottomPane = new FlowPane();
        bottomPane.getChildren().add(UIUtil.newButton("Create/Modify View", this::FinishAction));

        setTop(headerPane);
        setCenter(centerPane);
        setBottom(bottomPane);
    }

    private final void add(GridPane pane, int column, int row, int ncols, Control child) {
        GridPane.setConstraints(child, column, row, ncols, 1); // column row colspan rowspan
        pane.getChildren().add(child);
    }

    public void TableNamePane(ActionEvent e) {
        Session session = Session.getInstance();
        Database database = session.getDatabase();

        String name = session.getTable().getName();
        if (name == null || name.length() == 0) {
            UIUtil.showError("Please enter a table name");
            return;
        }
    }

    public void FinishAction(ActionEvent e) {
        if (finishAction() == null) {
            return;
        }

        Session session = Session.getInstance();
        Database database = session.getDatabase();
        List<Table> tables = database.getTables();
        if (database.findTable(table.getName()) != null) {
            UIUtil.showError("Table already exist: " + table.getName());
            return;
        }

        // TODO add table with the SQL
        tables.add(table);
        session.setTable(table);

    }
    // -------------------------------------------------------------------------

    public Table finishAction() {
        Session session = Session.getInstance();
        TableType tableType = session.getTable().getTableType();
        if (tableType == null) {
            UIUtil.showError("Please enter the Table Type: ");
            return null;
        }

        Database database = session.getDatabase();
        List<Table> tables = database.getTables();

        Table table = session.getTable();

        try {
            String errmsg = null; // validationMgr.validateTableName(session.getDatabases().getMetadata(),
                                  // table.getName());
            if (errmsg != null) {
                UIUtil.showError(errmsg);
                return null;
            }

            if (table.getTableType() == null) {
                table.setTableType(TableType.TABLE);
            }

            Column column = new Column();
            column.setName("NewColumn");
            // column.setJavaType("String");
            table.getColumns().add(column);

            Connection connection = session.openConnection();
            connection.create(table);

            tables.add(table);
            session.setTable(table);

            return table;

        } catch (Exception ex) {
            String msg = "Add table " + database.getName() + "." + table.getName();
            UIUtil.showException(msg, ex);
        }
        return null;
    }
}
