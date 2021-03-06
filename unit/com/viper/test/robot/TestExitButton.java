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

import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class TestExitButton extends AbstractTestCase {

    @BeforeClass
    public static void initialize() throws Exception {
        FXHarness.start(DatabaseViewer.class);
    }

    @AfterClass
    public static void finish() throws Exception {
        FXHarness.stop();
    }

    @Test
    public void testExitButton() throws Exception {

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

        MenuItem connectionMenu = FXHarness.lookupMenuItem(menuBar.getMenus(), "Connection");
        FXHarness.runAndWait(() -> connectionMenu.fire());

        // FXHarness.fire(FXHarness.lookupMenuItem(connectionMenu, "Exit"));

        FXHarness.assertEquals("testExitButton", "unit/data/results/TestExitButton.png", scene);
    }
}
