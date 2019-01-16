/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java    1.00 2003/06/15
 *
 * Copyright 1998-2003 by Viper Software Services
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
 * @version 1.0, 06/15/2003 
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.vome.jfx;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;

public class Wizard extends BorderPane {

    public final static String ACTION_NEXT = "NEXT";
    public final static String ACTION_BACK = "BACK";
    public final static String ACTION_DONE = "DONE";
    public final static String ACTION_CANCEL = "CANCEL";

    private SimpleBooleanProperty isLastProperty = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isFirstProperty = new SimpleBooleanProperty(true);
    private SimpleStringProperty titleProperty = new SimpleStringProperty("Title");
    private SimpleStringProperty messageProperty = new SimpleStringProperty("");

    Dialog<ButtonType> dialog = null;
    StackPane stackPane = new StackPane();

    public Wizard(String title) {
        super();

        Label titleLabel = new Label();
        titleLabel.getStyleClass().add("wizard-dialog-title");
        titleLabel.textProperty().bindBidirectional(titleProperty);
        titleLabel.setCache(true);
        titleLabel.setTextFill(Color.rgb(250, 15, 60));
        Reflection r = new Reflection();
        r.setFraction(0.7f);
        // titleLabel.setEffect(r);

        FlowPane titlePane = new FlowPane(Orientation.HORIZONTAL);
        titlePane.setAlignment(Pos.CENTER);
        titlePane.getChildren().add(titleLabel);

        Label messageLabel = new Label();
        messageLabel.textProperty().bindBidirectional(messageProperty);
        messageLabel.setTextFill(Color.RED);
        
        FlowPane messagePane = new FlowPane(Orientation.HORIZONTAL);
        messagePane.setAlignment(Pos.CENTER);
        messagePane.getChildren().add(messageLabel);

        stackPane.getStyleClass().add("wizard-center-pane");

        getStyleClass().add("wizard-dialog");
        setTop(titlePane);
        setCenter(stackPane);
        setBottom(messagePane);

        ImageView banner = new ImageView("/images/wizardside.gif");
        banner.setPreserveRatio(false);
        banner.setSmooth(true);
        banner.setCache(true);
        banner.fitHeightProperty().bind(stackPane.heightProperty());
        banner.getStyleClass().add("wizard-sidebanner");

        dialog = new Dialog<ButtonType>();
        dialog.getDialogPane().setContent(this);
        dialog.setGraphic(banner);
        dialog.setTitle(title);
        dialog.setResizable(true);
        dialog.initModality(Modality.APPLICATION_MODAL);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.PREVIOUS);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.NEXT);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.FINISH);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        final Button previousButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.PREVIOUS);
        previousButton.addEventFilter(ActionEvent.ACTION, event -> {
            int index = getCurrentPane();
            show((index <= 0) ? 0 : index - 1);

            event.consume();
        });

        final Button nextButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.NEXT);
        nextButton.addEventFilter(ActionEvent.ACTION, event -> {
            int index = getCurrentPane();
            WizardPane pane = (WizardPane) stackPane.getChildren().get(index);
            if (pane.actionPerformed(event) != null) {
                show(((index + 1) >= stackPane.getChildren().size()) ? stackPane.getChildren().size() - 1 : index + 1);
            }
            event.consume();
        });

        final Button finishButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.FINISH);
        finishButton.addEventFilter(ActionEvent.ACTION, event -> {

            int index = getCurrentPane();
            WizardPane pane = (WizardPane) stackPane.getChildren().get(index);
            if (pane.actionPerformed(event) == null) {
                event.consume();
            }
        });

        nextButton.disableProperty().bind(isLastProperty);
        previousButton.disableProperty().bind(isFirstProperty);
        finishButton.disableProperty().bind(isLastProperty.not());
    }

    public void addCard(WizardPane card) {
        stackPane.getChildren().add(card);
    }

    public ObservableList<Node> getCards() {
        return stackPane.getChildren();
    }

    public int getCurrentPane() {
        for (int i = 0; i < stackPane.getChildren().size(); i++) {
            WizardPane pane = (WizardPane) stackPane.getChildren().get(i);
            if (pane.isVisible()) {
                return i;
            }
        }
        return -1;
    }
    
    public void showError(String message) {
        messageProperty.setValue(message);
    }

    public void show(int index) {
        for (int i = 0; i < stackPane.getChildren().size(); i++) {
            WizardPane pane = (WizardPane) stackPane.getChildren().get(i);
            if (index == i) {
                pane.setVisible(true);
                titleProperty.setValue(pane.getTitle());
            } else {
                pane.setVisible(false);
            }
        }

        showError("");
        
        isLastProperty.setValue(index == (stackPane.getChildren().size() - 1));
        isFirstProperty.setValue(index == 0);

        if (!dialog.isShowing()) {
            dialog.show();

            // Optional<ButtonType> result = dialog.showAndWait();
            // if (result.isPresent()) {
            // return result.get().getText();
            // }
        }
    }

}
