/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2012/01/15
 *
 * Copyright 1998-2012 by Viper Software Services
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
 * @version 1.0, 01/15/2012
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.vome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import com.viper.vome.dao.Connections;
import com.viper.vome.dao.Row;
import com.viper.vome.dao.Table;
import com.viper.vome.jfx.UIUtil;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DatabaseViewer extends Application {

    private final int DEFAULT_WIDTH = 750;
    private final int DEFAULT_HEIGHT = 650;

    private static DatabaseViewer instance = null;
    private static final Actions actions = new Actions();

    public DatabaseViewer() {
        super();

        instance = this;
    }

    public static DatabaseViewer getInstance() {
        return instance;
    }

    // --------------------------------------------------------------------------

    @Override
    public void start(Stage stage) {

        System.setProperty("glass.accessible.force", "false");

        Session session = Session.getInstance();
        setDatabaseFilename();

        session.setMetaData(loadMetaData());
        session.setStage(stage);

        Scene scene = createScene();
        scene.getStylesheets().add(getClass().getResource("DatabaseViewer.css").toExternalForm());

        stage.setTitle("Viper Software Services | Database Viewer");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/favicon.gif")));
        stage.setScene(scene);
        stage.show();
    }

    protected ResourceBundle loadMetaData() {
        return ResourceBundle.getBundle("dbmetadata");
    }

    protected Scene createScene() {

        Session session = Session.getInstance();

        // Create the browser tree - left side
        ScrollPane navigationScrollPane = UIUtil.newScrollPane(new NavigationTree(session));
        navigationScrollPane.setFitToHeight(true);
        navigationScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);

        ProgressBar progressBar = new ProgressBar(0.0);

        TitledPane navigationPane = new TitledPane();
        navigationPane.setCollapsible(false);
        navigationPane.setText("Connections");
        navigationPane.setContent(navigationScrollPane);

        // Create the Split Pane
        TableTabbedPane tabbedPane = new TableTabbedPane(session);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.3);
        splitPane.getItems().addAll(navigationPane, tabbedPane);

        navigationPane.prefHeightProperty().bind(splitPane.heightProperty());

        VBox box = new VBox();
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        box.getChildren().addAll(createMenuBar(), createToolPane(), splitPane, session.getStatusField(), progressBar);

        actions.LookAndFeelAction(null);
        openDefaultProject(session.getDatabasePropertyFilename());

        return new Scene(box, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private final void setDatabaseFilename() {

        Session session = Session.getInstance();
        Application.Parameters parameters = getParameters();
        List<String> args = parameters.getRaw();
        
        String filename = null;
        for (int i = 0; i < args.size(); i++) {
            if ("--config".equalsIgnoreCase(args.get(i))) {
                filename = args.get(i + 1);
            }
        }
        if (filename == null) {
            filename = LocaleUtil.getProperty("databases-filename");
        }

        session.setDatabasePropertyFilename(filename);

        try {
            if (!new File(filename).exists()) {
                UIUtil.showError("Database Connections failed to find file connections: " + filename);
            }
        } catch (Exception e) {
            UIUtil.showError("Database Connections failed for filename: " + filename);
        }
    }

    private void openDefaultProject(String filename) {

        try {
            Session session = Session.getInstance();

            Connections.openDatabaseConnections(filename);

            session.setDatabaseConnections(Connections.getDatabaseConnections());

        } catch (Exception e) {
            UIUtil.showError("Database Connections failed for filename: " + filename);
        }
    }

    protected MenuBar createMenuBar() {

        Session session = Session.getInstance();

        // File menu
        Menu fileMenu = UIUtil.newMenu("Connection");
        fileMenu.getItems().add(UIUtil.newMenuItem("New...", actions::NewProjectAction));
        fileMenu.getItems().add(UIUtil.newMenuItem("Open", actions::OpenProjectAction));
        fileMenu.getItems().add(UIUtil.newMenuItem("Save", actions::SaveProjectAction));
        fileMenu.getItems().add(UIUtil.newMenuItem("Close", actions::CloseProjectAction));
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(UIUtil.newMenuItem("Hosts", actions::ImportHostsAction));
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(UIUtil.newMenuItem("Exit", actions::exitAction));

        // Edit menu
        Menu editMenu = new Menu("Edit");
        editMenu.getItems().add(UIUtil.newMenuItem("Cut", actions::CutAction));
        editMenu.getItems().add(UIUtil.newMenuItem("Copy", actions::CopyAction));
        editMenu.getItems().add(UIUtil.newMenuItem("Paste", actions::PasteAction));
        editMenu.getItems().add(UIUtil.newMenuItem("Paste Special", actions::PasteSpecialAction));
        editMenu.getItems().add(UIUtil.newMenuItem("Select All", actions::SelectAllAction));

        // Look and Feel menu
        Menu lfMenu = new Menu("Look & Feel...");
        for (String skin : session.getSkins()) {
            lfMenu.getItems().add(UIUtil.newMenuItem(skin, actions::LookAndFeelAction));
        }

        // View menu
        Menu viewMenu = new Menu("View");
        viewMenu.getItems().add(lfMenu);
        viewMenu.getItems().add(UIUtil.newCheckMenuItem("Row Numbers", actions::RowNumberAction));
        viewMenu.getItems().add(new SeparatorMenuItem());
        viewMenu.getItems().add(UIUtil.newMenuItem("Format Table", actions::FormatTableAction));
        viewMenu.getItems().add(UIUtil.newMenuItem("Format Column", actions::FormatColumnAction));

        // Tools menu
        Menu toolsMenu = new Menu("Tools");
        toolsMenu.getItems().add(UIUtil.newMenuItem("Spelling", actions::SpellCheckerAction));
        toolsMenu.getItems().add(new SeparatorMenuItem());
        toolsMenu.getItems().add(UIUtil.newMenuItem("Run Script", actions::runScriptAction));
        toolsMenu.getItems().add(UIUtil.newMenuItem("SQL Editor", actions::sqlEditorAction));
        toolsMenu.getItems().add(UIUtil.newMenuItem("SQL History", actions::ViewSQLHistoryAction));

        // Data menu
        Menu dataMenu = new Menu("Data");
        dataMenu.getItems().add(UIUtil.newMenuItem("Backup", actions::BackupDatabaseAction));
        dataMenu.getItems().add(UIUtil.newMenuItem("Recover", actions::RecoverDatabaseAction));
        dataMenu.getItems().add(new SeparatorMenuItem());
        dataMenu.getItems().add(UIUtil.newMenuItem("MetaData", actions::MetaDataAction));
        dataMenu.getItems().add(UIUtil.newMenuItem("Migrate", actions::MigrateAction));
        dataMenu.getItems().add(UIUtil.newMenuItem("Compare", actions::CompareAction));
        dataMenu.getItems().add(UIUtil.newMenuItem("Generate", actions::GenerateAction));
        dataMenu.getItems().add(new SeparatorMenuItem());
        dataMenu.getItems().add(UIUtil.newMenuItem("Import", actions::ImportAction));
        dataMenu.getItems().add(UIUtil.newMenuItem("Export", actions::ExportAction));

        // Main HelpSet & Broker

        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(UIUtil.newMenuItem("About", actions::aboutAction));
        helpMenu.getItems().add(new SeparatorMenuItem());
        helpMenu.getItems().add(UIUtil.newMenuItem("Users Guide", actions::guideAction));
        helpMenu.getItems().add(UIUtil.newMenuItem("Help OnItem", actions::onItemAction));

        MenuBar menuBar = new MenuBar();
        menuBar.setId("MenuBar");
        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, toolsMenu, dataMenu, helpMenu);

        return menuBar;
    }

    // -------------------------------------------------------------------------
    // T O O L P A N E
    // -------------------------------------------------------------------------

    public ToolBar createToolPane() {

        Session session = Session.getInstance();
        ToolBar toolbar = new ToolBar();
        toolbar.getItems().add(UIUtil.newButton("Save", actions::SaveTableAction));
        toolbar.getItems().add(UIUtil.newButton("Print", actions::PrintAction));
        toolbar.getItems().add(new Separator());
        toolbar.getItems().add(UIUtil.newButton("Cut", actions::CutAction));
        toolbar.getItems().add(UIUtil.newButton("Copy", actions::CopyAction));
        toolbar.getItems().add(UIUtil.newButton("Paste", actions::PasteAction));
        toolbar.getItems().add(new Separator());
        toolbar.getItems().add(UIUtil.newButton("Left", actions::AlignLeftAction));
        toolbar.getItems().add(UIUtil.newButton("Center", actions::AlignCenterAction));
        toolbar.getItems().add(UIUtil.newButton("Right", actions::AlignRightAction));
        return toolbar;
    }

    /**
     * This method defines the pop-up menu for the main data table.
     * 
     * @param rowno
     * @param columnno
     * @return
     */
    public static ContextMenu createTablePopupMenu(Session session) {
        ContextMenu popup = new ContextMenu();
        popup.getItems().add(UIUtil.newMenuItem("Cut", actions::CutAction));
        popup.getItems().add(UIUtil.newMenuItem("Copy", actions::CopyAction));
        popup.getItems().add(UIUtil.newMenuItem("Paste", actions::PasteAction));
        popup.getItems().add(UIUtil.newMenuItem("Paste Special", actions::PasteSpecialAction));
        popup.getItems().add(UIUtil.newMenuItem("Select Column", actions::SelectColumnAction));
        popup.getItems().add(UIUtil.newMenuItem("Select Row", actions::SelectRowAction));
        popup.getItems().add(UIUtil.newMenuItem("Select All", actions::SelectAllAction));
        popup.getItems().add(new SeparatorMenuItem());
        popup.getItems().add(UIUtil.newMenuItem("Format Table...", actions::FormatTableAction));
        popup.getItems().add(UIUtil.newMenuItem("Format Column...", actions::FormatColumnAction));
        return popup;
    }

    /**
     * This method defines the pop-up menu for the main data table.
     * 
     * @param rowno
     * @param columnno
     * @return
     */
    public static ToolBar createTopTableToolbar(Session session, TableView<Row> tableView) {

        Table table = (Table) tableView.getUserData();
        if (table == null) {
            session.getStatusField().showError("tableView does not have table in UserData, software error? " + tableView.getId());
        }

        ToolBar toolbar = new ToolBar();
        toolbar.getItems().add(UIUtil.newCheckComboBox(table.getColumns(), "name", actions::SelectColumnVisibilityListener));
        toolbar.getItems().add(UIUtil.newButton("Edit", null, null, actions::ToggleTableEditModeAction));
        toolbar.getItems().add(UIUtil.newButton("Save", null, null, actions::SaveTableAction));
        if (tableView != null) {
            HBox box = createSearchTableToolbar(session, tableView);
            if (box != null) {
                toolbar.getItems().add(box);
            }
        }
        toolbar.getItems().add(UIUtil.newButton("Explain", null, null, actions::ExplainTableAction));
        return toolbar;
    }

    /**
     * This method defines the pop-up menu for the main data table.
     * 
     * @param rowno
     * @param columnno
     * @return
     */
    public static HBox createSearchTableToolbar(Session session, TableView<Row> tableView) {
        if (tableView == null) {
            return null;
        }

        ChoiceBox<String> choiceBox = new ChoiceBox<String>();
        for (TableColumn column : tableView.getColumns()) {
            choiceBox.getItems().add(column.getText());
        }
        if (tableView.getColumns().size() > 0) {
            choiceBox.setValue(tableView.getColumns().get(0).getText());
        }

        TextField textField = new TextField();
        textField.setPromptText("Search here!");
        textField.setOnKeyReleased(keyEvent -> {
            Object name = choiceBox.getValue();
            String value = textField.getText().trim();

            List<Row> items = ((Table) tableView.getUserData()).getRows();

            // TODO ERROR we need the original data.
            FilteredList<Row> flist = new FilteredList<Row>(FXCollections.observableList(items));
            flist.setPredicate(p -> p.get(name).toString().contains(value));

            tableView.setItems(flist);
        });

        HBox hbox = new HBox();
        hbox.getChildren().add(choiceBox);
        hbox.getChildren().add(textField);

        return hbox;
    }

    public static final ToolBar createEditToolbar(Session session, String propertiesName) {

        switch (propertiesName) {
        case Session.PROPERTIES_TABLE:
            return createTableEditToolbar(session);
        case Session.PROPERTIES_COLUMN:
            return createColumnEditToolbar(session);
        case Session.PROPERTIES_INDEX:
            return createIndexEditToolbar(session);
        }

        return createTableEditToolbar(session);
    }

    /**
     * This method defines the pop-up menu for the main data table.
     * 
     * @param rowno
     * @param columnno
     * @return
     */
    public static ToolBar createTableEditToolbar(Session session) {

        Table table = session.getTable();
        String currentName = null;
        if (table != null) {
            currentName = table.getName();
        }

        String uniqueID = UUID.randomUUID().toString();

        TextField newTableNameTF = UIUtil.newTextField(uniqueID, currentName, 0, actions::RenameTableAction);
        newTableNameTF.setVisible(false);
        newTableNameTF.setManaged(false);

        ToolBar toolbar = new ToolBar();
        toolbar.getItems().add(UIUtil.newButton("Delete Table", null, null, actions::DeleteTableAction));
        toolbar.getItems().add(newTableNameTF);
        toolbar.getItems().add(UIUtil.newButton("Rename Table", null, newTableNameTF, actions::SetVisible));
        toolbar.getItems().add(UIUtil.newButton("Row+", null, null, actions::AddRowAction));
        toolbar.getItems().add(UIUtil.newButton("Row-", null, null, actions::DeleteRowsAction));
        toolbar.getItems().add(UIUtil.newButton("Col+", null, null, actions::AddColumnAction));
        toolbar.getItems().add(UIUtil.newButton("Col-", null, null, actions::DeleteColumnAction));
        return toolbar;
    }

    /**
     * This method defines the pop-up menu for the main data table.
     * 
     * @param rowno
     * @param columnno
     * @return
     */
    public static ToolBar createColumnEditToolbar(Session session) {
        ToolBar toolbar = new ToolBar();
        toolbar.getItems().add(UIUtil.newButton("Add Column", null, null, actions::AddColumnAction));
        toolbar.getItems().add(UIUtil.newButton("Delete Column", null, null, actions::DeleteColumnAction));
        toolbar.getItems().add(UIUtil.newButton("Rename Column", null, null, actions::RenameColumnAction));
        toolbar.getItems().add(UIUtil.newButton("Edit Column", null, null, actions::EditColumnAction));
        return toolbar;
    }

    /**
     * This method defines the pop-up menu for the main data table.
     * 
     * @param rowno
     * @param columnno
     * @return
     */
    public static ToolBar createIndexEditToolbar(Session session) {
        ToolBar toolbar = new ToolBar();
        toolbar.getItems().add(UIUtil.newButton("Col+", null, null, actions::AddColumnAction));
        toolbar.getItems().add(UIUtil.newButton("Col-", null, null, actions::DeleteColumnAction));
        return toolbar;
    }

    /**
     * 
     */
    public static ContextMenu createTabPopupMenu(Session session) {
        ContextMenu popup = new ContextMenu();
        popup.getItems().add(UIUtil.newMenuItem("Close", actions::CloseTabAction));
        popup.getItems().add(UIUtil.newMenuItem("Close Others", actions::CloseOtherTabsAction));
        popup.getItems().add(UIUtil.newMenuItem("Close All", actions::CloseAllTabsAction));
        return popup;
    }

    // -------------------------------------------------------------------------
    //

    public static String exec(String cmd, String inputStr) throws IOException, InterruptedException {
        String cwd = System.getProperty("user.dir");
        Process proc = Runtime.getRuntime().exec(cmd, null, new File(cwd));

        BufferedWriter stdin = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
        if (inputStr != null) {
            stdin.write(inputStr);
            stdin.newLine();
        }
        stdin.flush();
        stdin.close();
        StringBuffer stdout = new StringBuffer();
        StringBuffer stderr = new StringBuffer();

        new StreamThread(proc.getInputStream(), stdout).start();
        new StreamThread(proc.getErrorStream(), stderr).start();
        proc.waitFor();

        System.out.println("cmd: " + cmd);
        System.out.println("stdout: " + stdout);
        System.out.println("stderr: " + stderr);
        return stdout.toString();
    }

    static class StreamThread extends Thread {
        BufferedReader in;
        StringBuffer str;

        public StreamThread(InputStream in, StringBuffer str) {
            this.in = new BufferedReader(new InputStreamReader(in));
            this.str = str;
        }

        public void run() {
            try {
                while (true) {
                    String line = in.readLine();
                    if (line == null) {
                        break;
                    }
                    str.append(line);
                    str.append("\n");
                }
                in.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * This method pops up a dialog containing a text box, asking for multi-line textual input.
     * 
     * @param title
     *            the title which goes on the dialog frame
     * @param msg
     *            a label describing the purpose for the text to be entered.
     * @param text
     *            the initial text which the dialog box is to start with.
     * @return the edited text string.
     */
    public String askForField(String title, String msg, String text) {
        // if (title == null || msg == null || text == null)
        // return null;
        //
        // FieldDialog dialog = new FieldDialog(topFrame, title, msg, text);
        //
        // dialog.pack();
        // dialog.show();
        //
        // String answer = dialog.getResult();
        // dialog.dispose();
        // return answer;
        return null;
    }

    public static void main(String[] args) {
        launch(DatabaseViewer.class, args);
    }
}
