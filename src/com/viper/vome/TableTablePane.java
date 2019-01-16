/*
 * --------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * --------------------------------------------------------------
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
 * ---------------------------------------------------------------
 */

package com.viper.vome;

import com.viper.vome.dao.Table;
import com.viper.vome.jfx.UIUtil;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class TableTablePane extends BorderPane {

    public TableTablePane() {
        super();

        Session session = Session.getInstance();

        TableView<?> tableView = UIUtil.newTableView("tablepane.table", Table.class, session.getDatabase().getTables());

        FlowPane topPane = new FlowPane();

        String label = "Tables for: " + session.getDatabase().getName();
        topPane.getChildren().add(UIUtil.newLabel(label));

        FlowPane bottomPane = new FlowPane();
        bottomPane.getChildren().add(UIUtil.newButton("Add", new AddAction(tableView)));
        bottomPane.getChildren().add(UIUtil.newButton("Delete", new DeleteAction(tableView)));
        bottomPane.getChildren().add(UIUtil.newButton("Rename", new RenameAction(tableView)));

        setTop(topPane);
        setCenter(UIUtil.newScrollPane(tableView));
        setBottom(bottomPane);
    }

    class AddAction implements EventHandler<ActionEvent> {

        private TableView<?> tableView;

        public AddAction(TableView<?> tableView) {
            this.tableView = tableView;
        }

        @Override
        public void handle(ActionEvent e) {
            if (tableView == null) {
                UIUtil.showError("NO table view found for id: indexpane.table");
                return;
            }
            tableView.getColumns().add(new TableColumn("<NEW COLUMN>"));
        }
    }

    class DeleteAction implements EventHandler<ActionEvent> {

        private TableView tableView;

        public DeleteAction(TableView tableView) {
            this.tableView = tableView;
        }

        @Override
        public void handle(ActionEvent e) {
            if (tableView.getSelectionModel().getSelectedItems() == null) {
                UIUtil.showMessage("Please select columns(s) to delete.");
                return;
            }
            String msg = "Confirm " + tableView.getSelectionModel().getSelectedItems().size() + " columns to be deleted";
            if (UIUtil.askForConfirmation(msg)) {
                return;
            }
            ObservableList<Integer> columns = tableView.getSelectionModel().getSelectedIndices();
            for (int colno : columns) {
                tableView.getColumns().remove(colno);
            }
        }
    }

    class RenameAction implements EventHandler<ActionEvent> {

        private TableView tableView;

        public RenameAction(TableView tableView) {
            this.tableView = tableView;
        }

        @Override
        public void handle(ActionEvent e) {

        }
    }
}
