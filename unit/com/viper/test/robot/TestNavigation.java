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
import com.viper.vome.NavigationTree;
import com.viper.vome.Session;

import javafx.scene.Scene;
import javafx.scene.control.TreeItem;

public class TestNavigation extends AbstractTestCase {

    @BeforeClass
    public static void initialize() throws Exception {
        FXHarness.start(DatabaseViewer.class);
    }

    @AfterClass
    public static void finish() throws Exception {
        FXHarness.stop();
    }

    @Test
    public void testNavigation() throws Exception {

        Session session = Session.getInstance();
        DatabaseViewer fxa = DatabaseViewer.getInstance();

        assertNotNull("fxa is null", fxa);
        assertNotNull("fxa.session is null", session);
        assertNotNull("fxa.session.stage is null", session.getStage());
        assertNotNull("fxa.session.stage.scene is null", session.getStage().getScene());
        assertNotNull("fxa.session.bundle is null", LocaleUtil.getBundle());

        Scene scene = session.getStage().getScene();
        FXRobot robot = FXRobotFactory.createRobot(scene);
        robot.setAutoWaitForIdle(true);

        NavigationTree tree = (NavigationTree) FXHarness.lookupNode(scene, "#navigationtree");
        assertNotNull(" TreeView1 not found", tree);

        TreeItem item1 = FXHarness.fire(tree, tree.getRoot(), "../databases.xml");
        assertNotNull(" TreeItem2 (../databases.xml) not found", item1);

        TreeItem item2 = FXHarness.fire(tree, item1, "test");
        assertNotNull(" TreeItem3 (test) not found", item2);

        TreeItem item3 = FXHarness.fire(tree, item2, "information_schema");
        assertNotNull(" TreeItem4 (information_schema) not found", item3);

        TreeItem item4 = FXHarness.fire(tree, item3, "COLLATIONS");
        assertNotNull(" TreeItem5 (COLLATIONS) not found", item4);

        FXHarness.assertEquals("testNavigation", "unit/data/results/TestNavigation001.png", scene);
    }

    @Test
    public void testNavigationAll() throws Exception {

        Session session = Session.getInstance();
        DatabaseViewer fxa = DatabaseViewer.getInstance();

        assertNotNull("fxa is null", fxa);
        assertNotNull("fxa.session is null", session);
        assertNotNull("fxa.session.stage is null", session.getStage());
        assertNotNull("fxa.session.stage.scene is null", session.getStage().getScene());
        assertNotNull("fxa.session.bundle is null", LocaleUtil.getBundle());

        Scene scene = session.getStage().getScene();
        FXRobot robot = FXRobotFactory.createRobot(scene);
        robot.setAutoWaitForIdle(true);

        NavigationTree tree = (NavigationTree) FXHarness.lookupNode(scene, "#navigationtree");
        assertNotNull(" TreeView1 not found", tree);

        TreeItem item1 = FXHarness.fire(tree, tree.getRoot(), "../databases.xml");
        assertNotNull(" TreeItem2 (../databases.xml) not found", item1);

        TreeItem item2 = FXHarness.fire(tree, item1, "test");
        assertNotNull(" TreeItem3 (test) not found", item2);

        testNodes(tree, item2);

        FXHarness.assertEquals("testNavigation", "unit/data/results/TestNavigationAll.png", scene);
    }

    private void testNodes(NavigationTree tree, TreeItem item) throws Exception {
        if (item == null) {
            return;
        }

        FXHarness.runAndWait(() -> tree.getSelectionModel().select(item));

        System.err.println("testNodes: " + item.getValue().toString() + ":" + item.getChildren().size());
        for (Object child : item.getChildren()) {
            testNodes(tree, (TreeItem)child);
        }
    }
}
