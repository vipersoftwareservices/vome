/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java    1.00 2014/06/15
 *
 * Copyright 1998-2014 by Viper Software Services
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
 * @version 1.0, 06/15/2014
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.test.robot;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;
import com.viper.test.AbstractTestCase;
import com.viper.test.FXHarness;
import com.viper.vome.DatabaseViewer;
import com.viper.vome.LocaleUtil;
import com.viper.vome.Session;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class TestHelpAboutButton extends AbstractTestCase {

    @BeforeClass
    public static void initialize() throws Exception {
        FXHarness.start(DatabaseViewer.class);
        Platform.setImplicitExit(false);
    }

    @AfterClass
    public static void finish() throws Exception {
        FXHarness.stop();
    }

    @Test
    public void testBasicUI() throws Exception {

        DatabaseViewer fxa = DatabaseViewer.getInstance();
        Session session = Session.getInstance();

        assertNotNull("fxa is null", fxa);
        assertNotNull("fxa.session is null", session);
        assertNotNull("fxa.session.stage is null", session.getStage());
        assertNotNull("fxa.session.stage.scene is null", session.getStage().getScene());
        assertNotNull("fxa.session.bundle is null", LocaleUtil.getBundle());

        Scene scene = session.getStage().getScene();
        FXRobot robot = FXRobotFactory.createRobot(scene);
        robot.setAutoWaitForIdle(true);

        MenuBar menuBar = (MenuBar) FXHarness.lookupNode(scene, "#MenuBar");
        assertNotNull("MenuBar not found", menuBar);

        MenuItem helpMenu = FXHarness.lookupMenuItem(menuBar.getMenus(), "Help");
        assertNotNull("MenuBar not found", helpMenu);
        FXHarness.runAndWait(() -> helpMenu.fire());

        MenuItem aboutMenu = FXHarness.lookupMenuItem(helpMenu, "About");
        assertNotNull("About Menu not found", aboutMenu);

        FXHarness.runLater(() -> aboutMenu.fire());

        Dialog dialog = Session.getInstance().getDialog();
        assertNotNull("Dialog not found", dialog);

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        assertNotNull("Cancel button not found", cancelButton);

        FXHarness.runAndWait(() -> cancelButton.fire());

        MenuItem connectionMenu = FXHarness.lookupMenuItem(menuBar.getMenus(), "Connection");
        FXHarness.runAndWait(() -> connectionMenu.fire());

        // FXHarness.fire(FXHarness.lookupMenuItem(connectionMenu, "Exit"));
    }
}
