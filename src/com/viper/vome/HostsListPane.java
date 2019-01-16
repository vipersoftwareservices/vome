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

import java.io.IOException;
import java.net.Socket;

import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.DatabaseConnections;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class HostsListPane extends BorderPane {

    private SimpleStringProperty messageProperty = new SimpleStringProperty("");
    private TextField hostTF;
    private ListView<String> listView;

    public HostsListPane() {

        Session session = Session.getInstance();
        DatabaseConnections connections = session.getDatabaseConnections();

        setMaxSize(300.0, 200.0);

        VBox vbox = new VBox();

        hostTF = new TextField();

        listView = UIUtil.newListView(connections.getHosts());
        listView.setPrefHeight(200.0);
        listView.setPrefWidth(300.0);

        vbox.getChildren().add(listView);
        vbox.getChildren().add(UIUtil.newLabel("Enter Host Name or IP Address"));
        vbox.getChildren().add(hostTF);

        FlowPane buttonPane = new FlowPane(5.0, 5.0);
        buttonPane.setAlignment(Pos.BASELINE_CENTER);
        buttonPane.getChildren().add(UIUtil.newButton("Add", new AddHostsAction()));
        buttonPane.getChildren().add(UIUtil.newButton("Delete", new DeleteHostsAction()));
        buttonPane.getChildren().add(UIUtil.newButton("Test", new InterrogateHostsAction(connections)));
        buttonPane.getChildren().add(UIUtil.newInfoButton("connection.name"));

        setTop(UIUtil.newLabel("List of Hosts to Interrogate"));
        setCenter(vbox);
        setBottom(buttonPane);
    }

    void showError(String message) {
        messageProperty.setValue(message);
    }

    void showSuccess(String message) {
        messageProperty.setValue(message);
    }

    public void saveHostsAction() {

        Session session = Session.getInstance();
        DatabaseConnections connections;

        // TODO

        System.out.println("DataSourcePane.applyAction: save connections.");
        // Connections.saveDatabaseConnections(null);
    }

    class InterrogateHostsAction implements EventHandler<ActionEvent> {

        DatabaseConnections connections;

        public InterrogateHostsAction(DatabaseConnections connections) {
            this.connections = connections;
        }

        @Override
        public void handle(ActionEvent e) {

            ObservableList<String> items = listView.getItems();

            int portno = 3306;
            for (String host : items) {
                if (isListening(host, portno)) {
                    System.out.println("Database WAS found at host/port:" + host + "," + portno);
                } else {
                    System.out.println("Database NOT found at host/port:" + host + "," + portno);
                    
                }
            }
        }
    }

    class AddHostsAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            String value = hostTF.getText();
            listView.getItems().add(value);
        }
    }

    class DeleteHostsAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            ObservableList<String> selectedItems = listView.getSelectionModel().getSelectedItems();
            listView.getItems().removeAll(selectedItems);
        }
    }

    private boolean isListening(String host, int port) {
        Socket s = null;
        try {
            s = new Socket(host, port);
            // setSoTimeout(60*1000)

            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    ;
                }
            }
        }
    }
}
