/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2010/06/15
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

import java.util.Collections;
import java.util.List;

import com.viper.vome.dao.Column;
import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Table;
import com.viper.vome.jfx.UIUtil;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class ColumnWizard extends BorderPane {

    protected Column column = new Column();

    public ColumnWizard(Table table) {

        Session session = Session.getInstance();

        List<String> columnNames = UIUtil.asList(session.getSelectedTableView().getColumns(), "text");
        Collections.sort(columnNames);

        List<String> dataTypes = Column.getDataTypes();
        Collections.sort(dataTypes);

        GridPane centerPane = new GridPane();
        centerPane.setHgap(10); // horizontal gap in pixels => that's what you are asking for
        centerPane.setVgap(10); // vertical gap in pixels
        centerPane.setPadding(new Insets(10, 10, 10, 10));

        add(centerPane, 1, 1, 1, UIUtil.newLabel("Table:"));
        add(centerPane, 2, 1, 2, UIUtil.newLabel(table, "name"));

        add(centerPane, 1, 2, 1, UIUtil.newLabel("Column Name"));
        add(centerPane, 2, 2, 2, UIUtil.newComboBox(column, "name", columnNames, this::SelectColumnHandler));

        add(centerPane, 1, 3, 1, UIUtil.newLabel("Data Type"));
        add(centerPane, 2, 3, 2, UIUtil.newComboBox(column, "dataType", dataTypes, null));

        add(centerPane, 1, 4, 1, UIUtil.newLabel("Is Primary Key?"));
        add(centerPane, 2, 4, 2, UIUtil.newCheckBox("ColumnWizard.is-primary-key", column, "primaryKey"));

        add(centerPane, 1, 5, 1, UIUtil.newLabel("Is Required? (nullable)"));
        add(centerPane, 2, 5, 2, UIUtil.newCheckBox("ColumnWizard.is-required", column, "nullable"));

        add(centerPane, 1, 6, 1, UIUtil.newLabel("Column Size"));
        add(centerPane, 2, 6, 2, UIUtil.newSpinner(column, "columnSize", 1, 4096));

        add(centerPane, 1, 7, 1, UIUtil.newLabel("Decimal Digits"));
        add(centerPane, 2, 7, 2, UIUtil.newSpinner(column, "decimalDigits", 1, 64));

        add(centerPane, 1, 8, 1, UIUtil.newLabel("Comments:"));
        add(centerPane, 2, 8, 2, UIUtil.newScrollPane(UIUtil.newTextArea(column, "remarks", 40, 5)));

        setCenter(centerPane);
    }

    Column getColumn() {
        return column;
    }

    private final void add(GridPane pane, int column, int row, int ncols, Control child) {
        GridPane.setConstraints(child, column, row, ncols, 1); // column=2 row=0
        pane.getChildren().add(child);
    }

    public void SelectColumnHandler(ActionEvent event) {

        Session session = Session.getInstance();

        ComboBox<String> combobox = (ComboBox<String>) event.getTarget();
        System.err.println("SelectColumnHandler: " + combobox);

        String name = combobox.getValue();
        if (name == null || name.length() == 0) {
            UIUtil.showError("Please enter a column name: " + name);
            return;
        }

        Column column = session.getTable().findColumn(name);
        if (column == null) {
            column = new Column();
            column.setTableName(name);
        }

        System.out.println("ComboBox Action (selected: " + name + ")");
        // DatabaseUtil.copyFields(column, getColumn());
    }

    // -------------------------------------------------------------------------

    public void handleOKAction(ActionEvent event) {
        if (column == null) {
            UIUtil.showError("Column not setup null value: " + column);
            return;
        }

        try {

            Session session = Session.getInstance();

            Connection connection = session.openConnection();

            Table table = session.getTable();

            if (table.findColumn(column.getName()) == null) {
                connection.addColumn(table, column);
            } else {
                connection.alterColumn(table, column, column);
            }

            session.getTable().getColumns().add(column);
            session.setColumn(column);

        } catch (Exception ex) {
            UIUtil.showException("Failure ColumnNamePane", ex);
        }
    }
}
