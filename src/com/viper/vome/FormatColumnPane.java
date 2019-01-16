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

import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import com.viper.vome.jfx.UIUtil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FormatColumnPane extends BorderPane {
    public enum DateStyle {
        FULL(0, "FULL"), LONG(1, "LONG"), MEDIUM(2, "MEDIUM"), SHORT(3, "SHORT"), DEFAULT(2, "DEFAULT");

        private final int value;
        private final String label;

        DateStyle(int v, String l) {
            value = v;
            label = l;
        }

        public int value() {
            return value;
        }

        public String toString() {
            return label;
        }
    }

    protected String newChoice = "";
    protected List<String> choiceList = new ArrayList<String>();

    protected Locale locale = Locale.getDefault();
    protected String format = "Number";
    protected String preview = "ABCDEFGHIJKLMOPQRSTUVWXYZabcdefghijklmnopqrsuvwxyz0123456789";
    protected Session session = null;
    protected FormatColumnPane formatColumnPane = this;

    public FormatColumnPane(Session session) {
        super();

        this.session = session;

        List<String> cardnames = new ArrayList<String>();

        VBox rightPane = new VBox();
        rightPane.getChildren().add(UIUtil.newLabel("Text:"));
        rightPane.getChildren().add(UIUtil.newTextField(formatColumnPane, "preview", 0));
        rightPane.getChildren().add(UIUtil.newLabel("Preview:"));
        rightPane.getChildren().add(UIUtil.newLabel(preview));
        rightPane.getChildren().add(UIUtil.newButton("Preview", new PreviewAction()));

        ScrollPane formatPane1 = UIUtil.newScrollListView(formatColumnPane, "Format", cardnames);
        ComboBox localePane1 = UIUtil.newComboBox(formatColumnPane, "Locale", Locale.getAvailableLocales());

        HBox leftPane = new HBox();
        leftPane.getChildren().add(UIUtil.newBorderedTitlePane("Format:", formatPane1));
        leftPane.getChildren().add(UIUtil.newBorderedTitlePane("Language:", localePane1));

        final TabPane detailPane = new TabPane();
        detailPane.getTabs().add(UIUtil.newTab("Format", leftPane));
        detailPane.getTabs().add(UIUtil.newTab("Currency", new CurrencyDetailsPane()));
        detailPane.getTabs().add(UIUtil.newTab("Number", new NumberDetailsPane()));
        detailPane.getTabs().add(UIUtil.newTab("Percent", new PercentDetailsPane()));
        detailPane.getTabs().add(UIUtil.newTab("Date", new DateDetailsPane()));
        detailPane.getTabs().add(UIUtil.newTab("Time", new TimeDetailsPane()));
        detailPane.getTabs().add(UIUtil.newTab("Timestamp", new DateTimeDetailsPane()));
        detailPane.getTabs().add(UIUtil.newTab("Scientific", new DecimalDetailsPane()));
        detailPane.getTabs().add(UIUtil.newTab("Fraction", new FractionDetailsPane()));
        detailPane.getTabs().add(UIUtil.newTab("Logical", new LogicalDetailsPane()));
        detailPane.getTabs().add(UIUtil.newTab("Textual", new TextualDetailsPane()));
        detailPane.getTabs().add(UIUtil.newTab("Enumeration", new ChoiceDetailsPane()));

        setTop(UIUtil.newBorderedTitlePane("Sample:", rightPane));
        setCenter(UIUtil.newBorderedTitlePane("Details:", detailPane));
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getNewChoice() {
        return newChoice;
    }

    public void setNewChoice(String newChoice) {
        this.newChoice = newChoice;
    }

    interface FormatInterface {
        public String format(String sample);
    }

    // -------------------------------------------------------------------------

    class TextualDetailsPane extends VBox implements FormatInterface {
        public TextualDetailsPane() {
            super();
        }

        public String format(String sample) {
            return sample;
        }
    }

    // -------------------------------------------------------------------------

    class FractionDetailsPane extends GridPane {
        NumberFormat formatter = NumberFormat.getNumberInstance();

        public FractionDetailsPane() {
            super();

            formatter.setParseIntegerOnly(true);
            formatter.setGroupingUsed(false);
            formatter.setRoundingMode(RoundingMode.UP);

            getStyleClass().add("grid");
            add(UIUtil.newLabel("Integer Only"), 0, 1);
            add(UIUtil.newCheckBox(formatter, "ParseIntegerOnly"), 1, 1);
            add(UIUtil.newLabel("Grouping"), 0, 2);
            add(UIUtil.newCheckBox(formatter, "GroupingUsed"), 1, 2);
            add(UIUtil.newLabel("Rounding Mode:"), 0, 3);
            add(UIUtil.newComboBox(formatter, "RoundingMode", RoundingMode.values()), 1, 3);
            add(UIUtil.newLabel("Min Fraction Digits:"), 0, 4);
            add(UIUtil.newSlider(formatter, "MinimumFractionDigits", 0, 20), 1, 4);
            add(UIUtil.newLabel("Max Fraction Digits:"), 0, 5);
            add(UIUtil.newSlider(formatter, "MaximumFractionDigits", 0, 20), 1, 5);
        }
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    class LogicalDetailsPane extends VBox implements FormatInterface {

        public String format(String sample) {
            try {
                double limits[] = { 0.0, 1.0 };
                String formats[] = new String[] { "TRUE", "FALSE" };
                Format formatter = new ChoiceFormat(limits, formats);
                return formatter.format(Double.valueOf(sample));
            } catch (IllegalArgumentException iae) {
                UIUtil.showException("Not a logical string " + sample + ", enter numeric.", iae);
            }
            return sample;
        }
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    public class DateTimeDetailsPane extends GridPane implements FormatInterface {

        public DateStyle dateStyle = DateStyle.DEFAULT;
        public DateStyle timeStyle = DateStyle.DEFAULT;

        public DateTimeDetailsPane() {
            super();

            getStyleClass().add("grid");
            add(UIUtil.newLabel("(yyyy-mm-dd hh:mm:ss[.f...])"), 0, 1, 2, 1);
            add(UIUtil.newLabel("Date Style:"), 0, 2);
            add(UIUtil.newComboBox(this, "DateStyle", DateStyle.values()), 1, 2);
            add(UIUtil.newLabel("Time Style:"), 0, 3);
            add(UIUtil.newComboBox(this, "TimeStyle", DateStyle.values()), 1, 3);
        }

        public DateStyle getDateStyle() {
            return dateStyle;
        }

        public void setDateStyle(DateStyle dateStyle) {
            this.dateStyle = dateStyle;
        }

        public DateStyle getTimeStyle() {
            return timeStyle;
        }

        public void setTimeStyle(DateStyle timeStyle) {
            this.timeStyle = timeStyle;
        }

        public String format(String sample) {
            try {
                DateFormat formatter = DateFormat.getDateTimeInstance(dateStyle.value(), timeStyle.value());
                return formatter.format(Timestamp.valueOf(sample));
            } catch (IllegalArgumentException iae) {
                UIUtil.showException("Not a timestamp string " + sample + ", enter timestamp.", iae);
            }
            return sample;
        }
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    public class TimeDetailsPane extends VBox implements FormatInterface {

        public DateStyle timeStyle = DateStyle.DEFAULT;

        public TimeDetailsPane() {
            super();

            getChildren().add(UIUtil.newLabel("(hh:mm:ss[.f...])"));
            getChildren().add(UIUtil.newLabel("Time Style:"));
            getChildren().add(UIUtil.newComboBox(this, "TimeStyle", DateStyle.values()));
            getChildren().add(UIUtil.newLabel(""));
        }

        public DateStyle getTimeStyle() {
            return timeStyle;
        }

        public void setTimeStyle(DateStyle timeStyle) {
            this.timeStyle = timeStyle;
        }

        public String format(String sample) {
            try {
                DateFormat formatter = DateFormat.getTimeInstance(timeStyle.value());
                return formatter.format(Time.valueOf(sample));
            } catch (IllegalArgumentException iae) {
                UIUtil.showException("Not a time string " + sample + ", enter time.", iae);
            }
            return sample;
        }
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    public class DateDetailsPane extends VBox implements FormatInterface {
        public DateStyle dateStyle = DateStyle.DEFAULT;

        public DateDetailsPane() {
            super();

            getChildren().add(UIUtil.newLabel("(yyyy-mm-dd)"));
            getChildren().add(UIUtil.newLabel("Date Style:"));
            getChildren().add(UIUtil.newComboBox(this, "DateStyle", DateStyle.values()));
            getChildren().add(UIUtil.newLabel(""));
        }

        public DateStyle getDateStyle() {
            return dateStyle;
        }

        public void setDateStyle(DateStyle dateStyle) {
            this.dateStyle = dateStyle;
        }

        public String format(String sample) {
            try {
                Format formatter = DateFormat.getDateInstance(dateStyle.value());
                return formatter.format(Date.valueOf(sample));
            } catch (IllegalArgumentException iae) {
                UIUtil.showException("Not a sate string " + sample + ", enter date.", iae);
            }
            return sample;
        }
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    class DecimalDetailsPane extends VBox implements FormatInterface {
        NumberFormat formatter = new DecimalFormat();

        public DecimalDetailsPane() {
            super();

            formatter.setParseIntegerOnly(true);
            formatter.setGroupingUsed(false);

            getChildren().add(UIUtil.newLabel("Minimum Integer Digits:"));
            getChildren().add(UIUtil.newSlider(formatter, "MinimumIntegerDigits", 0, 20));
            getChildren().add(UIUtil.newLabel("Maximum Integer Digits:"));
            getChildren().add(UIUtil.newSlider(formatter, "MaximumIntegerDigits", 0, 20));
            getChildren().add(UIUtil.newLabel("Minimum Fraction Digits:"));
            getChildren().add(UIUtil.newSlider(formatter, "MinimumFractionDigits", 0, 20));
            getChildren().add(UIUtil.newLabel("Maximum Fraction Digits:"));
            getChildren().add(UIUtil.newSlider(formatter, "MaximumFractionDigits", 0, 20));
        }

        public String format(String sample) {
            try {
                return formatter.format(Double.valueOf(sample));
            } catch (IllegalArgumentException iae) {
                UIUtil.showException("Not a float string " + sample + ", enter float value.", iae);
            }
            return sample;
        }
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    class PercentDetailsPane extends VBox implements FormatInterface {
        NumberFormat formatter = NumberFormat.getPercentInstance();

        public PercentDetailsPane() {
            super();

            formatter.setParseIntegerOnly(true);
            formatter.setGroupingUsed(false);

            getChildren().add(UIUtil.newLabel("Minimum Integer Digits:"));
            getChildren().add(UIUtil.newSlider(formatter, "MinimumIntegerDigits", 0, 20));
            getChildren().add(UIUtil.newLabel("Maximum Integer Digits:"));
            getChildren().add(UIUtil.newSlider(formatter, "MaximumIntegerDigits", 0, 20));
        }

        public String format(String sample) {
            try {
                return formatter.format(Double.valueOf(sample));
            } catch (IllegalArgumentException iae) {
                UIUtil.showException("Not a float string " + sample + ", enter float value.", iae);
            }
            return sample;
        }
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    class CurrencyDetailsPane extends GridPane implements FormatInterface {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        public CurrencyDetailsPane() {
            super();

            formatter.setParseIntegerOnly(true);
            formatter.setGroupingUsed(false);
            formatter.setRoundingMode(RoundingMode.UP);

            add(UIUtil.newLabel("Currency"), 0, 1);
            add(UIUtil.newComboBox(formatter, "Currency", listCurrencies()), 1, 1);
            add(UIUtil.newLabel("Integer Only: "), 0, 2);
            add(UIUtil.newCheckBox(formatter, "ParseIntegerOnly"), 1, 2);
            add(UIUtil.newLabel("Grouping: "), 0, 3);
            add(UIUtil.newCheckBox(formatter, "GroupingUsed"), 1, 3);
            add(UIUtil.newLabel("Rounding Mode:"), 0, 4);
            add(UIUtil.newComboBox(formatter, "RoundingMode", RoundingMode.values()), 1, 4);
            add(UIUtil.newLabel("Min Fraction Digits:"), 0, 5);
            add(UIUtil.newSlider(formatter, "MinimumFractionDigits", 0, 20), 1, 5);
            add(UIUtil.newLabel("Max Fraction Digits:"), 0, 6);
            add(UIUtil.newSlider(formatter, "MaximumFractionDigits", 0, 20), 1, 6);
        }

        public String format(String sample) {
            try {
                return formatter.format(Double.valueOf(sample));
            } catch (IllegalArgumentException iae) {
                UIUtil.showException("Not a float string " + sample + ", enter float value.", iae);
            }
            return sample;
        }

        private List<String> listCurrencies() {
            List<String> list = new ArrayList<String>();
            for (Locale l : Locale.getAvailableLocales()) {
                if (null == l.getCountry() || l.getCountry().equals("")) {
                    continue;
                }
                list.add(Currency.getInstance(l).getCurrencyCode());
            }
            return list;
        }
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    class NumberDetailsPane extends GridPane {
        NumberFormat formatter = NumberFormat.getNumberInstance();

        public NumberDetailsPane() {
            super();

            formatter.setParseIntegerOnly(true);
            formatter.setGroupingUsed(false);

            add(UIUtil.newLabel("Minimum Integer Digits:"), 0, 1);
            add(UIUtil.newSlider(formatter, "MinimumIntegerDigits", 0, 20), 1, 1);
            add(UIUtil.newLabel("Maximum Integer Digits:"), 0, 2);
            add(UIUtil.newSlider(formatter, "MaximumIntegerDigits", 0, 20), 1, 2);
        }
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    class ChoiceDetailsPane extends VBox implements FormatInterface {

        public ChoiceDetailsPane() {
            super();

            HBox buttonPane = new HBox();
            buttonPane.getChildren().add(UIUtil.newButton("+", new AddAction()));
            buttonPane.getChildren().add(UIUtil.newButton("-", new DeleteAction()));

            getChildren().add(UIUtil.newLabel("Choices"));
            getChildren().add(UIUtil.newScrollListView(formatColumnPane, "newChoice", choiceList));
            getChildren().add(buttonPane);
        }

        public double[] getLimits(int length) {
            double limits[] = new double[length];
            for (int i = 0; i < length; i++) {
                limits[i] = i + 1;
            }
            return limits;
        }

        public String[] getFormats(int length) {
            String formats[] = new String[length];
            for (int i = 0; i < length; i++) {
                // TODO formats[i] = (String) model.getElementAt(i);
            }
            return formats;
        }

        public String format(String sample) {
            try {
                double limits[] = getLimits(10);
                String formats[] = getFormats(10);
                ChoiceFormat formatter = new ChoiceFormat(limits, formats);
                return formatter.format(Double.valueOf(sample));
            } catch (IllegalArgumentException iae) {
                UIUtil.showException("Not a choice string " + sample + ", enter numeric.", iae);
            }
            return sample;
        }
    }

    class AddAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            choiceList.add(newChoice);
        }
    }

    class DeleteAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            choiceList.remove(newChoice);
        }
    }

    class PreviewAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            applyFormat();
        }
    }

    private void applyFormat() {
        // TODO
    }
}
