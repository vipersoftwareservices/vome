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
import java.io.IOException;

import com.viper.vome.jfx.UIUtil;
import com.viper.vome.jfx.Wizard;
import com.viper.vome.jfx.WizardPane;
import com.viper.vome.util.FileUtil;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InstallWizard extends Application {

    final static String CARD_SPLASH_PAGE = "card1";
    final static String CARD_LICENSE = "card2";
    final static String CARD_DIRECTORY = "card3";
    final static String CARD_CONFIRMATION = "card4";
    final static String CARD_LAUNCH_APPLICATION = "card5";

    private String installationDirectory = getDefaultInstallationDirectory();
    private InstallWizard wizard = this;

    @Override
    public void start(Stage parentStage) {

        Stage stage = new Stage();
        Wizard wizard = new Wizard("VOME Installation");
        wizard.getScene().getStylesheets().setAll(stage.getScene().getStylesheets());
        wizard.addCard(new SplashPane(CARD_SPLASH_PAGE));
        wizard.addCard(new LicensePane(CARD_LICENSE));
        wizard.addCard(new DirectoryPane(CARD_DIRECTORY));
        wizard.addCard(new ConfirmPane(CARD_CONFIRMATION));
        wizard.addCard(new LaunchApplicationPane(CARD_LAUNCH_APPLICATION));

        Scene scene = new Scene(new VBox(), 500, 500);
        scene.getStylesheets().add(getClass().getResource("DatabaseViewer.css").toExternalForm());

        stage.setTitle("Viper Software Services | Database Viewer");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/favicon.gif")));
        stage.setScene(scene);
        stage.show();
    }

    private String getDefaultInstallationDirectory() {
        if (System.getProperty("os.name").toLowerCase().startsWith("window")) {
            return "C:\\Program Files\\Viper Software Services";
        }
        return "/opt/viper";
    }

    // -------------------------------------------------------------------------

    public String getInstallationDirectory() {
        return installationDirectory;
    }

    public void setInstallationDirectory(String installationDirectory) {
        this.installationDirectory = installationDirectory;
    }

    class SplashPane extends WizardPane {
        public SplashPane(String cardname) {
            super(cardname);

            Session session = Session.getInstance();
            String headerTxt = LocaleUtil.getProperty("InstallWizard.splash-pane.header-text");
            String messageHtml = LocaleUtil.getProperty("InstallWizard.splash-pane.body-text");

            HTMLEditor htmlPane = new HTMLEditor();
            htmlPane.setHtmlText(messageHtml);

            getChildren().add(UIUtil.newLabel(headerTxt));
            getChildren().add(UIUtil.newScrollPane(htmlPane));
        }

        public String actionPerformed(ActionEvent e) {
            return CARD_LICENSE;
        }
    }

    // -------------------------------------------------------------------------

    class LicensePane extends WizardPane {

        private boolean isLicenseAgreement = false;

        public LicensePane(String cardname) {
            super(cardname);

            Session session = Session.getInstance();
            String headerTxt = LocaleUtil.getProperty("InstallWizard.license-pane.header-text");
            String header2Txt = LocaleUtil.getProperty("InstallWizard.license-pane.header2-text");
            String header2ATxt = LocaleUtil.getProperty("InstallWizard.license-pane.header2A-text");
            String messageHtml = readFile("res:/license.html");
            String body1Txt = LocaleUtil.getProperty("InstallWizard.license-pane.body1-text");
            String body2Txt = LocaleUtil.getProperty("InstallWizard.license-pane.body2-text");
            String body2ATxt = LocaleUtil.getProperty("InstallWizard.license-pane.body2A-text");
            String checkboxTxt = LocaleUtil.getProperty("InstallWizard.license-pane.checkbox-text");

            HTMLEditor htmlPane = new HTMLEditor();
            htmlPane.setHtmlText(messageHtml.toString());

            getChildren().add(UIUtil.newLabel(headerTxt));
            getChildren().add(UIUtil.newLabel(header2Txt));
            getChildren().add(UIUtil.newLabel(header2ATxt));
            getChildren().add(UIUtil.newLabel(body1Txt));
            getChildren().add(UIUtil.newCheckBox(checkboxTxt, this, "licenseAgreement"));
            getChildren().add(UIUtil.newLabel(body2Txt));
            getChildren().add(UIUtil.newLabel(body2ATxt));
            getChildren().add(UIUtil.newScrollPane(htmlPane));
        }

        public boolean isLicenseAgreement() {
            return isLicenseAgreement;
        }

        public void setLicenseAgreement(boolean isLicenseAgreement) {
            this.isLicenseAgreement = isLicenseAgreement;
        }

        private String readFile(String filename) {
            try {
                return FileUtil.readFile(filename);
            } catch (Exception e) {
                System.err.println("Unable to read file: " + filename);
                e.printStackTrace();
            }
            return null;
        }

        public String actionPerformed(ActionEvent e) {
            if (!isLicenseAgreement()) {
                Session session = Session.getInstance();
                UIUtil.showMessage(LocaleUtil.getProperty("InstallWizard.license-pane.no-agreement-text"));
                return null;
            }
            return CARD_DIRECTORY;
        }
    }

    // -------------------------------------------------------------------------

    class DirectoryPane extends WizardPane {
        public DirectoryPane(String cardname) {
            super(cardname);

            Session session = Session.getInstance();
            String messageHtml = LocaleUtil.getProperty("InstallWizard.directory-pane.text");
            String headerTxt = LocaleUtil.getProperty("InstallWizard.directory-pane.header-text");

            HTMLEditor htmlPane = new HTMLEditor();
            htmlPane.setHtmlText(messageHtml);

            getChildren().add(UIUtil.newLabel(headerTxt));
            getChildren().add(UIUtil.newScrollPane(htmlPane));
            getChildren().add(UIUtil.newLabel("Installation Directory:"));
            getChildren().add(UIUtil.newTextField(wizard, "installationDirectory", 0));
            getChildren().add(UIUtil.newButton("Browse", new BrowseDirectoryButton()));
        }

        public String actionPerformed(ActionEvent e) {

            Session session = Session.getInstance();
            String messageHtml = LocaleUtil.getProperty("InstallWizard.confirm.text");
            if (installationDirectory == null) {
                installationDirectory = "";
            }
            // confirmEP.setText(messageHtml.replace("%%DIRECTORY%%", installationDirectory));

            return CARD_CONFIRMATION;
        }
    }

    // -------------------------------------------------------------------------

    class BrowseDirectoryButton implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            File file = Dialogs.showDirectoryDialog(null, "", installationDirectory);
            if (file != null) {
                setInstallationDirectory(file.getAbsolutePath());
            }
        }
    }

    // -------------------------------------------------------------------------

    class ConfirmPane extends WizardPane implements ChangeListener {
        ProgressBar progressBar = new ProgressBar();

        public ConfirmPane(String cardname) {
            super(cardname);

            Session session = Session.getInstance();
            String messageHtml = LocaleUtil.getProperty("InstallWizard.confirm.text");
            String headerTxt = LocaleUtil.getProperty("InstallWizard.confirm.header-text");

            // progressBar.setMinimum(0);
            // progressBar.setMaximum(100);

            HTMLEditor confirmEP = new HTMLEditor();
            confirmEP.setHtmlText(messageHtml);

            getChildren().add(UIUtil.newLabel(headerTxt));
            getChildren().add(UIUtil.newScrollPane(confirmEP));
            getChildren().add(progressBar);
        }

        public String actionPerformed(ActionEvent e) {

            String directory = installationDirectory.replace('\\', '/');

            // Check if installation directory exists.
            File file = new File(directory);
            if (!file.exists()) {
                file.mkdir();
            }

            // Copy the vome.jar into the installation directory
            try {

                Session session = Session.getInstance();
                String filename = LocaleUtil.getProperty("databases-filename");
                FileUtil.copyFile(getClass(), "res:/" + filename, directory + "/" + filename, null);
                FileUtil.copyFile(getClass(), "res:/vome.jar", directory + "/vome.jar");

            } catch (IOException e1) {
                e1.printStackTrace();
            }

            // Create the vome shortcut.
            String os = System.getProperty("os.name");
            if (os.toLowerCase().startsWith("windows")) {
                String programDirectory = "c:/Documents and Settings/All Users.WINDOWS/Start Menu/Programs/Viper Software Services";
                File shortcutDirectory = new File(programDirectory);
                if (!shortcutDirectory.getParentFile().exists()) {
                    System.out.println("Directory does not exist or no access: " + shortcutDirectory.getParent()
                            + " short cut not created.");
                    return CARD_LAUNCH_APPLICATION;
                }
                if (!shortcutDirectory.exists()) {
                    shortcutDirectory.mkdir();
                }
                String shortcutFilename = programDirectory + "/vome.url";
                String shortcutText = getWindowsShortCut(directory);
                System.out.print(shortcutText);
                try {
                    FileUtil.writeFile(shortcutFilename, shortcutText);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else {
                System.out.println("Property os.name=" + os);
            }

            return CARD_LAUNCH_APPLICATION;
        }

        public void changed(ObservableValue value, Object o1, Object o2) {
            // progressBar.setProgess((double) e.getSource());
        }
    }

    public static int SW_HIDE = 0x0;
    public static int SW_NORMAL = 0x1;
    public static int SW_SHOWMINIMIZED = 0x2;
    public static int SW_SHOWMAXIMIZED = 0x3;

    public static int HOTKEY_SHIFT = 0x0100;
    public static int HOTKEY_CTRL = 0x0200;
    public static int HOTKEY_ALT = 0x0400;

    private String getWindowsShortCut(String installDirectory) {
        String template = "";
        String nl = System.getProperty("line.separator");

        // It's a UTF-8 file - write the BOM
        // int v[] = new int[]{239, 187, 191};
        byte v[] = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        String header = new String(v, 0, v.length);

        template += header;
        template += "[InternetShortcut]" + nl;
        template += "URL=file://#DIRECTORY#/vome.jar" + nl;
        template += "WorkingDirectory=#DIRECTORY#/" + nl;
        template += "ShowCommand=" + SW_NORMAL + nl;
        template += "IconIndex=0" + nl;
        template += "IconFile=#DIRECTORY#/icon.ico" + nl;
        template += "HotKey=" + (HOTKEY_CTRL | HOTKEY_SHIFT | 'V');

        return template.replace("#DIRECTORY#", installDirectory);
    }

    // -------------------------------------------------------------------------

    class LaunchApplicationPane extends WizardPane {
        CheckBox launchApplicationCB = new CheckBox();

        public LaunchApplicationPane(String cardname) {
            super(cardname);

            Session session = Session.getInstance();
            String headerTxt = LocaleUtil.getProperty("InstallWizard.launch-application.header-text");
            String messageHtml = LocaleUtil.getProperty("InstallWizard.launch-application.text");
            String checkboxTxt = LocaleUtil.getProperty("InstallWizard.launch-application.checkbox-text");

            launchApplicationCB.setText(checkboxTxt);

            HTMLEditor htmlPane = new HTMLEditor();
            htmlPane.setHtmlText(messageHtml);

            getChildren().add(UIUtil.newLabel(headerTxt));
            getChildren().add(UIUtil.newScrollPane(htmlPane));
            getChildren().add(launchApplicationCB);
        }

        public String actionPerformed(ActionEvent e) {
            if (launchApplicationCB.isSelected()) {
                String cmd = "java -jar vome.jar";
                try {
                    Runtime.getRuntime().exec(cmd, null, new File(installationDirectory));
                } catch (IOException ex1) {
                    ex1.printStackTrace();
                }
            }
            return "DONE";
        }
    }

    // -------------------------------------------------------------------------

    public static void main(String[] args) throws ClassNotFoundException {

        ClassLoader cl = new JarClassLoader(InstallWizard.class.getResource("/jhall.jar"));
        Class.forName("javax.help.CSH", true, cl);
        Class.forName("javax.help.HelpBroker", true, cl);
        Class.forName("javax.help.HelpSet", true, cl);
        Class.forName("javax.help.SecondaryWindow", true, cl);

        launch(args);
    }
}
