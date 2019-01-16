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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Database;
import com.viper.vome.dao.ForeignKey;
import com.viper.vome.dao.Table;
import com.viper.vome.jfx.UIUtil;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class ForeignKeyWizard extends BorderPane {

    protected Session session = null;
    protected ForeignKey foreignKey = new ForeignKey();
 
    public ForeignKeyWizard() { 

        try {
            Session session = Session.getInstance();

            List<String> names = UIUtil.asList(session.getTable().getForeignKeys(), "name");
            List<String> constraintNames = UIUtil.asList(session.getTable().getForeignKeys(), "constraintName");

            Connection connection = session.openConnection();
            List<String> databaseNames = connection.listDatabaseNames();

            List<String> list = new ArrayList<String>();
            Database foreignDatabase = connection.findDatabase(foreignKey.getFkTableDatabase());
            if (foreignDatabase != null) {
                list = foreignDatabase.listTableNames();
            }
            Table table = session.getTable();

            Table foreignTable = foreignDatabase.findTable(foreignKey.getFkTableName());

            List<String> localList = table.listColumnNames();
            List<String> foreignList = foreignTable.listColumnNames();

            FlowPane headerPane = new FlowPane();
            headerPane.getChildren().add(UIUtil.newLabel("Table:"));
            headerPane.getChildren().add(UIUtil.newLabel(session.getTable(), "Name"));
            headerPane.getChildren().add(UIUtil.newLabel("Foreign Key:"));
            headerPane.getChildren().add(UIUtil.newLabel(foreignKey, "name"));

            GridPane centerPane = new GridPane();
            centerPane.setHgap(10); // horizontal gap in pixels => that's what you are asking for
            centerPane.setVgap(10); // vertical gap in pixels
            centerPane.setPadding(new Insets(10, 10, 10, 10));

            add(centerPane, 1, 1, 1, UIUtil.newLabel("ForeignKey Name"));
            add(centerPane, 2, 1, 1, UIUtil.newTextField(foreignKey, "name", 0));
            add(centerPane, 3, 1, 1, UIUtil.newLabel("Existing ForeignKey Names"));
            add(centerPane, 4, 1, 1, UIUtil.newScrollListView(foreignKey, "name", names));

            add(centerPane, 1, 2, 1, UIUtil.newLabel("Constraint Name"));
            add(centerPane, 2, 2, 1, UIUtil.newTextField(foreignKey, "constraintName", 0));
            add(centerPane, 3, 2, 1, UIUtil.newLabel("Existing Constraint Names"));
            add(centerPane, 4, 2, 1, UIUtil.newScrollListView(foreignKey, "constraintName", names));

            add(centerPane, 1, 3, 1, UIUtil.newCheckBox("Is Unique?", foreignKey, "unique"));
            add(centerPane, 2, 3, 3, UIUtil.newHTMLEditor("", LocaleUtil.getProperty("ColumnWizard.is-foreign-key-unique.text")));

            add(centerPane, 1, 4, 1, UIUtil.newLabel("Foreign Database"));
            add(centerPane, 2, 4, 1, UIUtil.newTextField(foreignKey, "foreignCatalog", 0));
            add(centerPane, 3, 4, 1, UIUtil.newLabel("Existing Database(s)"));
            add(centerPane, 4, 4, 1, UIUtil.newScrollListView(foreignKey, "foreignCatalog", list));

            add(centerPane, 1, 5, 1, UIUtil.newLabel("Foreign Table"));
            add(centerPane, 2, 5, 1, UIUtil.newTextField(foreignKey, "foreignTable", 0));
            add(centerPane, 3, 5, 1, UIUtil.newLabel("Existing Table(s)"));
            add(centerPane, 4, 5, 1, UIUtil.newScrollListView(foreignKey, "foreignTable", list));

            add(centerPane, 1, 6, 1, UIUtil.newScrollListView(foreignKey, "name", localList));
            add(centerPane, 2, 6, 3, UIUtil.newScrollListView(foreignKey, "name", foreignList));

            FlowPane bottomPane = new FlowPane(5, 5);
            bottomPane.getChildren().add(UIUtil.newButton("Export", null, null, this::FinishPane));

            setTop(headerPane);
            setCenter(centerPane);
            setBottom(bottomPane);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public ForeignKey getForeignKey() {
        return foreignKey;
    }

    private final void add(GridPane pane, int column, int row, int ncols, Control child) {
        GridPane.setConstraints(child, column, row, ncols, 1); // column row colspan rowspan
        pane.getChildren().add(child);
    }

    public void ForeignKeyNameAction(ActionEvent e) {

        try {

            String name = foreignKey.getName();
            if (name == null || name.length() == 0) {
                UIUtil.showError("Please enter ForeignKey name");
                return;
            }

            Table table = session.getTable();
            Connection connection = session.openConnection();
            connection.loadForeignKeys(table);
            if (table.getForeignKeys().size() == 0) {
                UIUtil.showError("");
                return;
            }

            foreignKey = table.findForeignKey(name);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ForeignKeyConstraintName(ActionEvent e) {
        String name = foreignKey.getName();
        if (name == null || name.length() == 0) {
            UIUtil.showError("Please enter ForeignKey Constraint Name");
            return;
        }

        Table table = session.getTable();
        try {
            Connection connection = session.openConnection();
            connection.loadForeignKeys(table);
            if (table.getForeignKeys().size() == 0) {
                UIUtil.showError("");
                return;
            }
            if (table.findForeignKey(name) == null) {
                UIUtil.showError("");
                return;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void FinishPane(ActionEvent e) {
        if (finishAction() == null) {
            return;
        }
        if (foreignKey == null || foreignKey.getName() == null) {
            return;
        }

        Table table = session.getTable();
        if (table.findForeignKey(foreignKey.getName()) != null) {
            UIUtil.showError("Foreign key already defined: " + foreignKey.getName());
            return;
        }
        session.setForeignKey(foreignKey);
    }

    public void propertyChange(PropertyChangeEvent evt) {

        Vector<String> names = new Vector<String>();
        names.add("");
        names.add("");

        Vector<Vector<String>> rows = new Vector<Vector<String>>();
        addRow(rows, "Foreign Key Name: ", foreignKey.getName());

        // TODO jtable.setModel(new DefaultTableModel(rows, names));
    }

    public void addRow(Vector<Vector<String>> rows, String name, Object value) {
        Vector<String> row = new Vector<String>();
        row.add(name);
        if (value != null) {
            row.add(value.toString());
        }
        rows.add(row);
    }

    // -------------------------------------------------------------------------

    public ForeignKey finishAction() {

        Table table = session.getTable();

        try {

            Connection connection = session.openConnection();
            connection.loadForeignKeys(table);
            if (table.getForeignKeys().size() == 0) {
                UIUtil.showError("");
                return null;
            }

            if (table.findForeignKey(foreignKey.getName()) == null) {
                table.getForeignKeys().add(foreignKey);
            }

            connection.createForeignKey(foreignKey);
            session.setForeignKey(foreignKey);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return foreignKey;
    }
}
