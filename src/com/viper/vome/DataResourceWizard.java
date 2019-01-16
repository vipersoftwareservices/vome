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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.viper.vome.dao.Connections;
import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.model.DatabaseConnections;
import com.viper.vome.util.FileUtil;
import com.viper.vome.util.FileWalker;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class DataResourceWizard extends BorderPane {

    final String ATTR_RESOURCE_TYPE = "resource-type";
    final String ATTR_RESOURCE_SERVER = "resource-server";
    final String ATTR_RESOURCE_JNDI = "resource-jndi";
    final String ATTR_SERVER_FILENAME = "server-filename";
    final String ATTR_JNDI_DATASOURCE = "jndi-datasource";

    String SERVER_SCAN_DIRECTORY = "server.scan.directory";

    private List<String> resourceFileExtensions = new ArrayList<String>();
    {
        resourceFileExtensions.add("web.xml");
        resourceFileExtensions.add("sun-web.xml");
        resourceFileExtensions.add("sun-web-app.xml");
        resourceFileExtensions.add("server.xml");
        resourceFileExtensions.add("-ds.xml");
    }
 
    protected Map<String, String> attributes = new HashMap<String, String>();
    protected List<DatabaseConnection> additionalConnections = new ArrayList<DatabaseConnection>();

    public DataResourceWizard( ) { 

        Session session = Session.getInstance();

        DatabaseConnections connections = session.getDatabaseConnections();

        String propertyFilename = LocaleUtil.getProperty("server.directory");
        List<String> filenames = new ArrayList<String>();

        // connections.getConnection();
        List<String> dataSources = new ArrayList<String>();

        FlowPane headerPane = new FlowPane();
        headerPane.getChildren().add(UIUtil.newLabel("Name:"));
        headerPane.getChildren().add(UIUtil.newLabel(attributes, "Name"));

        GridPane centerPane = new GridPane();
        centerPane.setHgap(10); // horizontal gap in pixels => that's what you are asking for
        centerPane.setVgap(10); // vertical gap in pixels
        centerPane.setPadding(new Insets(10, 10, 10, 10));

        ToggleGroup bg = new ToggleGroup();

        add(centerPane, 1, 1, 1, UIUtil.newLabel("Resource Type:"));
        add(centerPane, 2, 1, 1, UIUtil.newRadioButton(ATTR_RESOURCE_SERVER, attributes, ATTR_RESOURCE_SERVER, bg));
        add(centerPane, 3, 1, 1, UIUtil.newRadioButton(ATTR_RESOURCE_JNDI, attributes, ATTR_RESOURCE_JNDI, bg));
 
        add(centerPane, 1, 2, 1, UIUtil.newButton("Browse", this::BrowseDirectoryButton ));
        add(centerPane, 2, 2, 1, UIUtil.newButton("Scan Driectory",  this::ScanDirectoryButton ));
        add(centerPane, 3, 2, 1, UIUtil.newButton("Source", this::SourceAction ));
        add(centerPane, 4, 2, 1, UIUtil.newButton("Validate", this::ValidateResourceAction ));

        add(centerPane, 1, 3, 1, UIUtil.newLabel("Resource Filename:"));
        add(centerPane, 2, 3, 1, UIUtil.newTextField(attributes, ATTR_SERVER_FILENAME, 0));
        add(centerPane, 3, 3, 1, UIUtil.newLabel("Existing Resource Filename(s)"));
        add(centerPane, 4, 3, 1, UIUtil.newScrollListView(attributes, ATTR_SERVER_FILENAME, filenames));
     
        add(centerPane, 1, 4, 1, UIUtil.newLabel("JNDI Datasource:"));
        add(centerPane, 2, 4, 1, UIUtil.newTextField(attributes, ATTR_JNDI_DATASOURCE, 0));
        add(centerPane, 3, 4, 1, UIUtil.newLabel("Existing Data Source(s)"));
        add(centerPane, 4, 4, 1, UIUtil.newScrollListView(attributes, ATTR_JNDI_DATASOURCE, dataSources)); 

        add(centerPane, 1, 5, 1, UIUtil.newLabel("Database Connection:"));
        add(centerPane, 2, 5, 1, UIUtil.newTextField(session.getDatabaseConnection(), "Name", 0));
        add(centerPane, 3, 5, 1, UIUtil.newLabel("Possible Database Connection(s)"));
        add(centerPane, 4, 5, 1, UIUtil.newScrollListView(session.getDatabaseConnection(), "Name", connections.getConnections()));

        add(centerPane, 1, 6, 1, UIUtil.newLabel("Database Connection Name"));
        add(centerPane, 2, 6, 1, UIUtil.newTextField(session.getDatabaseConnection(), "Name", 0));
        add(centerPane, 3, 6, 1, UIUtil.newLabel("Existing Database Connection Names"));
        add(centerPane, 4, 6, 1, UIUtil.newScrollListView(session.getDatabaseConnection(), "Name", connections.getConnections()));

        FlowPane bottomPane = new FlowPane();
        bottomPane.getChildren().add(UIUtil.newButton("Scan", this::ScanResourceAction));

        setTop(headerPane);
        setCenter(centerPane);
        setBottom(bottomPane);

    } 

    public void BrowseResourceButton(ActionEvent e) {
        String filename = attributes.get(ATTR_SERVER_FILENAME);
        if (filename == null || filename.length() == 0) {
            UIUtil.showError("Please enter the resource filename.");
            return ;
        }

        try {
            Connections.openDatabaseConnections(filename);

            List<DatabaseConnection> databaseConnections = Connections.getDatabaseConnectionsList();
            databaseConnections.addAll(additionalConnections);
            for (DatabaseConnection conn : databaseConnections) {
                System.out.println("ResourceFilename[" + conn.getName() + "]");
            }
        } catch (Exception ex) {
            UIUtil.showException("Unable to parse " + filename + " as a database registry.", ex);
        } 
    }

    public void BrowseJNDIButton(ActionEvent e) {
        String datasource = attributes.get(ATTR_JNDI_DATASOURCE);
        if (datasource == null || datasource.length() == 0) {
            UIUtil.showError("Please enter the data source name.");
            return  ;
        }

        try {
            Connections.openDatabaseConnections(datasource);
            additionalConnections = Connections.getDatabaseConnectionsList();
        } catch (Exception ex) {
            UIUtil.showException("Unable to parse " + datasource + " as a database registry.", ex);
        } 
    }
  
    public void BrowseDirectoryButton(ActionEvent e) {
        try {
            Session session = Session.getInstance();
            Connections.getDatabaseConnectionsList().addAll(additionalConnections);
            Connections.saveDatabaseConnections(null);

            session.setDatabaseConnections(session.getDatabaseConnections());

        } catch (Exception ex) {
            UIUtil.showException("<empty> registry name failed", ex);
        }
    }

    public void HelpButton(ActionEvent e) {
       // Dialogs.showHelpDialog(resource, null);
    }

    public void ResourceButton(ActionEvent e) {
        Session session = Session.getInstance();
        String filename = LocaleUtil.getProperty("data.resource.filename");
        filename = UIUtil.showOpenDialog(session.getStage(), "", filename);
        if (filename == null) {
            return;
        }

        List<String> items = new ArrayList<String>();
        items.add(filename);
    }

    public void ScanDirectoryButton(ActionEvent e) {
        final List<String> filenames = new ArrayList<String>();

        while (true) {
            Session session = Session.getInstance();
            String filename = LocaleUtil.getProperty("server.scan.directory");

            filename = UIUtil.showOpenDialog(session.getStage(), "", filename);
            if (filename == null) {
                break;
            }

            File file = new File(filename);
            if (!file.exists()) {
                UIUtil.showError("File " + filename + " does not exist.");
                continue;
            }
            if (!file.canRead()) {
                UIUtil.showError("Read access to file " + filename + " is denied.");
                continue;
            }
            if (!file.isDirectory()) {
                filename = file.getParent();
            }
            for (String extension : resourceFileExtensions) {
                filenames.addAll(FileWalker.find(filename, extension));
            }
            break;
        }
    }

    public void ScanResourceAction(ActionEvent e) {
        Session session = Session.getInstance();
        final List<String> filenames = new ArrayList<String>();
        while (true) {
            String filename = UIUtil.showOpenDialog(session.getStage(), "", null);
            if (filename == null) {
                break;
            }

            File file = new File(filename);
            if (!file.exists()) {
                UIUtil.showError("File " + filename + " does not exist.");
                continue;
            }
            if (!file.canRead()) {
                UIUtil.showError("Read access to file " + filename + " is denied.");
                continue;
            }
            if (!file.isDirectory()) {
                filename = file.getParent();
            }
            for (String extension : resourceFileExtensions) {
                filenames.addAll(FileWalker.find(filename, extension));
            }
            break;
        }
    }

    public void ValidateResourceAction(ActionEvent e) {
        List<String> allProblems = new ArrayList<String>();
        Object[] items = null; // listPane.getSelectedValues();
        if (items != null) {
            for (Object item : items) {
                String filename = (String) item;
                try {
                    List<String> problems = null; // new
                                                  // ConnectionsConverter().getDatabaseResourceProblems(filename);
                    if (problems != null) {
                        allProblems.addAll(problems);
                    }
                } catch (Exception ex) {
                    UIUtil.showException("Source Action TODO", ex);
                }
            }
        }
        Dialogs.showListDialog("Resource Validation", allProblems);
    }

    public void SourceAction(ActionEvent e) {
        try {
            Object item = null; // TODO listPane.getSelectedValue();
            if (item != null) {
                String filename = (String) item;
                List<String> lines = FileUtil.readFileViaLines(filename);
                Dialogs.showListDialog("Source for: " + filename, lines);
            }
        } catch (Exception ex) {
            UIUtil.showException("Source Action TODO", ex);
        }
    }

    private final void add(GridPane pane, int column, int row, int ncols, Control child) {
        GridPane.setConstraints(child, column, row, ncols, 1); // column=2 row=0
        pane.getChildren().add(child);
    }

}
