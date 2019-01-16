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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.viper.vome.dao.Column;
import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Database;
import com.viper.vome.dao.Index;
import com.viper.vome.dao.IndexClassType;
import com.viper.vome.dao.IndexType;
import com.viper.vome.dao.Procedure;
import com.viper.vome.dao.Row;
import com.viper.vome.dao.Table;
import com.viper.vome.dao.User;
import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.DatabaseModel;
import com.viper.vome.model.TableType;
import com.viper.vome.util.FileUtil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.util.Callback;

public class Dialogs {

    private static final String HELP_PATH = "res:/help/guide/features/%1.html";

    public static final int OPEN_FILE = 1;
    public static final int SAVE_FILE = 2;

    public static ButtonType askForChoice(String msg, ButtonType... buttonTitles) {
        if (buttonTitles == null) {
            return null;
        }

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(UIUtil.newLabel(msg));

        return showAsDialog("Question", mainPane, null, buttonTitles);
    }

    // -------------------------------------------------------------------------

    public static String showTextDialog(String title, String value) {

        String uniqueID = UUID.randomUUID().toString();
        TextField textTF = UIUtil.newTextField(uniqueID, value, 0, null);
        textTF.selectAll();

        ButtonType result = showAsDialog(title, textTF, null, ButtonType.APPLY, ButtonType.CANCEL);

        return (result == ButtonType.APPLY) ? textTF.getText() : null;
    }

    // -------------------------------------------------------------------------

    public static String showRenameDialog(String title, String value) {
        String renameTitle = "Rename " + title + " " + value + " to:";
        return showTextDialog(renameTitle, value);
    }

    // -------------------------------------------------------------------------

