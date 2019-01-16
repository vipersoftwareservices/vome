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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.viper.vome.dao.Column;
import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Database;
import com.viper.vome.dao.ForeignKey;
import com.viper.vome.dao.Index;
import com.viper.vome.dao.IndexColumn;
import com.viper.vome.dao.Procedure;
import com.viper.vome.dao.Table;
import com.viper.vome.dao.User;
import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.model.DatabaseConnections;
import com.viper.vome.model.TableType;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.web.WebView;

public class NavigationTree extends TreeView implements PropertyChangeListener, EventHandler<ContextMenuEvent> {

    private static final Actions actions = new Actions();

    protected final static String connectionsImage = "/images/connect_server.gif";
    protected final static String connectionImage = "/images/connect_server.gif";
    protected final static String databaseImage = "/images/database.gif";
    protected final static String columnsImage = "/images/columns.gif";
    protected final static String columnImage = "/images/column.gif";
    protected final static String procedureImage = "/images/procedure.gif";
    protected final static String triggerImage = "/images/trigger.gif";
    protected final static String userImage = "/images/user.gif";
    protected final static String usersImage = "/images/users.gif";
    protected final static String foreignKeyImage = "/images/index.gif";
    protected final static String indexImage = "/images/index.gif";

    protected final static String DatabaseConnectionsClosed = "/images/cprj_obj.gif";
    protected final static String DatabaseConnectionsOpened = "/images/prj_obj.gif";
    protected final static String DatabaseConnectionClosed = "/images/crepo_rep.gif";
    protected final static String DatabaseConnectionBroken = "/images/crepo_broken_rep.gif";
    protected final static String DatabaseConnectionOpened = "/images/repo_opened_rep.gif";
    protected final static String DatabaseClosed = "/images/crepository_rep.gif";
    protected final static String DatabaseOpened = "/images/repository_rep.gif";

    protected final static String ColumnSelectionClosed = "/images/ccolumn.gif";
    protected final static String ColumnSelectionOpened = "/images/column.gif";
    protected final static String ProcedureSelectionClosed = "/images/cprocedure.gif";
    protected final static String ProcedureSelectionOpened = "/images/procedure.gif";
    protected final static String PROCEDURE_LISTClosed = "/images/cprocedure.gif";
    protected final static String PROCEDURE_LISTOpened = "/images/procedure.gif";
    protected final static String UsersSelectionClosed = "/images/users.gif";
    protected final static String UsersSelectionOpened = "/images/users.gif";
    protected final static String UserSelectionClosed = "/images/cuser.gif";
    protected final static String UserSelectionOpened = "/images/user.gif";
    protected final static String TriggerSelectionClosed = "/images/ctrigger.gif";
    protected final static String TriggerSelectionOpened = "/images/trigger.gif";

    private final static Map<String, String> tableIcons = new HashMap<String, String>();

    static {
        tableIcons.put(TableType.TABLE + ".opened", "/images/table.gif");
        tableIcons.put(TableType.TABLE + ".closed", "/images/ctable.gif");
        tableIcons.put(TableType.LOCAL_TEMPORARY + ".opened", "/images/table.gif");
        tableIcons.put(TableType.LOCAL_TEMPORARY + ".closed", "/images/ctable.gif");
        tableIcons.put(TableType.BEAN + ".opened", "/images/table.gif");
        tableIcons.put(TableType.BEAN + ".closed", "/images/ctable.gif");
        tableIcons.put(TableType.VIEW + ".opened", "/images/view_table.gif");
        tableIcons.put(TableType.VIEW + ".closed", "/images/cview_table.gif");
        tableIcons.put(TableType.SYSTEM_TABLE + ".opened", "/images/super_table.gif");
        tableIcons.put(TableType.SYSTEM_TABLE + ".closed", "/images/csuper_table.gif");
        tableIcons.put(TableType.SYSTEM_VIEW + ".opened", "/images/super_table.gif");
        tableIcons.put(TableType.SYSTEM_VIEW + ".closed", "/images/csuper_table.gif");
        tableIcons.put(TableType.GLOBAL_TEMPORARY + ".opened", "/images/table.gif");
        tableIcons.put(TableType.GLOBAL_TEMPORARY + ".closed", "/images/ctable.gif");
        tableIcons.put(TableType.ALIAS + ".opened", "/images/table.gif");
        tableIcons.put(TableType.ALIAS + ".closed", "/images/ctable.gif");
        tableIcons.put(TableType.SYNONYM + ".opened", "/images/table.gif");
        tableIcons.put(TableType.SYNONYM + ".closed", "/images/ctable.gif");
        tableIcons.put(TableType.DATA + ".opened", "/images/table.gif");
        tableIcons.put(TableType.DATA + ".closed", "/images/ctable.gif");
        tableIcons.put(TableType.BASE_TABLE + ".opened", "/images/table.gif");
        tableIcons.put(TableType.BASE_TABLE + ".closed", "/images/ctable.gif");
        tableIcons.put(TableType.CRUD_BEAN + ".opened", "/images/table.gif");
        tableIcons.put(TableType.CRUD_BEAN + ".closed", "/images/ctable.gif");
    }

