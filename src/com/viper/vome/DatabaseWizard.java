/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2008/06/15
 *
 * Copyright 1998-2014 by Viper Software Services
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

import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Database;
import com.viper.vome.jfx.UIUtil;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class DatabaseWizard extends BorderPane {

    protected Database database = new Database();

    public DatabaseWizard() throws Exception { 

        Session session = Session.getInstance();

        Connection connection = session.getConnection();
        
        List<String> charsetNames = UIUtil.asUniqueSortedList(connection.loadCharacterSets(), "CHARACTER_SET_NAME");
        List<String> collationNames = UIUtil.asUniqueSortedList(connection.loadCollations(), "COLLATION_NAME");
        List<String> databaseNames = UIUtil.asUniqueSortedList(connection.getDatabases(), "name");

        FlowPane headerPane = new FlowPane();
        headerPane.getChildren().add(UIUtil.newLabel("Database:"));
        headerPane.getChildren().add(UIUtil.newLabel(database, "name"));

        GridPane centerPane = new GridPane();
        centerPane.setHgap(10); // horizontal gap in pixels => that's what you are asking for
        centerPane.setVgap(10); // vertical gap in pixels
        centerPane.setPadding(new Insets(10, 10, 10, 10));

        add(centerPane, 1, 1, 1, UIUtil.newLabel("Database Name"));
        add(centerPane, 2, 1, 1, UIUtil.newComboBox(database, "name", databaseNames));

        add(centerPane, 1, 2, 1, UIUtil.newLabel("Character Set Name:"));
        add(centerPane, 2, 2, 1, UIUtil.newComboBox(database, "charsetName", charsetNames));

        add(centerPane, 1, 3, 1, UIUtil.newLabel("Collation Name:"));
        add(centerPane, 2, 3, 1, UIUtil.newComboBox(database, "collationName", collationNames));

        FlowPane bottomPane = new FlowPane(5, 5);
        bottomPane.getChildren().add(UIUtil.newButton("Create Database", null, null, this::CreateDatabaseAction));

        setTop(headerPane);
        setCenter(centerPane);
        setBottom(bottomPane);

    }

    public void CreateDatabaseAction(ActionEvent e) {
        Session session = Session.getInstance();
        String collationName = session.getDatabase().getCollationName();
        if (collationName == null || collationName.length() == 0) {
            UIUtil.showError("Please enter the Collation Name.");
            return;
        }

        String databaseName = database.getName();

        try {
            Connection connection = session.openConnection();

            // String errmsg =
            // validationMgr.validateDatabaseName(session.getDatabases().getMetadata(),
            // database.getName());
            // if (errmsg != null) {
            // UIUtil.showError(errmsg);
            // return null;
            // }
            if (connection.findDatabase(databaseName) == null) {
                database = new Database();
                database.setName(databaseName);
            }

            connection.create(database);

            session.setDatabase(database);

        } catch (Exception ex) {
            UIUtil.showException("Add database " + database.getName(), ex);
        }
    }

    private final void add(GridPane pane, int column, int row, int ncols, Control child) {
        GridPane.setConstraints(child, column, row, ncols, 1); // column=2 row=0
        pane.getChildren().add(child);
    }

}
