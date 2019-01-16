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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.viper.vome.dao.Column;
import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Connections;
import com.viper.vome.dao.Database;
import com.viper.vome.dao.ForeignKey;
import com.viper.vome.dao.Index;
import com.viper.vome.dao.IndexColumn;
import com.viper.vome.dao.Procedure;
import com.viper.vome.dao.Table;
import com.viper.vome.dao.User;
import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.model.DatabaseConnections;
import com.viper.vome.util.PropertyChangeManager;

import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Session {

    public static final String PROPERTIES_DATABASE = "DATABASE";
    public static final String PROPERTIES_TABLE = "TABLE";
    public static final String PROPERTIES_COLUMN = "COLUMN";
    public static final String PROPERTIES_FOREIGN_KEY = "FOREIGN_KEY";
    public static final String PROPERTIES_PRIMARY_KEY = "PRIMARY_KEY";
    public static final String PROPERTIES_INDEX = "INDEX";
    public static final String PROPERTIES_PROCEDURE = "PROCEDURE";
    public static final String PROPERTIES_USER = "USER";

    public final static String NEW_DATA_SOURCE_FILENAME_LABEL = "<< New Project >>";

    public final static String NEW_DATABASE_CONNECTION_PROPERTY = "NewDatabaseConnection";
    public final static String DATABASE_CONNECTION_PROPERTY = "DatabaseConnection";
    public final static String DATABASE_CONNECTIONS_PROPERTY = "DatabaseConnections";

    public final static List<String> RESIZE_MODES = new ArrayList<String>();

    static {
        RESIZE_MODES.add("OFF");
        RESIZE_MODES.add("NEXT_COLUMN");
        RESIZE_MODES.add("SUBSEQUENT_COLUMNS");
        RESIZE_MODES.add("LAST_COLUMN");
        RESIZE_MODES.add("ALL_COLUMNS");
    }

    private static final Session instance = new Session();

    private final PropertyChangeManager changeManager = new PropertyChangeManager();
    private final String[] skins = new String[] { "DatabaseViewer.css" };

    private MessageField statusField = null;
    private TableTabbedPane tableTabbedPane = null;
    private ResourceBundle metadata = null;
    private Stage stage = null;
    private boolean showHorizontalLines = false;
    private boolean showVerticalLines = false;
    private boolean showLineNumbers = false;
    private int interCellSpacing = 0;
    private int rowHeight = 0;
    private int bandingInterval = 0;
    private String autoResizeMode = "OFF";
    private Color nonBandingColor = Color.GREY;
    private Color bandingColor = Color.WHITE;
    private Color fontColor = Color.BLACK;
    private Font tableFont = Font.getDefault();
    private Dialog dialog = null;
    private String databasePropertyFilename = "etc/databases.xml";

    private Session() {

    }

    public static Session getInstance() {
        return instance;
    }

    public Color getNonBandingColor() {
        return nonBandingColor;
    }

    public void setNonBandingColor(Color nonBandingColor) {
        this.nonBandingColor = nonBandingColor;
    }

    public Color getBandingColor() {
        return bandingColor;
    }

    public void setBandingColor(Color bandingColor) {
        this.bandingColor = bandingColor;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public boolean isShowHorizontalLines() {
        return showHorizontalLines;
    }

    public void setShowHorizontalLines(boolean showHorizontalLines) {
        this.showHorizontalLines = showHorizontalLines;
    }

    public boolean isShowVerticalLines() {
        return showVerticalLines;
    }

    public void setShowVerticalLines(boolean showVerticalLines) {
        this.showVerticalLines = showVerticalLines;
    }

    public int getInterCellSpacing() {
        return interCellSpacing;
    }

    public void setInterCellSpacing(int interCellSpacing) {
        this.interCellSpacing = interCellSpacing;
    }

    public void setInterCellSpacing(Integer interCellSpacing) {
        this.interCellSpacing = (interCellSpacing == null) ? 0 : interCellSpacing;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    public void setRowHeight(Integer rowHeight) {
        this.rowHeight = (rowHeight == null) ? 0 : rowHeight;
    }

    public int getBandingInterval() {
        return bandingInterval;
    }

    public void setBandingInterval(int bandingInterval) {
        this.bandingInterval = bandingInterval;
    }

    public void setBandingInterval(Integer bandingInterval) {
        this.bandingInterval = (bandingInterval == null) ? 0: bandingInterval;
    }
    
    public String getAutoResizeMode() {
        return autoResizeMode;
    }

    public void setAutoResizeMode(String autoResizeMode) {
        this.autoResizeMode = autoResizeMode;
    }

    public Font getTableFont() {
        return tableFont;
    }

    public void setTableFont(Font tableFont) {
        this.tableFont = tableFont;
    }

    public String getDatabasePropertyFilename() {
        return databasePropertyFilename;
    }

    public void setDatabasePropertyFilename(String databasePropertyFilename) {
        this.databasePropertyFilename = databasePropertyFilename;
    }

    public boolean isShowLineNumbers() {
        return showLineNumbers;
    }

    public void setShowLineNumbers(boolean showLineNumbers) {
        this.showLineNumbers = showLineNumbers;
    }

    // ----------------------------------------------------------------------------

    public Connection openConnection() throws Exception {
        if (getConnection() == null) {
            DatabaseConnection dbc = getDatabaseConnection();
            if (dbc == null) {
                return null;
            }
            Connection connection = Connections.openConnection(dbc);
            setConnection(connection);
        }
        setConnection(getConnection());
        return getConnection();
    }

    public Connection openConnection(DatabaseConnection dbc) throws Exception {

        setConnection(Connections.openConnection(dbc));
        return getConnection();
    }

    public Connection getConnection() {
        return (Connection) changeManager.getValue("CONNECTION");
    }

    public void setConnection(Connection connection) {
        changeManager.fireChangeEvent(this, "CONNECTION", connection);
    }

    public Connections getConnections() {
        return new Connections();
    }

    public DatabaseConnections getDatabaseConnections() {
        return (DatabaseConnections) changeManager.getValue(DATABASE_CONNECTIONS_PROPERTY);
    }

    public void setDatabaseConnections(DatabaseConnections connections) {
        changeManager.fireChangeEvent(this, DATABASE_CONNECTIONS_PROPERTY, connections);
    }

    public DatabaseConnection getDatabaseConnection() {
        return (DatabaseConnection) changeManager.getValue(DATABASE_CONNECTION_PROPERTY);
    }

    public void setDatabaseConnection(DatabaseConnection connection) {
        changeManager.fireChangeEvent(this, DATABASE_CONNECTION_PROPERTY, connection);
    }

    public Database getDatabase() {
        return (Database) changeManager.getValue(PROPERTIES_DATABASE);
    }

    public void setDatabase(Database database) {
        changeManager.fireChangeEvent(this, PROPERTIES_DATABASE, database);
    }

    public Procedure getProcedure() {
        return (Procedure) changeManager.getValue("PROCEDURE");
    }

    public void setProcedure(Procedure procedure) {
        changeManager.fireChangeEvent(this, "PROCEDURE", procedure);
    }

    public User getUser() {
        return (User) changeManager.getValue("USER");
    }

    public void setUser(User user) {
        changeManager.fireChangeEvent(this, "USER", user);
    }

    public Table getTable() {
        return (Table) changeManager.getValue(PROPERTIES_TABLE);
    }

    public void setTable(Table table) {
        changeManager.fireChangeEvent(this, PROPERTIES_TABLE, table);
    }

    public Column getColumn() {
        return (Column) changeManager.getValue(PROPERTIES_COLUMN);
    }

    public void setColumn(Column column) {
        changeManager.fireChangeEvent(this, PROPERTIES_COLUMN, column);
    }

    public ForeignKey getForeignKey() {
        return (ForeignKey) changeManager.getValue("FOREIGNKEY");
    }

    public void setForeignKey(ForeignKey item) {
        changeManager.fireChangeEvent(this, "FOREIGNKEY", item);
    }

    public Index getIndex() {
        return (Index) changeManager.getValue("INDEX");
    }

    public void setIndex(Index index) {
        changeManager.fireChangeEvent(this, "INDEX", index);
    }

    public IndexColumn getIndexColumn() {
        return (IndexColumn) changeManager.getValue("INDEXCOLUMN");
    }

    public void setIndexColumn(IndexColumn indexColumn) {
        changeManager.fireChangeEvent(this, "INDEXCOLUMN", indexColumn);
    }

    // -----------------------------------------------------------------------------

    public TableTabbedPane getTableTabbedPane() {
        return tableTabbedPane;
    }

    public void setTableTabbedPane(TableTabbedPane tableTabbedPane) {
        this.tableTabbedPane = tableTabbedPane;
    }

    public TableView getSelectedTableView() {
        return getTableTabbedPane().getSelectedTable();
    }

    public PropertyChangeManager getChangeManager() {
        return changeManager;
    }

    public MessageField getStatusField() {
        if (statusField == null) {
            statusField = new MessageField();
        }
        return statusField;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public ResourceBundle getMetaData() {
        return metadata;
    }

    public final void setMetaData(ResourceBundle metadata) {
        this.metadata = metadata;
    }

    public String[] getSkins() {
        return skins;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public Parent getRoot() {
        return stage.getScene().getRoot();
    }
}
