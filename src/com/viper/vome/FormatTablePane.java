/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2003/06/15
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

package com.viper.vome;

import com.viper.vome.jfx.UIUtil;
import com.viper.vome.view.dialogs.FontChooser;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class FormatTablePane extends VBox {

    public FormatTablePane() {
        super();

        Session session = Session.getInstance();

        setSpacing(10.0);
        getStyleClass().add("vbox");

        TilePane pane1 = new TilePane(2, 2);
        pane1.setPrefColumns(2);
        pane1.getStyleClass().add("grid");
        pane1.getChildren().add(UIUtil.newLabel("ShowHorizontalLines", Pos.BASELINE_RIGHT));
        pane1.getChildren().add(UIUtil.newCheckBox("", session, "showHorizontalLines"));
        pane1.getChildren().add(UIUtil.newLabel("ShowVerticalLines", Pos.BASELINE_RIGHT));
        pane1.getChildren().add(UIUtil.newCheckBox("", session, "showVerticalLines"));
        pane1.getChildren().add(UIUtil.newLabel("Inter Cell Spacing", Pos.BASELINE_RIGHT));
        pane1.getChildren().add(UIUtil.newSlider(session, "interCellSpacing", 0, 100));
        pane1.getChildren().add(UIUtil.newLabel("Row Height", Pos.BASELINE_RIGHT));
        pane1.getChildren().add(UIUtil.newSlider(session, "rowHeight", 0, 100));
        pane1.getChildren().add(UIUtil.newLabel("Automatic Resize Mode", Pos.BASELINE_RIGHT));
        pane1.getChildren().add(UIUtil.newComboBox(session, "autoResizeMode", Session.RESIZE_MODES));
        pane1.getChildren().add(UIUtil.newLabel("Banding Interval", Pos.BASELINE_RIGHT));
        pane1.getChildren().add(UIUtil.newSlider(session, "bandingInterval", 0, 100));
        pane1.getChildren().add(UIUtil.newLabel("Banding Color", Pos.BASELINE_RIGHT));
        pane1.getChildren().add(UIUtil.newColorPicker(session, "BandingColor"));
        pane1.getChildren().add(UIUtil.newLabel("Non Banding Color", Pos.BASELINE_RIGHT));
        pane1.getChildren().add(UIUtil.newColorPicker(session, "NonBandingColor"));
        pane1.getChildren().add(UIUtil.newLabel("Font", Pos.BASELINE_RIGHT));
        pane1.getChildren().add(UIUtil.newButton("Font", this::FontAction));
        pane1.getChildren().add(UIUtil.newLabel("Font Color", Pos.BASELINE_RIGHT));
        pane1.getChildren().add(UIUtil.newColorPicker(session, "FontColor"));
        pane1.getChildren().add(UIUtil.newLabel("aAbBcCdDeEfF", Pos.BASELINE_RIGHT));
        pane1.getChildren().add(UIUtil.newButton("Preview", this::PreviewAction));

        getChildren().add(pane1);
    }

    public void FontAction(ActionEvent e) {

        Session session = Session.getInstance();
        Font newFont = FontChooser.show(session, session.getTableFont());
        if (newFont != null) {
            session.setTableFont(newFont);
        }
        // TODO UIManager.put(propertyName, newFont);
    }

    public void PreviewAction(ActionEvent e) {
        // Label label = (Label) e.getSource();
        // label.setFont((Font) e.getNewValue());
    }
}
