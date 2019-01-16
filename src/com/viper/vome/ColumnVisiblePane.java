
/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java    1.00 2008/06/15
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

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.CheckListView;

import com.viper.vome.jfx.UIUtil;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.BorderPane;

public class ColumnVisiblePane extends BorderPane {

    public ColumnVisiblePane(List<TableColumn> items) {

        List<String> titles = new ArrayList<String>();
        for (TableColumn column : items) {
            titles.add(column.getText());
        }

        CheckListView checkListView = new CheckListView();
        checkListView.setItems(FXCollections.observableArrayList(titles));

        for (TableColumn column : items) {
            if (column.isVisible()) {
                checkListView.getCheckModel().check(column.getText());
            } else {
                checkListView.getCheckModel().clearCheck(column.getText());
            }
        }

        checkListView.getCheckModel().getCheckedIndices().addListener(new CheckBoxChangeListener(checkListView, items));
    }

    public class CheckBoxChangeListener implements ListChangeListener<String> {

        CheckListView checkListView = null;
        List<TableColumn> items = null;

        public CheckBoxChangeListener(CheckListView checkListView, List<TableColumn> items) {
            this.checkListView = checkListView;
            this.items = items;
        }

        public void onChanged(ListChangeListener.Change<? extends String> c) {
            if (c.wasAdded()) {
                for (String name : c.getAddedSubList()) {
                    int index = indexOf(items, name);
                    if (index != -1) {
                        TableColumn column = items.get(index);
                        column.setVisible(true);
                    }
                }
            }
            if (c.wasRemoved()) {
                for (String name : c.getRemoved()) {
                    int index = indexOf(items, name);
                    if (index != -1) {
                        TableColumn column = items.get(index);
                        column.setVisible(false);
                    }
                }
            }
        }
    }

    private static final int indexOf(List<TableColumn> items, String name) {
        for (int index = 0; index < items.size(); index++) {
            TableColumn column = items.get(index);
            if (name.equalsIgnoreCase(column.getText())) {
                return index;
            }
        }
        return -1;
    }

    public static final boolean popCheckListPane(List<TableColumn> observableList) {

        ScrollPane scrollPane = UIUtil.newScrollPane(new ColumnVisiblePane(observableList));
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);

        ButtonType result = Dialogs.showAsDialog("Column Visibility", scrollPane, null, ButtonType.OK, ButtonType.CANCEL);
        return (result != ButtonType.CANCEL);
    }
}
