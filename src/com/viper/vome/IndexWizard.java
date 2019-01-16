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

import java.util.Arrays;
import java.util.List;

import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Index;
import com.viper.vome.dao.IndexClassType;
import com.viper.vome.dao.IndexType;
import com.viper.vome.dao.Table;
import com.viper.vome.jfx.UIUtil;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class IndexWizard extends BorderPane {

    protected Index index = new Index();

    // -------------------------------------------------------------------------

    public IndexWizard( ) { 

        Session session = Session.getInstance();

        List<String> names = UIUtil.asList(session.getTable().getIndicies(), "name");

        List<IndexClassType> indexClassTypes = Arrays.asList(IndexClassType.values());
        List<IndexType> indexTypes = Arrays.asList(IndexType.values());
        List<Index> indicies = session.getTable().getIndicies();

        FlowPane headerPane = new FlowPane();
        headerPane.getChildren().add(UIUtil.newLabel("Table:"));
        headerPane.getChildren().add(UIUtil.newLabel(session.getTable(), "Name"));
        headerPane.getChildren().add(UIUtil.newLabel("Index:"));
        headerPane.getChildren().add(UIUtil.newLabel(index, "name"));

        GridPane centerPane = new GridPane();
        centerPane.setHgap(10); // horizontal gap in pixels => that's what you are asking for
        centerPane.setVgap(10); // vertical gap in pixels
        centerPane.setPadding(new Insets(10, 10, 10, 10));

        add(centerPane, 1, 1, 1, UIUtil.newLabel("Index Name"));
        add(centerPane, 2, 1, 1, UIUtil.newTextField(index, "name", 0));
        add(centerPane, 3, 1, 1, UIUtil.newLabel("Existing Index Names"));
        add(centerPane, 4, 1, 1, UIUtil.newScrollListView(index, "name", names));

        add(centerPane, 1, 2, 1, UIUtil.newLabel("Index Class"));
        add(centerPane, 2, 2, 1, UIUtil.newTextField(index, "indexClass", 0));
        add(centerPane, 3, 2, 1, UIUtil.newLabel("Existing Index Class"));
        add(centerPane, 4, 2, 1, UIUtil.newScrollListView(index, "indexClass", names));

        add(centerPane, 1, 3, 1, UIUtil.newLabel("Index Type"));
        add(centerPane, 2, 3, 1, UIUtil.newTextField(index, "indexType", 0));
        add(centerPane, 3, 3, 1, UIUtil.newLabel("Existing Index Type"));
        add(centerPane, 4, 3, 1, UIUtil.newScrollListView(index, "indexType", names));

        add(centerPane, 1, 4, 4, UIUtil.newHTMLEditor("", LocaleUtil.getProperty("ColumnWizard.index-type.text")));

        add(centerPane, 1, 5, 1, UIUtil.newLabel("Column Selection(s)"));
        add(centerPane, 2, 5, 3, UIUtil.newScrollListView(index, "Name", indicies));

        FlowPane buttonPane = new FlowPane(5, 5);
        buttonPane.getChildren().add(UIUtil.newButton("Create Index", null, null, this::FinishPane));

        setTop(headerPane);
        setCenter(centerPane);
        setBottom(buttonPane);
    }

    private final void add(GridPane pane, int column, int row, int ncols, Control child) {
        GridPane.setConstraints(child, column, row, ncols, 1); // column row colspan rowspan
        pane.getChildren().add(child);
    }

    public void IndexNamePane(ActionEvent e) {
        Session session = Session.getInstance();
        String name = index.getName();
        if (name == null || name.length() == 0) {
            UIUtil.showError("Please enter index name");
            return;
        }

        Table table = session.getTable();

        try {
            Connection connection = session.openConnection();
            connection.loadIndicies(table);
            if (table.getIndicies().size() == 0) {
                UIUtil.showError("");
                return;
            }

            Index index = table.findIndex(name);
            if (index != null) {
                // this.index = index;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void FinishPane(ActionEvent e) {
        try {
            Session session = Session.getInstance();
            if (finishAction(e) == null) {
                return;
            }
            if (index == null) {
                return;
            }
            if (session.getTable().findIndex(index.getName()) != null) {
                UIUtil.showError("Index already defined: " + index.getName());
                return;
            }

            // TODO Add index to database.
            session.getTable().getIndicies().add(index);

            // session.setIndex(index);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------

    public Index finishAction(ActionEvent e) throws Exception {

        Session session = Session.getInstance();
        Table table = session.getTable();
        Connection connection = session.openConnection();
        connection.loadIndicies(table);

        if (table.getIndicies().size() == 0) {
            UIUtil.showError("");
            return null;
        }

        if (table.findIndex(index.getName()) == null) {
            table.getIndicies().add(index);
        }

        connection.createIndex(index);
        session.getChangeManager().fireChangeEvent(index);

        return index;
    }
}
