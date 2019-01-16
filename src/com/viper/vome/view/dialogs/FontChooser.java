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

package com.viper.vome.view.dialogs;

import java.awt.GraphicsEnvironment;
import java.util.Arrays;

import com.viper.vome.Dialogs;
import com.viper.vome.Session;
import com.viper.vome.jfx.UIUtil;

import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FontChooser {

    private String fontName = null;
    private  FontWeight fontWeight = null;
    private double fontSize = 0.0;

    private static final Double[] SIZES = { 9.0, 10.0, 11.0, 12.0, 14.0, 16.0, 18.0, 20.0, 22.0, 24.0, 28.0, 36.0, 48.0, 72.0 };
    private static final FontWeight[] WEIGHTS = { FontWeight.BLACK, FontWeight.BOLD, FontWeight.EXTRA_BOLD,
            FontWeight.EXTRA_LIGHT, FontWeight.LIGHT, FontWeight.MEDIUM, FontWeight.NORMAL, FontWeight.SEMI_BOLD, FontWeight.THIN };

    public FontChooser() {
        super();
    }

    /**
     * Standard constructor - builds a FontChooserPanel initialized with the specified font.
     * 
     * @param font
     *            the initial font to display.
     */
    public static Font show(Session session, Font initialFont) {

        FontChooser chooser = new FontChooser();
        
        final GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final String[] fonts = g.getAvailableFontFamilyNames();

        Pane fontPane = UIUtil.newBorderedTitlePane("Font", UIUtil.newScrollListView(chooser, "fontName", Arrays.asList(fonts)));
        Pane sizePane = UIUtil.newBorderedTitlePane("Size", UIUtil.newScrollListView(chooser, "fontSize", Arrays.asList(SIZES)));
        Pane weightPane = UIUtil.newBorderedTitlePane("Weight", UIUtil.newScrollListView(chooser, "fontWeight", Arrays.asList(WEIGHTS)));

        HBox centerPane = new HBox();
        centerPane.getChildren().add(fontPane);
        centerPane.getChildren().add(sizePane);
        centerPane.getChildren().add(weightPane);

        ButtonType result = Dialogs.showAsDialog( "Font Chooser", centerPane, null, ButtonType.OK, ButtonType.CANCEL);
        if (result == ButtonType.CANCEL) {
            return null;
        }
        return chooser.getSelectedFont();
    }

    /**
     * Returns a Font object representing the selection in the panel.
     * 
     * @return the font.
     */
    public Font getSelectedFont() {
        return Font.font(getFontName(), getFontWeight(), getFontSize());
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public FontWeight getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(FontWeight fontWeight) {
        this.fontWeight = fontWeight;
    }

    public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }    
}