    public static Table showNewTableDialog(Database database) {

        String title = "New Table Parameters";
        Table table = new Table();

        VBox box = new VBox();
        box.getChildren().add(UIUtil.newLabel("Database Name " + database.getName()));
        box.getChildren().add(UIUtil.newLabel("Table Name:"));
        box.getChildren().add(UIUtil.newTextField(table, "Name", 0));
        box.getChildren().add(UIUtil.newLabel("Table Type:"));
        box.getChildren().add(UIUtil.newComboBox(table, "TableType", TableType.values()));

        ButtonType result = showAsDialog(title, box, null, ButtonType.CANCEL, ButtonType.APPLY);
        if (result != ButtonType.APPLY) {
            return null;
        }

        if ("DEFAULT".equals(table.getTableType())) {
            table.setTableType(TableType.TABLE);
        }

        Column column = new Column();
        column.setName("NewColumn");
        table.getColumns().add(column);
        return table;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    public static Table showNewViewDialog(Database database) {

        String title = "New Table View Parameters";

        Table table = new Table();
        table.setTableType(TableType.VIEW);

        VBox box = new VBox();
        box.getChildren().add(UIUtil.newLabel("Database Name " + database.getName()));
        box.getChildren().add(UIUtil.newLabel("Table View Name:"));
        box.getChildren().add(UIUtil.newTextField(table, "Name", 0));

        ButtonType result = showAsDialog(title, box, null, ButtonType.CANCEL, ButtonType.APPLY);
        return (result != ButtonType.APPLY) ? null : table;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    public static Procedure showNewProcedureDialog(Database database) {

        String title = "New Procedure";
        Procedure procedure = new Procedure();

        VBox box = new VBox();
        box.getChildren().add(UIUtil.newLabel("Database Name " + database.getName()));
        box.getChildren().add(UIUtil.newLabel("Procedure Name:"));
        box.getChildren().add(UIUtil.newTextField(procedure, "Name", 0));

        ButtonType result = showAsDialog(title, box, null, ButtonType.APPLY, ButtonType.CANCEL);
        return (result != ButtonType.APPLY) ? null : procedure;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    public static User showNewUserDialog(Database database) {

        User user = new User();
        user.setName("");

        VBox box = new VBox();
        box.getChildren().add(UIUtil.newLabel("Database Name " + database.getName()));
        box.getChildren().add(UIUtil.newLabel("User Name:"));
        box.getChildren().add(UIUtil.newTextField(user, "name", 0));
        box.getChildren().add(UIUtil.newLabel("Password:"));
        box.getChildren().add(UIUtil.newPasswordField(user, "password", null));
        box.getChildren().add(UIUtil.newLabel("Host Name:"));
        box.getChildren().add(UIUtil.newTextField(user, "hostName", 0));

        ButtonType result = showAsDialog("New User", box, null, ButtonType.APPLY, ButtonType.CANCEL);
        return (result != ButtonType.APPLY) ? null : user;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    public static Index showNewIndexDialog(Table table) {
        Index index = new Index();
        index.setName("");
        if (showIndexDialog("New Index Parameters", table, index)) {
            return index;
        }
        return null;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    public static Index showEditIndexDialog(Table table, Index index) {
        if (showIndexDialog("Edit Index Parameters", table, index)) {
            return index;
        }
        return null;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    public static Column showNewIndexColumnDialog(Index index) {

        String title = "New Database Parameters";
        Column indexColumn = new Column();

        VBox box = new VBox();
        box.getChildren().add(UIUtil.newLabel("Index Column Name:"));
        box.getChildren().add(UIUtil.newTextField(indexColumn, "name", 0));

        ButtonType result = showAsDialog(title, box, null, ButtonType.APPLY, ButtonType.CANCEL);
        return (result != ButtonType.APPLY) ? null : indexColumn;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    private static boolean showIndexDialog(String title, Table table, Index index) {

        Column indexColumn = new Column();

        TextField indexNameTF = UIUtil.newTextField(index, "Name", 0);
        TextField columnNameTF = UIUtil.newTextField(indexColumn, "Name", 0);
        ComboBox<IndexClassType> indexClassBox = UIUtil.newComboBox(index, "IndexClass",
                sort(EnumSet.allOf(IndexClassType.class)));
        ComboBox<IndexType> indexTypeBox = UIUtil.newComboBox(index, "IndexType", sort(EnumSet.allOf(IndexType.class)));

        GridPane box = new GridPane();
        box.getChildren().add(UIUtil.newLabel("Table Name:"));
        box.getChildren().add(UIUtil.newLabel(table.getName()));
        box.getChildren().add(UIUtil.newLabel("Index Name:"));
        box.getChildren().add(indexNameTF);
        box.getChildren().add(UIUtil.newLabel("Index Class:"));
        box.getChildren().add(indexClassBox);
        box.getChildren().add(UIUtil.newLabel("Index Type:"));
        box.getChildren().add(indexTypeBox);
        box.getChildren().add(UIUtil.newLabel("Column Name:"));
        box.getChildren().add(columnNameTF);

        ButtonType result = showAsDialog(title, box, null, ButtonType.APPLY, ButtonType.CANCEL);
        if (result != ButtonType.APPLY) {
            return false;
        }

        // TODO ste column in table for indexing.
        // table.getColumn().index = indexName;
        return true;
    }

    // -------------------------------------------------------------------------

    public static ButtonType showHelpDialog(String resource, Bounds bounds) {
        String html = "";
        try {
            html = FileUtil.readFile(HELP_PATH.replace("%1", resource));
        } catch (Exception e) {
            html = new String("No Help Available");
        }
        return showHTMLDialog("Help", html, bounds);
    }

    public static ButtonType showHTMLDialog(String title, String html, Bounds bounds) {

        WebView pane = UIUtil.newWebView(html);

        return showAsDialog(title, pane, bounds, ButtonType.CANCEL);
    }

    public static void showHTMLEditorDialog(String title, String html) {

        HTMLEditor pane = new HTMLEditor();
        pane.setHtmlText(html);

        showAsDialog(title, pane, null, ButtonType.CANCEL);
    }

    public static boolean showStatusDialog(String title, Node content) {

        ButtonType result = showAsDialog(title, content, null, new ButtonType("Send Report"), ButtonType.CANCEL);
        return (result == ButtonType.CANCEL) ? false : true;
    }

    public static void showComponentDialog(String title, Region component) {
        showAsDialog(title, component, null, ButtonType.CANCEL);
    }

    public static File showDirectoryDialog(Session session, String title, String filename) {
        DirectoryChooser fc = new DirectoryChooser();
        fc.setTitle(title);
        if (filename != null) {
            fc.setInitialDirectory(new File(filename));
        }
        return fc.showDialog(session.getStage());
    }

    public static File showOpenDialog(Session session, String title, String filename, String... extensions) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new ExtensionFilter("", extensions));
        fc.setTitle(title);
        if (filename != null) {
            fc.setInitialDirectory(new File(filename));
        }
        return fc.showOpenDialog(session.getStage());
    }

    public static File showSaveDialog(Session session, String title, String filename, String... extensions) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new ExtensionFilter("", extensions));
        fc.setTitle(title);
        if (filename != null) {
            fc.setInitialDirectory(new File(filename));
        }
        return fc.showSaveDialog(session.getStage());
    }

    public static Object showListDialog(String title, Collection<?> items) {

        Bean selectedValue = new Bean();

        ScrollPane scrollPane = UIUtil.newScrollListView(selectedValue, "value", items);

        ButtonType result = showAsDialog(title, scrollPane, null, ButtonType.APPLY, ButtonType.CANCEL);
        return (result == ButtonType.CANCEL) ? null : selectedValue.getValue();
    }

    public static Object showConfirmListDialog(String title, Collection<?> items) {

        Bean selectedValue = new Bean();
        ScrollPane scrollPane = UIUtil.newScrollListView(selectedValue, "value", items);

        ButtonType result = showAsDialog(title, scrollPane, null, new ButtonType("Try Again!"), ButtonType.CANCEL);
        return (result == ButtonType.CANCEL) ? null : selectedValue.getValue();
    }

    public static boolean showTableDialog(String title, Collection<Row> rows) throws Exception {

        Table table = new Table();
        table.getRows().addAll(rows);

        Session session = Session.getInstance();
        Connection connection = session.openConnection();

        TableView uiTable = new TableView();
        uiTable.getColumns().setAll(DatabaseModel.getColumns(uiTable, table));
        uiTable.getItems().setAll(getData(table));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(uiTable);

        ButtonType result = showAsDialog(title, scrollPane, null, ButtonType.OK, ButtonType.CANCEL);
        return (result != ButtonType.CANCEL);
    }

    public static ButtonType showTableDialog(String title, Table table) throws Exception {

        Session session = Session.getInstance();
        Connection connection = session.openConnection();

        TableView uiTable = new TableView();
        uiTable.getColumns().setAll(DatabaseModel.getColumns(uiTable, table));
        uiTable.getItems().setAll(getData(table));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(uiTable);

        return showAsDialog(title, scrollPane, null, ButtonType.OK, ButtonType.CANCEL);
    }

    private static <T> T[] sort(Collection<T> list) {
        T sortedItems[] = (T[]) list.toArray();
        Arrays.sort(sortedItems);
        return sortedItems;
    }

    public static ButtonType showAsDialog(String title, Node content, Bounds bounds, ButtonType... buttonTypes) {

        final Dialog<ButtonType> dialog = new Dialog<ButtonType>();

        for (int i = buttonTypes.length - 1; i >= 0; i--) {
            ButtonType buttonType = buttonTypes[i];
            dialog.getDialogPane().getButtonTypes().add(buttonType);
        }

        dialog.getDialogPane().setContent(content);
        // dialog.setGraphic(imageView);
        dialog.setTitle(title);
        dialog.setResizable(true);
        dialog.initModality(Modality.APPLICATION_MODAL);
        if (bounds != null) {
            dialog.setX(bounds.getMinX());
            dialog.setY(bounds.getMinY());
            dialog.setWidth(bounds.getWidth());
            dialog.setHeight(bounds.getHeight());
        }

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return ButtonType.CANCEL;
    }

    public static void showAsDialog(String title, Node content, EventHandler handler) {

        final Dialog<ButtonType> dialog = new Dialog<ButtonType>();

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        final Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, handler);

        dialog.getDialogPane().setContent(content);
        // dialog.setGraphic(imageView);
        dialog.setTitle(title);
        dialog.setResizable(true);
        dialog.initModality(Modality.APPLICATION_MODAL);

        dialog.showAndWait();
    }

    public static <T> T showAsDialog(String title, Node content, Callback<ButtonType, T> converter) {

        final Dialog<T> dialog = new Dialog<T>();

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.getDialogPane().setContent(content);
        // dialog.setGraphic(imageView);
        dialog.setTitle(title);
        dialog.setResizable(true);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResultConverter(converter);

        Optional<T> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public static ImageView getSideImage() {
        ImageView imageView = new ImageView("/images/wizardside.gif");
        imageView.getStyleClass().add("sidebanner");
        imageView.setSmooth(true);
        imageView.setCache(true);
        return imageView;
    }

    private static final List<Row> getData(Table table) throws Exception {

        Session session = Session.getInstance();
        Connection conn = session.openConnection();

        return conn.queryAll(table);
    }
}

class Bean {
    String value = null;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