    private Session session = null;

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    public NavigationTree(Session session) {
        super();

        this.session = session;

        setId("navigationtree");

        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> changed(observable, oldValue, newValue));
        setOnContextMenuRequested(this);

        session.getChangeManager().add(this);

        // ToolTipManager.sharedInstance().registerComponent(this);
        // putClientProperty("Tree.lineStyle", "Angled");
        // setCellRenderer(AbstractSelectionNode.getTreeCellRenderer());
        // setToggleClickCount(2);

    }

    public void changed(ObservableValue observable, Object oldValue, Object newValue) {

        System.err.println(
                "Changed: #1," + observable + ", value=" + observable.getValue() + ", old=" + oldValue + ", new=" + newValue);

        try {

            TreeItem selectedItem = (TreeItem) newValue;
            if (selectedItem == null || selectedItem.getValue() == null) {
                return;
            }

            System.err.println("Changed: #2, value=" + selectedItem.getValue());

            session.setUser(findAncestor(selectedItem, User.class));
            session.setForeignKey(findAncestor(selectedItem, ForeignKey.class));
            session.setIndexColumn(findAncestor(selectedItem, IndexColumn.class));
            session.setIndex(findAncestor(selectedItem, Index.class));
            session.setColumn(findAncestor(selectedItem, Column.class));
            session.setProcedure(findAncestor(selectedItem, Procedure.class));
            session.setTable(findAncestor(selectedItem, Table.class));
            session.setDatabase(findAncestor(selectedItem, Database.class));
            session.setDatabaseConnection(findAncestor(selectedItem, DatabaseConnection.class));
            // session.setConnections(findAncestor(selectedItem,
            // DatabaseConnections.class));

            if (selectedItem.getValue() instanceof DatabaseConnections) {
                DatabaseConnectionsHandler(selectedItem);

            } else if (selectedItem.getValue() instanceof DatabaseConnection) {
                DatabaseConnectionHandler(selectedItem);

            } else if (selectedItem.getValue() instanceof Database) {
                DatabaseHandler(selectedItem);

            } else if (selectedItem.getValue() instanceof Table) {
                TableHandler(selectedItem);

            } else if (selectedItem.getValue() instanceof Procedure) {
                ProcedureHandler(selectedItem);

            } else if (selectedItem.getValue() instanceof User) {
                UserHandler(selectedItem);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------

    @Override
    public void handle(ContextMenuEvent event) {
        TreeItem selectedItem = (TreeItem) getSelectionModel().getSelectedItem();

        System.out.println("Handle ContextMenu: " + selectedItem + ":" + selectedItem.getValue());

        NavigationTree tree = (NavigationTree) event.getSource();
        double X = event.getScreenX() + 1;
        double Y = event.getScreenY();

        if (selectedItem == null || selectedItem.getValue() == null) {
            return;
        }

        session.setUser(findAncestor(selectedItem, User.class));
        session.setColumn(findAncestor(selectedItem, Column.class));
        session.setProcedure(findAncestor(selectedItem, Procedure.class));
        session.setTable(findAncestor(selectedItem, Table.class));
        session.setDatabase(findAncestor(selectedItem, Database.class));
        session.setDatabaseConnection(findAncestor(selectedItem, DatabaseConnection.class));
        // session.setConnections(findAncestor(selectedItem,
        // DatabaseConnections.class));

        if (selectedItem.getValue() instanceof DatabaseConnections) {
            getDatabaseConnectionsMenu().show(tree, X, Y);

        } else if (selectedItem.getValue() instanceof DatabaseConnection) {
            getDatabaseConnectionMenu().show(tree, X, Y);

        } else if (selectedItem.getValue() instanceof Database) {
            getDatabaseMenu().show(tree, X, Y);

        } else if (selectedItem.getValue() instanceof Table) {
            getTableMenu(session.getTable()).show(tree, X, Y);

        } else if (selectedItem.getValue() instanceof Procedure) {
            getProcedureMenu().show(tree, X, Y);

        } else if (selectedItem.getValue() instanceof User) {
            getUserMenu().show(tree, X, Y);

        }
    }

    // -------------------------------------------------------------------------
    // Starting point for creating the tree, or updating the tree
    // -------------------------------------------------------------------------

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        try {

            String propertyName = evt.getPropertyName();
            Object newValue = evt.getNewValue();

            if (Session.DATABASE_CONNECTIONS_PROPERTY.equals(propertyName)) {

                DatabaseConnections connections = session.getDatabaseConnections();
                if (connections.getFilename() == null) {
                    connections.setFilename(Session.NEW_DATA_SOURCE_FILENAME_LABEL);
                }

                setRoot(UIUtil.newTreeItem(connections, connectionsImage, 16, 16));
                getRoot().setExpanded(true);

            } else {
                // TreeItem item = find(getRoot(), newValue);
                //
                // if (item == null) {
                //
                // } else if (Session.PROPERTIES_DATABASE.equals(propertyName)) {
                // DatabaseHandler(item);
                //
                // } else if (Session.PROPERTIES_TABLE.equals(propertyName)) {
                // TableHandler(item);
                //
                // } else if (Session.PROPERTIES_PROCEDURE.equals(propertyName)) {
                // ProcedureHandler(item);
                //
                // } else if (Session.PROPERTIES_USER.equals(propertyName)) {
                // UserHandler(item);
                // }

                this.refresh();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    private static <T> T findAncestor(TreeItem item, Class<T> clazz) {
        while (item != null) {
            if (clazz.isInstance(item.getValue())) {
                return clazz.cast(item.getValue());
            }
            item = item.getParent();
        }
        return null;
    }

    private static TreeItem find(TreeItem item, Object value) {
        if (item.getValue() == value) {
            return item;
        }
        for (Object o : item.getChildren()) {
            TreeItem child = (TreeItem) o;
            TreeItem result = find(child, value);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Selection Nodes
    // -------------------------------------------------------------------------

    public <T> void DatabaseConnectionsHandler(TreeItem item) {
        DatabaseConnections connections = (DatabaseConnections) item.getValue();
        try {
            item.getChildren().clear();
            for (DatabaseConnection connection : connections.getConnections()) {
                if (!contains(item.getChildren(), connection)) {
                    item.getChildren().add(UIUtil.newTreeItem(connection, connectionImage, 16, 16));
                }
            }
            // FXCollections.sort(item.getChildren());
        } catch (Exception e) {
            showError("Unable display connections in file: " + connections.getFilename(), e);
        }
    }

    public ContextMenu getDatabaseConnectionsMenu() {
        ContextMenu popup = new ContextMenu();
        popup.getItems().add(UIUtil.newMenuItem("Add Connection", actions::NewConnectionAction));
        popup.getItems().add(UIUtil.newMenuItem("Describe", actions::DatabaseConnectionsPropertiesAction));
        return popup;
    }

    // -------------------------------------------------------------------------

    public <T> void DatabaseConnectionHandler(TreeItem item) {
        DatabaseConnection dbc = (DatabaseConnection) item.getValue();
        if (dbc == null) {
            showError("DatabaseConnection is null " + dbc.getName());
            return;
        }

        try {

            actions.OpenConnectionAction(null);

            item.getChildren().clear();

            Connection connection = session.getConnection();
            if (connection == null) {
                showError("connection is null " + dbc.getName());
                return;
            }
            connection.loadDatabases();
            connection.loadUsers();

            for (Database database : connection.getDatabases()) {
                if (!contains(item.getChildren(), database)) {
                    item.getChildren().add(UIUtil.newTreeItem(database, databaseImage, 16, 16));
                }
            }

            TreeItem users = UIUtil.newTreeItem("Users", usersImage, 16, 16);
            item.getChildren().add(users);

            for (User user : connection.getUsers()) {
                users.getChildren().add(UIUtil.newTreeItem(user, userImage, 16, 16));
            }

            item.setExpanded(true);
        } catch (Exception e) {
            showError("Unable to make database connection " + dbc.getName(), e);
        }
    }

    public ContextMenu getDatabaseConnectionMenu() {
        ContextMenu popup = new ContextMenu();
        popup.getItems().add(UIUtil.newMenuItem("Connect", actions::OpenConnectionAction));
        popup.getItems().add(UIUtil.newMenuItem("Delete Connection", actions::DeleteConnectionAction));
        popup.getItems().add(UIUtil.newMenuItem("Rename Connection", actions::RenameDatabaseConnectionAction));
        popup.getItems().add(UIUtil.newMenuItem("Add Database", actions::AddDatabaseAction));
        popup.getItems().add(new SeparatorMenuItem());
        popup.getItems().add(UIUtil.newMenuItem("Privileges...", actions::NoopAction));
        popup.getItems().add(UIUtil.newMenuItem("Properties...", actions::EditConnectionAction));
        return popup;
    }

    // -------------------------------------------------------------------------

    public <T> void DatabaseHandler(TreeItem item) {

        try {
            Database database = (Database) item.getValue();

            Connection connection = session.getConnection();
            connection.loadTables(session.getDatabase());
            connection.loadProcedures(session.getDatabase());

            item.getChildren().clear();
            for (Table table : database.getTables()) {
                item.getChildren().add(UIUtil.newTreeItem(table, tableIcons.get(table.getTableType() + ".opened"), 16, 16));
            }

            List<Procedure> procedures = database.getProcedures();
            for (Procedure procedure : procedures) {
                item.getChildren().add(UIUtil.newTreeItem(procedure, procedureImage, 16, 16));
            }

            if (item.getChildren().size() == 0) {
                String msg = "Database " + database.getName() + " is empty";
                boolean allTablesAreSelectable = false; // database.
                // getAttributeBoolean(
                // "allTablesAreSelectable"
                // );
                if (!allTablesAreSelectable) {
                    msg = msg + " or you do not have access to the tables in the database.";
                }
                UIUtil.showInformationDialog(msg);
            }

            item.setExpanded(true);

        } catch (Exception e) {
            showError("Error loading tables for database " + session.getDatabase().getName(), e);
        }
    }

    public ContextMenu getDatabaseMenu() {
        Menu addMenu = new Menu("Add");
        addMenu.getItems().add(UIUtil.newMenuItem("Table", actions::AddTableAction));
        addMenu.getItems().add(UIUtil.newMenuItem("View", actions::AddViewAction));
        addMenu.getItems().add(UIUtil.newMenuItem("Procedure", actions::AddProcedureAction));
        addMenu.getItems().add(UIUtil.newMenuItem("User", actions::AddUserAction));

        ContextMenu popup = new ContextMenu();
        popup.getItems().add(addMenu);
        popup.getItems().add(UIUtil.newMenuItem("Delete Database", actions::DeleteDatabaseAction));
        popup.getItems().add(UIUtil.newMenuItem("Rename Database", actions::RenameDatabaseAction));
        popup.getItems().add(new SeparatorMenuItem());
        popup.getItems().add(UIUtil.newMenuItem("Parameters", actions::DatabaseParametersAction));
        popup.getItems().add(UIUtil.newMenuItem("Describe", actions::DatabasePropertiesAction));
        return popup;
    }

    // -------------------------------------------------------------------------

    public <T> void TableHandler(TreeItem item) {
        Table table = (Table) item.getValue();

        try {

            Connection connection = session.getConnection();
            connection.loadTable(table);

            session.getTableTabbedPane().createTableItem(table);

        } catch (Exception ex) {
            showError("Unable to select table: " + session.getDatabase().getName() + "." + table.getName(), ex);
        }
        item.setExpanded(true);
    }

    public ContextMenu getTableMenu(Table table) {
        ContextMenu popup = new ContextMenu();
        if (table.getTableType() == TableType.VIEW) {
            popup.getItems().add(UIUtil.newMenuItem("View Source", actions::ViewSourceAction));
        }
        popup.getItems().add(UIUtil.newMenuItem("TablePrivileges", actions::TablePrivilegesAction));
        popup.getItems().add(new SeparatorMenuItem());
        popup.getItems().add(UIUtil.newMenuItem("Primary Key...", actions::PropertiesPrimaryKeyAction));
        popup.getItems().add(UIUtil.newMenuItem("Foreign Key...", actions::PropertiesForeignKeyAction));
        popup.getItems().add(UIUtil.newMenuItem("Indices...", actions::PropertiesIndiciesAction));
        popup.getItems().add(UIUtil.newMenuItem("Columns...", actions::PropertiesColumnsAction));
        return popup;
    }

    // -------------------------------------------------------------------------

    public <T> void ProcedureHandler(TreeItem item) throws Exception {

        Procedure procedure = (Procedure) item.getValue();

        Session session = Session.getInstance();

        Connection connection = session.getConnection();
        connection.loadProcedure(procedure);

        String html = procedure.getSource();
        // format source.
        if (html != null) {
            html = html.replaceAll(";", ";<br>");
        }

        WebView pane = UIUtil.newWebView(html);

        TableTabbedPane tabpane = session.getTableTabbedPane();

        Tab tab = UIUtil.newTab("Procedure-" + procedure.getName(), pane);
        tab.setUserData(procedure);

        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
    }

    public ContextMenu getProcedureMenu() {
        ContextMenu popup = new ContextMenu();
        popup.getItems().add(UIUtil.newMenuItem("New", actions::NoopAction));
        popup.getItems().add(UIUtil.newMenuItem("Delete Procedure", actions::DeleteProcedureAction));
        popup.getItems().add(new SeparatorMenuItem());
        popup.getItems().add(UIUtil.newMenuItem("Privileges...", actions::NoopAction));
        popup.getItems().add(UIUtil.newMenuItem("Describe", actions::PropertiesProcedureAction));
        return popup;
    }

    // -------------------------------------------------------------------------

    public <T> void UserHandler(TreeItem item) {
        try {
            Connection connection = session.getConnection();
            User user = connection.findUser(session.getUser().getName());
        } catch (Exception ex) {
            showError("Unable to select user: " + session.getUser().getName(), ex);
        }
    }

    public ContextMenu getUserMenu() {
        ContextMenu popup = new ContextMenu();
        popup.getItems().add(UIUtil.newMenuItem("New", actions::NoopAction));
        popup.getItems().add(UIUtil.newMenuItem("Delete User", actions::DeleteUserAction));
        popup.getItems().add(UIUtil.newMenuItem("Rename User", actions::RenameUserAction));
        popup.getItems().add(new SeparatorMenuItem());
        popup.getItems().add(UIUtil.newMenuItem("Privileges...", actions::NoopAction));
        popup.getItems().add(UIUtil.newMenuItem("Describe", actions::PropertiesUserAction));
        return popup;
    }

    private <T> boolean contains(List<TreeItem> children, T child) {
        for (TreeItem node : children) {
            if (child.equals(node.getValue())) {
                return true;
            }
        }
        return false;
    }

    private void showError(String msg) {
        Session.getInstance().getStatusField().showError(msg);
    }

    private void showError(String msg, Throwable t) {
        Session.getInstance().getStatusField().showError(msg, t);
    }
}
