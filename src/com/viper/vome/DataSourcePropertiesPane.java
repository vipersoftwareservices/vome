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

import com.viper.vome.dao.Connections;
import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.model.DatabaseConnections;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class DataSourcePropertiesPane extends BorderPane {

    public DataSourcePropertiesPane() {
        super();

        Session session = Session.getInstance();

        DatabaseConnections connections = session.getDatabaseConnections();
        TableView<DatabaseConnection> tableView = UIUtil.newTableView("parameters-data-properties", DatabaseConnection.class,
                connections.getConnections());

        UIUtil.newTableColumn(tableView, "Name", "name", 0.25);
        UIUtil.newTableColumn(tableView, "DatabaseUrl", "databaseUrl", 0.15);
        UIUtil.newTableColumn(tableView, "Username", "username", 0.15);
        UIUtil.newTableColumn(tableView, "Password", "password", 0.15);
        UIUtil.newTableColumn(tableView, "Driver", "driver", 0.15);
        UIUtil.newTableColumn(tableView, "Model", "model", 0.15);

        FlowPane buttonPane = new FlowPane(5.0, 5.0);
        buttonPane.setAlignment(Pos.BASELINE_CENTER);
        buttonPane.getChildren().add(UIUtil.newButton("Save", new SaveAction()));

        setTop(UIUtil.newLabel("Data Source Properties"));
        setCenter(UIUtil.newScrollPane(tableView));
        setBottom(buttonPane);
    }

    class SaveAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            try {

                Session session = Session.getInstance();
                Connections.saveDatabaseConnections(null);
                session.setDatabaseConnections(session.getDatabaseConnections());
            } catch (Exception ex) {
                UIUtil.showException("", ex);
            }
        }
    }
}
