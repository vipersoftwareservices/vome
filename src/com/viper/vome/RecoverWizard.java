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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.DatabaseConnection;
import com.viper.vome.model.Selections;
import com.viper.vome.util.FileUtil;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class RecoverWizard extends BorderPane {

    protected Selections selector = new Selections();

    public RecoverWizard() {

        Session session = Session.getInstance();

        FlowPane headerPane = new FlowPane(5, 5);
        headerPane.getChildren().add(UIUtil.newLabel("Database:"));
        headerPane.getChildren().add(UIUtil.newLabel(session.getDatabaseConnection(), "Name"));

        String filename = System.getProperty("user.dir") + "/" + LocaleUtil.getProperty("default-backup-filename");
        FileUtil.mkPath(filename);

        GridPane centerPane = new GridPane();
        centerPane.setHgap(10); // horizontal gap in pixels => that's what you are asking for
        centerPane.setVgap(10); // vertical gap in pixels
        centerPane.setPadding(new Insets(10, 10, 10, 10));

        add(centerPane, 1, 3, 1, UIUtil.newLabel("Recovery Filename"));
        add(centerPane, 2, 3, 1, UIUtil.newComboBox(selector, "filename", null, null));
        add(centerPane, 3, 3, 1, UIUtil.newButton("Browse", this::BrowseFilename));

        FlowPane bottomPane = new FlowPane(5, 5);
        bottomPane.getChildren().add(UIUtil.newButton("Execute Backup", null, null, this::ExecuteRecoveryAction));

        setTop(headerPane);
        setCenter(centerPane);
        setBottom(bottomPane);
    }

    private final void add(GridPane pane, int column, int row, int ncols, Control child) {
        GridPane.setConstraints(child, column, row, ncols, 1); // column row colspan rowspan
        pane.getChildren().add(child);
    }

    public void ExecuteRecoveryAction(ActionEvent e) {

        Session session = Session.getInstance();

        String name = selector.getFilename();
        if (name == null || name.length() == 0) {
            UIUtil.showError("Please enter the recovery filename");
            return;
        }

        DatabaseConnection dbc = session.getDatabaseConnection();
        if (dbc != null) {
            String filename = selector.getFilename();
            if (!new File(filename).exists()) {
                UIUtil.showError("Recovery file does not exists: " + filename);
            }
            String command = null;
            try {
                // TODO command = session.getDriver(dbc).recoverDatabase(dbc, selector);

                UIUtil.showStatus("Recovery in process...");
                UIUtil.showStatus(command);

                String status = exec(command, filename);

                UIUtil.showStatus("Recovery finished: " + status);
            } catch (Exception ex) {
                String msg = "Failed to execute recovery command: " + command;
                UIUtil.showException(msg, ex);
            }
            session.getChangeManager().fireChangeEvent(selector, "Filename", filename);
        }
    }

    // -------------------------------------------------------------------------

    public void BrowseFilename(ActionEvent e) {

        Session session = Session.getInstance();

        selector.setFilename(UIUtil.showOpenDialog(session.getStage(), "Browse filename", selector.getFilename()));

        if (selector.getFilename() != null) {
            session.getChangeManager().fireChangeEvent(selector, "Filename", selector.getFilename());
        }
    }

    // -------------------------------------------------------------------------

    public String exec(String cmd, String filename) throws IOException, InterruptedException {
        System.out.println("cmd: " + cmd);

        String cwd = System.getProperty("user.dir");
        Process proc = Runtime.getRuntime().exec(cmd, null, new File(cwd));

        BufferedWriter stdin = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
        if (filename != null) {
            stdin.write(FileUtil.readFile(filename).toString());
            stdin.newLine();
        }
        stdin.flush();
        stdin.close();
        StringBuffer stdout = new StringBuffer();
        StringBuffer stderr = new StringBuffer();

        new StreamThread(proc.getInputStream(), stdout).start();
        new StreamThread(proc.getErrorStream(), stderr).start();

        proc.waitFor();

        System.out.println("stdout: " + stdout);
        System.out.println("stderr: " + stderr);
        return stdout.toString();
    }

    class StreamThread extends Thread {
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
}
