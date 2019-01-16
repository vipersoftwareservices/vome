/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java    1.00 2003/06/15
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
 * -----------------------------------------------------------------------------
 */

package com.viper.vome.model;

import java.util.ArrayList;
import java.util.List;

import com.viper.vome.LocaleUtil;
import com.viper.vome.dao.Column;
import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Row;
import com.viper.vome.dao.Table;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class DatabaseModel {

    public static Pagination createPagination(final TableView<Row> tableview, final Connection connection, final Table table)
            throws Exception {

        final int numRowsPerPage = LocaleUtil.getPropertyInt("table.number.rows.per.page", 32);

        long pageCount = (connection.size(table) / numRowsPerPage) + 1;

        tableview.getItems().setAll(FXCollections.observableList(connection.loadPage(table, 0, numRowsPerPage)));

        final Pagination pagination = new Pagination((int) pageCount, 0);
        pagination.currentPageIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int startRow = newValue.intValue() * numRowsPerPage;
                tableview.getItems().setAll(FXCollections.observableList(connection.loadPage(table, startRow, numRowsPerPage)));
            }
        });

        tableview.getSortOrder().addListener(new ListChangeListener<TableColumn<Row, ?>>() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                System.out.println("getSortOrder().addListener=" + c.getList().size());
                if (c.getList().size() == 0) {
                    table.setOrderColumn(null);
                } else {
                    for (Object o : c.getList()) {
                        TableColumn<Row, ?> tableColumn = (TableColumn<Row, ?>) o;
                        table.setOrderColumn((Column) tableColumn.getUserData());
                    }
                }
                tableview.refresh();
            }
        });

        return pagination;
    }

    public static final List<String> toColumnNames(Table table) {
        List<String> names = new ArrayList<String>();
        for (Column column : table.getColumns()) {
            names.add(column.getName());
        }
        return names;
    }

    public static final List<String> toColumnNames(Row row) {
        List<String> names = new ArrayList<String>();
        for (String name : row.keySet()) {
            names.add(name);
        }
        return names;
    }

    public static final List<TableColumn<Row, Object>> getColumns(TableView<Row> tableview,  Table table) throws Exception {

        List<TableColumn<Row, Object>> cols = new ArrayList<TableColumn<Row, Object>>();

        for (final Column column : table.getColumns()) {

            TableColumn<Row, Object> col = new TableColumn<Row, Object>(column.getName());
            col.setCellFactory(getCellMapFactory());
            col.setCellValueFactory(new MapValueFactory(column.getName())); 
            
            col.setOnEditCommit(new EventHandler<CellEditEvent<Row, Object>>() {
                @Override
                public void handle(CellEditEvent<Row, Object> t) {
                    ((Row) t.getTableView().getItems().get(t.getTablePosition().getRow())).put(column.getName(), t.getNewValue());
                }
            });
            cols.add(col);
        }
        return cols;
    }

    public static Callback<TableColumn<Row, Object>, TableCell<Row, Object>> getCellMapFactory() {
        Callback<TableColumn<Row, Object>, TableCell<Row, Object>> cellFactoryForMap = new Callback<TableColumn<Row, Object>, TableCell<Row, Object>>() {
            @Override
            public TableCell<Row, Object> call(TableColumn<Row, Object> p) {
                return new TextFieldTableCell<Row, Object>(new StringConverter<Object>() {
                    @Override
                    public String toString(Object t) {
                        return (t == null) ? null : t.toString();
                    }

                    @Override
                    public Object fromString(String string) {
                        return string;
                    }
                });
            }
        };
        return cellFactoryForMap;
    }
}
