/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2008/01/15
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
 * @version 1.0, 01/15/2008 
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.vome;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.StatusBar;

import com.viper.vome.jfx.UIUtil;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;

public class MessageField extends StatusBar {

    private WebView editor = null;
    private List<String> messages = new ArrayList<String>();

    public MessageField() {

        initialize();

        Button button = new Button("...");

        getRightItems().add(button);

        button.setOnAction(event -> showLog());
    }

    private void initialize() {
        if (editor == null) {
            editor = new WebView();

        }
    }

    public void showLog() {
        showInfo("");

        Tab tab = UIUtil.newTab("Message Log", editor);

        Session session = Session.getInstance();
        TableTabbedPane tabpane = session.getTableTabbedPane();

        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);

    }

    public void showInfo(String msg) {
        final String message = "FYI: " + msg;
        final String style = "color: black; font-size: 14px; font-weight: bold";

        if (msg == null || msg.length() == 0) {
            setText("");
            return;
        }

        setText(message);
        setStyle(style);

        append("<div style='" + style + "'>" + message + "</div><br/>\n");
    }

    public void showWarning(String msg) {
        final String message = "WARNING: " + msg;
        final String style = "color: yellow; font-size: 14px; font-weight: bold";

        setText(message);
        setStyle(style);

        append("<div style='" + style + "'>" + message + "</div><br/>\n");
    }

    public void showError(String msg) {
        final String message = "FAILED: " + msg;
        final String style = "color: red; font-size: 14px; font-weight: bold";

        setText(message);
        setStyle(style);

        append("<div style='" + style + "'>" + message + "</div><br/>\n");
    }

    public void showError(String msg, Throwable e) {
        showError(msg);
        showError(e);
    }

    public void showError(Throwable e) {
        if (e == null) {
            return;
        }

        StringWriter buf = new StringWriter();
        PrintWriter out = new PrintWriter(buf);
        e.printStackTrace(out);

        final String message = "FAILED: " + e.getMessage();
        final String style1 = "color: red; font-size: 14px; font-weight: bold";
        final String style2 = "color: #0000aa; font-size: 10px; font-weight: normal";

        setText(message);
        setStyle(style1);

        append("<div style='" + style1 + "'>" + message + "</div><br/>\n");
        append("<div style='" + style2 + "'>" + buf.toString() + "</div><br/>\n");
    }

    private String buildHtml(List<String> messages) {

        String html = "";
        html += "<html>";
        html += "<body>";
        html += "<div id='content'>";
        for (String message : messages) {
            html += message;
        }
        html += "</div>";
        html += "</body>";
        html += "</html>";

        return html;
    }

    private void append(String text) {

        messages.add(text);

        if (editor == null) {
            System.err.println(text);
            return;
        }
        if (editor.getEngine() == null) {
            System.err.println(text);
            return;
        }
        if (editor.getEngine().getDocument() == null) {
            System.err.println(text);
            return;
        }
        editor.getEngine().load(buildHtml(messages));
    }
}
