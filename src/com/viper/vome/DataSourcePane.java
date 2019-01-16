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

import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Connections;
import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.model.DatabaseConnections;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class DataSourcePane extends BorderPane {

    SimpleStringProperty messageProperty = new SimpleStringProperty("");

    public DataSourcePane() {

        setMaxSize(400.0, 400.0);
    }

    public static final void showAsDialog(String title, DatabaseConnection dbc) {
        DataSourcePane pane = new DataSourcePane();
        pane.initialize(dbc);

        DatabaseConnection result = Dialogs.showAsDialog(title, pane, new Callback<ButtonType, DatabaseConnection>() {
            @Override
            public DatabaseConnection call(ButtonType b) {
                if (b == ButtonType.OK) {
                    if (pane.validate(dbc)) {
                        return dbc;
                    }
                }
                return null;
            }
        });
        if (result != null) {
            try {
                pane.applyAction(dbc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initialize(DatabaseConnection dbc) {

        if (dbc == null) {
            dbc = new DatabaseConnection();
            dbc.setName("");
        }

        Session session = Session.getInstance();

        DatabaseConnections connections = session.getDatabaseConnections();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox nameBox = UIUtil.newComboBoxBeans(dbc, "name", connections.getConnections(), new SelectAction(dbc));
        nameBox.setPromptText("<< New Connection Name >>");
        grid.add(UIUtil.newLabel("Name"), 0, 0);
        grid.add(nameBox, 1, 0);
        grid.add(UIUtil.newInfoButton("connection.name"), 2, 0);

        grid.add(UIUtil.newLabel("URL:"), 0, 1);
        grid.add(UIUtil.newComboBoxBeans(dbc, "databaseUrl", connections.getConnections(), null), 1, 1);
        grid.add(UIUtil.newInfoButton("connection.database.url"), 2, 1);

        grid.add(UIUtil.newLabel("Username:"), 0, 2);
        grid.add(UIUtil.newComboBoxBeans(dbc, "username", connections.getConnections(), null), 1, 2);
        grid.add(UIUtil.newInfoButton("connection.database.username"), 2, 2);

        grid.add(UIUtil.newLabel("Password:"), 0, 3);
        grid.add(UIUtil.newPasswordField(dbc, "password", null), 1, 3);
        grid.add(UIUtil.newInfoButton("connection.database.password"), 2, 3);

        grid.add(UIUtil.newLabel("Driver:"), 0, 4);
        grid.add(UIUtil.newComboBoxBeans(dbc, "driver", connections.getConnections(), null), 1, 4);
        grid.add(UIUtil.newInfoButton("connection.database.driver"), 2, 4);

        grid.add(UIUtil.newLabel("Model:"), 0, 5);
        HBox hbox = new HBox();
        hbox.getChildren().add(UIUtil.newComboBoxBeans(dbc, "model", connections.getConnections(), null));
        hbox.getChildren().add(UIUtil.newButton("Browse...", new ModelFilenameButton(dbc)));
        grid.add(hbox, 1, 5);
        grid.add(UIUtil.newInfoButton("connection.database.model"), 2, 5);

        grid.add(UIUtil.newLabel(messageProperty), 0, 6, 3, 1);

        FlowPane buttonPane = new FlowPane(5.0, 5.0);
        buttonPane.setAlignment(Pos.BASELINE_CENTER);
        buttonPane.getChildren().add(UIUtil.newButton("Test", new TestAction(dbc)));

        setCenter(grid);
        setBottom(buttonPane);
    }

    ColumnConstraints newColumnConstraints(double percent, HPos value) {
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setPercentWidth(percent);
        constraints.setFillWidth(true);
        if (value != null) {
            constraints.setHalignment(value);
        }
        return constraints;
    }

    void showError(String message) {
        messageProperty.setValue(message);
    }

    void showSuccess(String message) {
        messageProperty.setValue(message);
    }

    public boolean validate(DatabaseConnection dbc) {

        showError("");

        if (dbc == null) {
            showError("Internal error connection bean is null.");
            return false;
        }
        if (dbc.getDatabaseUrl() == null || dbc.getDatabaseUrl().length() == 0) {
            showError("Please enter the database URL.");
            return false;
        }
        if (dbc.getUsername() == null || dbc.getUsername().length() == 0) {
            showError("Please enter the username.");
            return false;
        }
        if (dbc.getPassword() == null || dbc.getPassword().length() == 0) {
            showError("Please enter the password.");
            return false;
        }
        if (dbc.getDriver() == null || dbc.getDriver().length() == 0) {
            showError("Please enter the database driver classname.");
            return false;
        }
        if (dbc.getModel() == null || dbc.getModel().length() == 0) {
            showError("Please enter the model filename.");
            return false;
        }
        return true;
    }

    public final boolean applyAction(DatabaseConnection dbc) throws Exception {

        Session session = Session.getInstance();

        DatabaseConnections connections = session.getDatabaseConnections();
        if (!connections.getConnections().contains(dbc)) {
            connections.getConnections().add(dbc);
        }
        session.setDatabaseConnections(connections);
        session.setDatabaseConnection(dbc);

        System.out.println("DataSourcePane.applyAction: save connections.");
        Connections.saveDatabaseConnections(null);

        return true;
    }

    class SelectAction implements EventHandler<ActionEvent> {
        DatabaseConnection dbc = null;

        public SelectAction(DatabaseConnection dbc) {
            this.dbc = dbc;
        }

        @Override
        public void handle(ActionEvent event) {

            Session session = Session.getInstance();

            String name = dbc.getName();
            if (name == null || name.length() == 0) {
                showError("Please enter a connection name");
                return;
            }
            DatabaseConnection connection = Connections.findDatabaseConnection(name);
            if (connection == null) {
                showError("Non existant connection name.");
                return;
            }

            initialize(connection);
        }
    }

    class TestAction implements EventHandler<ActionEvent> {

        DatabaseConnection dbc = null;

        public TestAction(DatabaseConnection dbc) {
            this.dbc = dbc;
        }

        @Override
        public void handle(ActionEvent e) {

            if (validate(dbc)) {
                Session session = Session.getInstance();
                if (dbc == null) {
                    showError("Unable to open connection: not specified.");
                    return;
                }

                try {
                    Connection connection = session.openConnection(dbc);
                    showSuccess("SUCCESS: opened connection " + dbc.getDatabaseUrl());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("FAILED: Unable to open connection: " + dbc.getDatabaseUrl() + "," + ex.getMessage());
                }
            }
        }
    }

    // -------------------------------------------------------------------------

    class ModelFilenameButton implements EventHandler<ActionEvent> {

        DatabaseConnection dbc = null;

        public ModelFilenameButton(DatabaseConnection dbc) {
            this.dbc = dbc;
        }

        @Override
        public void handle(ActionEvent e) {
            Session session = Session.getInstance();

            String filename = UIUtil.showOpenDialog(session.getStage(), "Model Filename", dbc.getModel());
            if (filename == null) {
                return;
            }
            dbc.setModel(filename);
        }
    }
}
