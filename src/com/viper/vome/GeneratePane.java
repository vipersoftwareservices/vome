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

import java.io.File;
import java.nio.file.Files;
import java.sql.Types;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import com.viper.vome.dao.Row;
import com.viper.vome.dao.Table;
import com.viper.vome.jfx.UIUtil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

public class GeneratePane extends BorderPane {

    final private Map<String, GeneratorTabPane> generators = new HashMap<String, GeneratorTabPane>();

    private final GeneratePane instance = this;
    private Random masterRandom = new Random();
    private String numberOfRows = "256";
    private Session session = null;

    public GeneratePane(Session session) {
        super();

        this.session = session;

        TabPane tabbedpane = new TabPane();
        tabbedpane.setId("generate.tabpane");

        FlowPane buttonPane = new FlowPane();
        buttonPane.getChildren().add(UIUtil.newButton("Generate", new GenerateAction()));

        FlowPane headerPane = new FlowPane();
        headerPane.getChildren().add(UIUtil.newLabel("Maximum new rows:"));
        headerPane.getChildren().add(UIUtil.newTextField(instance, "NumberOfRows",  0));

        setTop(headerPane);
        setCenter(tabbedpane);
        setBottom(buttonPane);
    }

    public String getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(String numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    class GenerateAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            apply();
        }
    }

    public boolean setColumnNames(Vector<String> columnNames) {
        TabPane tabpane = (TabPane) lookup("#generate.tabpane");
        if (tabpane == null) {
            return false;
        }
        if (columnNames != null) {
            for (String columnName : columnNames) {
                tabpane.getTabs().add(UIUtil.newTab(columnName, getGenerator(columnName)));
            }
        }
        return true;
    }

    private void apply() {
        TabPane tabpane = (TabPane) lookup("#generate.tabpane");

        int startingRow = 0;
        int endingRow = 0;
        try {
            endingRow = Integer.parseInt(numberOfRows);
        } catch (Exception e) {
            UIUtil.showError("Bad value for maximum new rows: " + numberOfRows);
            return;
        }

        Table table = session.getTable();
        for (int rowno = startingRow; rowno < endingRow; rowno++) {
            table.getRows().add(new Row());
            for (Tab tab : tabpane.getTabs()) {
                String columnname = tab.getText();
                GeneratorTabPane pane = (GeneratorTabPane) tab.getContent();
                Object value = pane.nextValue(columnname);
                // adapter.setValueAt(value, rowno, DatabaseMgr.adapter.findColumn(columnname));
            }
        }
    }

    public GeneratorTabPane getGenerator(String columnName) {
        GeneratorTabPane generator = generators.get(columnName);
        if (generator == null) {
            generator = new GeneratorTabPane();
            generators.put(columnName, generator);
        }
        return generator;
    }

    private FlowPane createRow(Label label, Node component) {
        FlowPane pane = new FlowPane();
        pane.getChildren().add(label);
        pane.getChildren().add(component);
        return pane;
    }

    class GeneratorTabPane extends BorderPane {

        TabPane cardPane = new TabPane();
        IncrementGeneratorPane incrementPane = new IncrementGeneratorPane();
        RandomGeneratorPane randomPane = new RandomGeneratorPane();
        RepeatGeneratorPane repeatPane = new RepeatGeneratorPane(null);
        RandomStringGeneratorPane randomStringPane = new RandomStringGeneratorPane();
        RandomListGeneratorPane randomListPane = new RandomListGeneratorPane();
        ComboBox selectionBox = new ComboBox();

        public GeneratorTabPane() {
            super();

            selectionBox.getItems().add(incrementPane.getName());
            selectionBox.getItems().add(randomPane.getName());
            selectionBox.getItems().add(repeatPane.getName());
            selectionBox.getItems().add(randomStringPane.getName());
            selectionBox.getItems().add(randomListPane.getName());

            cardPane.getTabs().add(UIUtil.newTab(incrementPane.getName(), incrementPane));
            cardPane.getTabs().add(UIUtil.newTab(randomPane.getName(), randomPane));
            cardPane.getTabs().add(UIUtil.newTab(repeatPane.getName(), repeatPane));
            cardPane.getTabs().add(UIUtil.newTab(randomStringPane.getName(), randomStringPane));
            cardPane.getTabs().add(UIUtil.newTab(randomListPane.getName(), randomListPane));

            setTop(selectionBox);
            setCenter(cardPane);
        }

        public Object nextValue(String columnName) {
            Object nextValue = null;
            if (incrementPane.getName().equals(selectionBox.getSelectionModel().getSelectedItem())) {
                nextValue = incrementPane.nextValue();
            } else if (randomPane.getName().equals(selectionBox.getSelectionModel().getSelectedItem())) {
                nextValue = randomPane.nextValue();
            } else if (repeatPane.getName().equals(selectionBox.getSelectionModel().getSelectedItem())) {
                nextValue = repeatPane.nextValue(columnName);
            } else if (randomStringPane.getName().equals(selectionBox.getSelectionModel().getSelectedItem())) {
                nextValue = randomStringPane.nextValue();
            } else if (randomListPane.getName().equals(selectionBox.getSelectionModel().getSelectedItem())) {
                nextValue = randomListPane.nextValue();
            }
            System.out.println("Next Value = " + nextValue);
            return nextValue;
        }
    }

    class DataTypePane extends FlowPane {
        boolean isInteger = false;
        boolean isDouble = false;

        public DataTypePane() {
            super();
            ToggleGroup bg = new ToggleGroup();
            getChildren().add(UIUtil.newRadioButton("Integer", this, "isInteger", bg));
            getChildren().add(UIUtil.newRadioButton("Double", this, "isDouble", bg));
        }

        public int getDataType() {
            if (isInteger) {
                return Types.INTEGER;
            } else if (isDouble) {
                return Types.DOUBLE;
            }
            return Types.INTEGER;
        }
    }

    /**
     * 
     * 
     */
    class IncrementGeneratorPane extends GridPane {

        String initialValue = "1";
        String finalValue = "100";
        String incrementValue = "1";

        final DataTypePane dataTypePane = new DataTypePane();

        double currentValue = -1.0;

        public IncrementGeneratorPane() {
            super();

            add(UIUtil.newLabel(getName()), 0, 1);
            add(UIUtil.newLabel("Data Type"), 0, 2);
            add(dataTypePane, 1, 2);
            add(UIUtil.newLabel("Initial Value:"), 0, 3);
            add(UIUtil.newTextField(instance, "initialValue",  0), 1, 3);
            add(UIUtil.newLabel("Final Value:"), 0, 4);
            add(UIUtil.newTextField(instance, "finalValue",  0), 1, 4);
            add(UIUtil.newLabel("Increment Value:"), 0, 5);
            add(UIUtil.newTextField(instance, "incrementValue",   0), 1, 5);
        }

        public String getName() {
            return "Numeric Increment Generator";
        }

        public String getInitialValue() {
            return initialValue;
        }

        public void setInitialValue(String initialValue) {
            this.initialValue = initialValue;
        }

        public String getFinalValue() {
            return finalValue;
        }

        public void setFinalValue(String finalValue) {
            this.finalValue = finalValue;
        }

        public String getIncrementValue() {
            return incrementValue;
        }

        public void setIncrementValue(String incrementValue) {
            this.incrementValue = incrementValue;
        }

        public Object nextValue() {
            double initialValue = Double.valueOf(this.initialValue);
            double finalValue = Double.valueOf(this.finalValue);
            double incrementValue = Double.valueOf(this.incrementValue);
            if (currentValue == -1.0) {
                currentValue = initialValue;
            } else {
                currentValue = currentValue + incrementValue;
            }
            if (currentValue > finalValue) {
                currentValue = initialValue;
            }
            int sqlType = dataTypePane.getDataType();
            switch (sqlType) {
            case Types.INTEGER:
                return (int) currentValue;
            case Types.DOUBLE:
                return currentValue;
            }
            return 0;
        }
    }

    /**
     * 
     * 
     */
    class RandomGeneratorPane extends GridPane {

        String seed = "" + masterRandom.nextInt();
        String minimum = "0";
        String maximum = "100";
        DataTypePane dataTypePane = new DataTypePane();

        Random random = null;

        public RandomGeneratorPane() {
            super();

            add(UIUtil.newLabel(getName()), 0, 1);
            add(UIUtil.newLabel("Data Type"), 0, 2);
            add(dataTypePane, 1, 2);
            add(UIUtil.newLabel("Seed Value:"), 0, 3);
            add(UIUtil.newTextField(instance, "seed",  0), 1, 3);
            add(UIUtil.newLabel("Minimum Value:"), 0, 4);
            add(UIUtil.newTextField(instance, "minimum",  0), 1, 4);
            add(UIUtil.newLabel("Maximum Value:"), 0, 5);
            add(UIUtil.newTextField(instance, "maximum",  0), 1, 5);
        }

        public String getName() {
            return "Random Number Generator";
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }

        public String getMinimum() {
            return minimum;
        }

        public void setMinimum(String minimum) {
            this.minimum = minimum;
        }

        public String getMaximum() {
            return maximum;
        }

        public void setMaximum(String maximum) {
            this.maximum = maximum;
        }

        public Object nextValue() {
            long seedValue = Long.valueOf(seed);
            double minValue = Double.valueOf(minimum);
            double maxValue = Double.valueOf(maximum);
            if (random == null) {
                random = new Random(seedValue);
            }
            double currentValue = random.nextDouble() * (maxValue - minValue) + minValue;

            int sqlType = dataTypePane.getDataType();
            switch (sqlType) {
            case Types.INTEGER:
                return (int) currentValue;
            case Types.DOUBLE:
                return currentValue;
            }
            return 0;
        }
    }

    /**
     * 
     * 
     */
    class RepeatGeneratorPane extends GridPane {

        int currentIndex = -1;
        int startingIndex = -1;
        int endingIndex = -1;

        public RepeatGeneratorPane(TableView table) {
            super();

            int rowCount = (table == null) ? 1 : table.getItems().size();

            Hashtable<Integer, Label> labels = new Hashtable<Integer, Label>();
            int istep = rowCount / 4;
            if (istep <= 0) {
                istep = 1;
            }
            for (int i = 0; i < rowCount; i = i + istep) {
                labels.put(i, UIUtil.newLabel("" + i));
            }
            labels.put(rowCount, UIUtil.newLabel("" + rowCount));

            add(UIUtil.newLabel(getName()), 0, 1);
            add(UIUtil.newLabel("Starting Index:"), 0, 2);
            add(UIUtil.newSlider(this, "startingIndex", 0, rowCount), 1, 2);
            add(UIUtil.newLabel("Ending Index:"), 0, 3);
            add(UIUtil.newSlider(this, "endingIndex", 0, rowCount), 1, 3);
            add(UIUtil.newLabel(""), 0, 4);
            add(UIUtil.newLabel(""), 1, 4);
            add(UIUtil.newLabel(""), 0, 5);
            add(UIUtil.newLabel(""), 1, 5);
        }

        public String getName() {
            return "Repeat Cell Generator";
        }

        public int getStartingIndex() {
            return startingIndex;
        }

        public void setStartingIndex(int startingIndex) {
            this.startingIndex = startingIndex;
        }

        public int getEndingIndex() {
            return endingIndex;
        }

        public void setEndingIndex(int endingIndex) {
            this.endingIndex = endingIndex;
        }

        public int nextIndex() {
            currentIndex++;
            if (currentIndex > endingIndex) {
                currentIndex = startingIndex;
            } else if (currentIndex < startingIndex) {
                currentIndex = startingIndex;
            }
            return currentIndex;
        }

        public Object nextValue(String columnName) {
            // int columnIndex = adapter.findColumn(columnName);
            // return adapter.getValueAt(nextIndex(), columnIndex);
            return null;
        }
    }

    /**
     * 
     * 
     */
    class RandomStringGeneratorPane extends GridPane {

        Random random = new Random();

        String CHARSET = "ABCDEFGHIJKLNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz";
        TextField stringLengthTF = new TextField();

        public RandomStringGeneratorPane() {
            super();

            add(UIUtil.newLabel(getName()), 0, 1);
            add(UIUtil.newLabel("Character Set:"), 0, 2);
            add(UIUtil.newTextField(instance, "CHARSET" , 0), 1, 2);
            add(UIUtil.newLabel("String Length:"), 0, 3);
            add(stringLengthTF, 1, 3);
        }

        public String getName() {
            return "Random String Generator";
        }

        public Object nextValue() {
            int stringLength = Integer.valueOf(stringLengthTF.getText());
            StringBuffer buf = new StringBuffer(stringLength);
            for (int i = 0; i < stringLength; i++) {
                int index = random.nextInt(CHARSET.length());
                char c = CHARSET.charAt(index);
                buf.append(c);
            }
            return buf.toString();
        }
    }

    class RandomListGeneratorPane extends GridPane implements EventHandler<ActionEvent> {

        final int visibleRowCount = 8;

        private String filename;
        private String stringlength;

        Random random = new Random();

        public RandomListGeneratorPane() {
            super();

            add(UIUtil.newLabel(getName()), 0, 1);
            add(UIUtil.newLabel("Filename:"), 0, 2);
            add(UIUtil.newTextField(instance, "filename",   0), 1, 2);
            add(UIUtil.newButton("Browse...", this), 2, 2);
            add(UIUtil.newLabel("String Length:"), 0, 3);
            add(UIUtil.newTextField(instance, "stringlength",   0), 1, 3);
            // add(UIUtil.newScrollListView(), 0, 4);
        }

        public String getName() {
            return "Random List Generator";
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getStringlength() {
            return stringlength;
        }

        public void setStringlength(String stringlength) {
            this.stringlength = stringlength;
        }

        @Override
        public void handle(ActionEvent e) {

            FileChooser chooser = new FileChooser();
            while (true) {
                File file = chooser.showOpenDialog(session.getStage());
                if (file == null) {
                    break;
                }
                String filename = file.getPath();
                if (file.exists() == false) {
                    UIUtil.showError("File " + filename + " does not exist.");
                    continue;
                }
                if (file.canRead() == false) {
                    UIUtil.showError("Read access to file " + filename + " is denied.");
                    continue;
                }
                // filenameTF.setText(filename);
                //
                // setList(scrollingList, file);
                break;
            }
        }

        private void setList(ListView list, File file) throws Exception {
            Vector<String> v = new Vector<String>();
            String buffer = new String(Files.readAllBytes(file.toPath()));
            StringTokenizer tokens = new StringTokenizer(buffer, "\n");
            while (tokens.hasMoreElements()) {
                String token = tokens.nextToken();
                v.add(token);
            }
            list.getItems().addAll(v);
        }

        public Object nextValue() {
            return null;
        }
    }
}
