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

package com.viper.test;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import org.junit.Assert;

import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.robot.FXRobot;
import com.viper.vome.DatabaseViewer;
import com.viper.vome.Session;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.WindowEvent;

public class FXHarness {

    public synchronized static void start(final Class clazz) throws Exception {

        if (DatabaseViewer.getInstance() != null) {
            return;
        }

        new JFXPanel(); // this will prepare JavaFX toolkit and environment

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Thread thread = new Thread("JavaFX Initialize Thread") {
                    @Override
                    public void run() {
                        Application.launch(clazz, new String[0]);
                    }
                };
                thread.setDaemon(true);
                thread.start();
            }
        });

        Handler windowShown = new Handler();
        for (int counter = 0; counter < 60; counter++) {
            Session session = Session.getInstance();
            DatabaseViewer app = DatabaseViewer.getInstance();
            if (app != null && session != null && session.getStage() != null) {
                if (session.getStage().isShowing()) {
                    session.getStage().setOnShown(windowShown);
                    windowShown.await();
                    break;
                }
            }
            Thread.sleep(100);
        }
    }

    static class Handler implements EventHandler<WindowEvent> {

        CountDownLatch signal = new CountDownLatch(1);

        public void await() throws Exception {
            signal.await();
        }

        @Override
        public void handle(WindowEvent arg0) {
            signal.countDown();
        }
    }

    public static void stop() throws Exception {
        // Platform.exit();
    }

    public static Node lookupNode(Scene scene, String... names) throws Exception {
        Node node = scene.getRoot();
        for (String name : names) {
            node = node.lookup(name);
            if (node == null) {
                throw new NullPointerException("Could not lookup " + name + " of " + names);
            }
        }
        return node;
    }

    public static MenuItem lookupMenuItem(Scene scene, String menubar, String... titles) throws Exception {
        MenuBar menuBar = (MenuBar) FXHarness.lookupNode(scene, menubar);
        if (menuBar == null) {
            return null;
        }

        MenuItem item = null;
        ObservableList<Menu> menus = menuBar.getMenus();
        for (String title : titles) {
            // for (Menu menu : menus) {
            // if (title.equals(item.getText())) {
            // item = menu;
            // menus = menu.getItems();
            // }
            // }
            //
            // item = lookupMenuItem(menus, title);
            // if (item instanceof Menu) {
            // menus = (item.gethelp());
            // }
        }
        return item;
    }

    public static MenuItem lookupMenuItem(ObservableList<Menu> menus, String title) throws Exception {
        for (Menu menu : menus) {
            MenuItem item = lookupMenuItem(menu, title);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    public static MenuItem lookupMenuItem(MenuItem item, String title) throws Exception {
        if (title.equals(item.getText())) {
            return item;
        }
        if (item instanceof Menu) {
            for (MenuItem menu : ((Menu) item).getItems()) {
                MenuItem m = lookupMenuItem(menu, title);
                if (m != null) {
                    return m;
                }
            }
        }
        return null;
    }

    public static MenuItem lookupMenuItemChild(MenuItem item, String title) throws Exception {
        if (item instanceof Menu) {
            for (MenuItem menu : ((Menu) item).getItems()) {
                if (title.equals(item.getText())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static TreeItem lookupTreeItem(TreeItem item, String title) throws Exception {

        if (item != null && title.equals(item.getValue().toString())) {
            System.err.println("FOUND: lookupTreeItem: " + item.getValue().toString() + "," + title);
            return item;
        }

        System.err.println("lookupTreeItem: " + item.getValue().toString() + "," + title + ":" + item.getChildren().size());
        for (Object child : item.getChildren()) {
            TreeItem c = lookupTreeItem((TreeItem) child, title);
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    public static TreeItem fire(TreeView tree, TreeItem item, String title) throws Exception {
        TreeItem newItem = lookupTreeItem(item, title);
        Assert.assertNotNull("FXHarness.fire: " + item + "=" + title, newItem);

        runAndWait(() -> tree.getSelectionModel().select(newItem));
        return newItem;
    }

    public static void fire(final MenuItem menu) throws Exception {
        runAndWait(() -> menu.fire());
    }

    public static void fire(final Button button) throws Exception {
        runAndWait(() -> button.fire());
    }

    public static void mouseMove(FXRobot robot, Node node) {
        Point2D pt = toScreenPosition(node);
        robot.mouseMove((int) pt.getX(), (int) pt.getY());
    }

    public static Point2D toScreenPosition(Node node) {
        final Scene scene = node.getScene();
        final Point2D windowCoordinates = new Point2D(scene.getWindow().getX(), scene.getWindow().getY());
        final Point2D sceneCoordinates = new Point2D(scene.getX(), scene.getY());
        final Point2D nodeCoordinates = node.localToScene(0.0, 0.0);
        final double X = Math.round(windowCoordinates.getX() + sceneCoordinates.getX() + nodeCoordinates.getX());
        final double Y = Math.round(windowCoordinates.getY() + sceneCoordinates.getY() + nodeCoordinates.getY());

        return new Point2D(X, Y);
    }

    private static void saveScene(WritableImage image, String filename) throws Exception {
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File(filename));
    }

    private static WritableImage readImage(String filename) throws Exception {
        return SwingFXUtils.toFXImage(ImageIO.read(new File(filename)), null);
    }

    public static void assertEquals(String msg, String filename, Scene scene) throws Exception {
        callAndWait(() -> assertEqualsInternal(msg, filename, scene));
    }

    private static boolean assertEqualsInternal(String msg, String filename, Scene scene) throws Exception {
        if (!new File(filename).exists()) {
            saveScene(scene.getRoot().snapshot(null, null), filename);
            return true;
        }
        WritableImage expected = readImage(filename);
        WritableImage actual = scene.getRoot().snapshot(null, null);

        assertEquals(msg, expected, actual);
        return true;
    }

    public static void assertEquals(String msg, WritableImage expected, WritableImage actual) throws Exception {
        if (expected.equals(actual)) {
            return;
        }
        Assert.assertEquals(msg + ".Height", expected.getHeight(), actual.getHeight(), 1.0);
        Assert.assertEquals(msg + ".Width", expected.getWidth(), actual.getWidth(), 1.0);

        PixelReader eReader = expected.getPixelReader();
        PixelReader aReader = actual.getPixelReader();

        for (int x = 0; x < actual.getWidth(); x++) {
            for (int y = 0; y < actual.getHeight(); y++) {
                Assert.assertEquals(msg + ".Color", eReader.getArgb(x, y), aReader.getArgb(x, y));
            }
        }
    }

    public static void runLater(Runnable runnable) throws Exception {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return;
        }
        final AtomicReference<Exception> exception = new AtomicReference<Exception>();

        PlatformImpl.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    // store our exception thrown by the inner thread
                    exception.set(e);
                }
            }
        });

        if (exception.get() != null) {
            throw exception.get();
        }
        Thread.sleep(100);
    }

    public static void runAndWait(Runnable runnable) throws Exception {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return;
        }
        final AtomicReference<Exception> exception = new AtomicReference<Exception>();

        PlatformImpl.runAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    // store our exception thrown by the inner thread
                    exception.set(e);
                }
            }
        });

        if (exception.get() != null) {
            throw exception.get();
        }
    }

    public static <V> void callAndWait(Callable<V> runnable) throws Exception {
        if (Platform.isFxApplicationThread()) {
            runnable.call();
            return;
        }
        final AtomicReference<Exception> exception = new AtomicReference<Exception>();

        PlatformImpl.runAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.call();
                } catch (Exception e) {
                    // store our exception thrown by the inner thread
                    exception.set(e);
                }
            }
        });

        if (exception.get() != null) {
            throw exception.get();
        }
    }
}
