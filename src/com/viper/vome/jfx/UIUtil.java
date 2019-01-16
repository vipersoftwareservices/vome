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

package com.viper.vome.jfx;

import static java.util.Locale.ENGLISH;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.controlsfx.control.CheckComboBox;

import com.viper.vome.Dialogs;
import com.viper.vome.LocaleUtil;
import com.viper.vome.dao.Column;
import com.viper.vome.dao.Row;
import com.viper.vome.dao.Table;
import com.viper.vome.skins.CustomCheckComboBoxSkin;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanLongPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * @author Tom Nevin
 */

public class UIUtil {

    public static List<String> toPropertyNames(String filename) {
        try {
            if (filename != null) {
                Properties properties = new Properties();
                if (filename.endsWith(".xml")) {
                    properties.loadFromXML(new FileInputStream(filename));
                } else {
                    properties.load(new FileInputStream(filename));
                }
                List<String> list = new ArrayList<String>();
                for (String name : properties.stringPropertyNames()) {
                    list.add(name);
                }
                return list;
            }
        } catch (FileNotFoundException e) {

        } catch (Exception e) {
            System.err.println("ERROR: getPropertiesFromFile: " + filename + ", " + e);
        }
        return new ArrayList<String>();
    }

    public static void toFront(StackPane stack, String id) {
        Pane pane = (Pane) stack.lookup("#" + id);
        assertNotNull(pane, "UI Item not found: " + id);
        for (Node child : stack.getChildren()) {
            child.setVisible(false);
        }
        pane.setVisible(true);
        pane.toFront();
    }

    public static StackPane newBorderedTitlePane(String titleString, Node content) {

        Label title = new Label(" " + titleString + " ");
        title.getStyleClass().add("bordered-titled-title");
        StackPane.setAlignment(title, Pos.TOP_CENTER);

        StackPane contentPane = new StackPane();
        content.getStyleClass().add("bordered-titled-content");
        contentPane.getChildren().add(content);

        StackPane borderPane = new StackPane();
        borderPane.getStyleClass().add("bordered-titled-border");
        borderPane.getChildren().addAll(title, contentPane);

        return borderPane;
    }

    // ------------------------------------------------------------------------------------------
    public static Menu newMenu(String title) {
        Menu item = new Menu(title);
        item.setId(title);
        return item;
    }

    public static MenuItem newMenuItem(String title, EventHandler<ActionEvent> action) {
        MenuItem item = new MenuItem(title);
        item.setId(title);
        item.setAccelerator(KeyCombination.keyCombination(title.substring(0, 1)));
        item.setOnAction(action);
        return item;
    }

    public static CheckMenuItem newCheckMenuItem(String title, EventHandler<ActionEvent> action) {
        CheckMenuItem item = new CheckMenuItem(title);
        item.setId(title);
        item.setOnAction(action);
        return item;
    }

    // ------------------------------------------------------------------------------------------

    public static Button newButton(String title, EventHandler<ActionEvent> action) {
        Button item = new Button(title);
        if (action != null) {
            item.setOnAction(action);
        }
        return item;
    }

    public static Button newButton(String key, Node icon, Object userData, EventHandler<ActionEvent> action) {
        Button item = new Button();

        String title = LocaleUtil.getProperty(key + ".label", key);
        String tooltip = LocaleUtil.getProperty(key + ".tooltip", key);
        if (title != null) {
            item.setText(title);
        }
        if (tooltip != null) {
            item.setTooltip(new Tooltip(tooltip));
        }
        if (action != null) {
            item.setOnAction(action);
        }
        if (userData != null) {
            item.setUserData(userData);
        }

        return item;
    }

    public static Button newWideButton(String title, EventHandler<ActionEvent> action) {
        Button item = new Button(title);
        item.setId(title);
        item.setMaxWidth(Double.MAX_VALUE);
        item.setOnAction(action);
        return item;
    }

    // -------------------------------------------------------------------------

    public static Button newInfoButton(String resource) {
        Button infoButton = newButton("Info", null);

        String styleClass = "table-filter";
        if (!infoButton.getStyleClass().contains(styleClass)) {
            infoButton.getStyleClass().add(styleClass);
        }

        Bounds boundsInScene = infoButton.localToScene(infoButton.getBoundsInLocal());

        Bounds boundsForPane = new BoundingBox(boundsInScene.getMaxX(), boundsInScene.getMaxY(), 200, 300);

        infoButton.setOnAction(new InfoButtonAction(resource, boundsForPane));
        return infoButton;
    }

    // ------------------------------------------------------------------------------------------

    public static Label newLabel(String title) {
        return newLabel(title, Pos.BASELINE_LEFT);
    }

    public static Label newLabel(String key, Pos alignment) {

        String title = LocaleUtil.getProperty(key + ".label", key);
        String tooltip = LocaleUtil.getProperty(key + ".tooltip", key);

        Label item = new Label();
        item.setText(title);
        item.setWrapText(true);
        item.setAlignment(alignment);
        item.setTextOverrun(OverrunStyle.ELLIPSIS);
        if (tooltip != null) {
            item.setTooltip(new Tooltip(tooltip));
        }
        return item;
    }

    public static Label newLabel(String key, TextAlignment alignment) {

        String title = LocaleUtil.getProperty(key + ".label", key);
        String tooltip = LocaleUtil.getProperty(key + ".tooltip", key);

        Label item = new Label();
        item.setText(title);
        item.setWrapText(true);
        item.setTextAlignment(alignment);
        item.setTextOverrun(OverrunStyle.ELLIPSIS);
        if (tooltip != null) {
            item.setTooltip(new Tooltip(tooltip));
        }
        return item;
    }

    public static Label newLabel(StringProperty property) {
        Label item = new Label();
        item.setWrapText(true);
        item.textProperty().bind(property);
        return item;
    }

    public static <T> Label newLabel(T bean, String propertyName) {
        Label item = new Label();
        item.setWrapText(true);
        item.textProperty().bind(buildStringProperty(bean, propertyName));
        return item;
    }

    // ------------------------------------------------------------------------------------------

    public static ProgressBar newProgressBar(String id) {
        ProgressBar item = new ProgressBar();
        item.setId(id);
        return item;
    }

    // ------------------------------------------------------------------------------------------

    public static <T> PasswordField newPasswordField(T bean, String propertyName, String promptText) {
        StringProperty property = buildStringProperty(bean, propertyName);

        PasswordField item = new PasswordField();
        item.setId(property.getName());
        if (promptText != null) {
            item.setPromptText(promptText);
        }
        // item.textProperty().bindBidirectional(property);
        item.setText(property.getValue());
        item.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                System.out.println("Value changed: " + t1);
                property.setValue(t1);
            }
        });
        return item;
    }

    // ------------------------------------------------------------------------------------------

    public static TextField newTextField(String key, String value, int preferredColumnCount, EventHandler<ActionEvent> action) {

        String title = LocaleUtil.getProperty(key + ".label", key);
        String tooltip = LocaleUtil.getProperty(key + ".tooltip", key);
        String prompt = LocaleUtil.getProperty(key + ".prompt", key);

        TextField item = new TextField();
        item.setId(key);

        if (prompt != null) {
            item.setPromptText(prompt);
        }
        if (preferredColumnCount != 0) {
            item.setPrefColumnCount(preferredColumnCount);
        }
        if (value != null) {
            item.setText(value);
        }
        if (action != null) {
            item.setOnAction(action);
        }
        item.setVisible(true); // TODO make false?
        return item;
    }

    public static <T> TextField newTextField(T bean, String propertyName, int preferredColumnCount) {
        StringProperty property = buildStringProperty(bean, propertyName);

        String title = LocaleUtil.getProperty(propertyName + ".label", propertyName);
        String tooltip = LocaleUtil.getProperty(propertyName + ".tooltip", propertyName);
        String prompt = LocaleUtil.getProperty(propertyName + ".prompt", propertyName);

        TextField item = new TextField();
        if (prompt != null) {
            item.setPromptText(prompt);
        }
        if (preferredColumnCount != 0) {
            item.setPrefColumnCount(preferredColumnCount);
        }
        Bindings.bindBidirectional(item.textProperty(), property);
        return item;
    }

    // ------------------------------------------------------------------------------------------

    public static <T> TextArea newTextArea(T bean, String propertyName, int ncols, int nrows) {

        String title = LocaleUtil.getProperty(propertyName + ".label", propertyName);
        String tooltip = LocaleUtil.getProperty(propertyName + ".tooltip", propertyName);
        String prompt = LocaleUtil.getProperty(propertyName + ".prompt", propertyName);

        StringProperty property = buildStringProperty(bean, propertyName);

        TextArea item = new TextArea();
        item.setPrefColumnCount(ncols);
        item.setPrefRowCount(nrows);
        item.setEditable(true);
        Bindings.bindBidirectional(item.textProperty(), property);
        return item;
    }

    public static TextArea newTextArea(String key, int ncols, int nrows, String str) {

        String title = LocaleUtil.getProperty(key + ".label", key);
        String tooltip = LocaleUtil.getProperty(key + ".tooltip", key);
        String prompt = LocaleUtil.getProperty(key + ".prompt", key);

        TextArea item = new TextArea();
        item.setId(key);
        item.setPrefColumnCount(ncols);
        item.setPrefRowCount(nrows);
        item.setEditable(true);
        if (str != null) {
            item.setText(str);
        }
        return item;
    }

    public static TextArea newTextArea(String str) {
        TextArea item = new TextArea();
        item.setText(str);
        return item;
    }

    // ------------------------------------------------------------------------------------------

    public static <T> HTMLEditor newHTMLEditor(String id, String text) {
        HTMLEditor item = new HTMLEditor();
        item.setId(id);
        item.setHtmlText(text);
        return item;
    }

    // ------------------------------------------------------------------------------------------

    public static ImageView newImageView(InputStream imageStream) {
        Image image = new Image(imageStream);

        ImageView view = new ImageView(image);
        view.setFitHeight(image.getHeight());
        view.setFitWidth(image.getWidth());
        view.setPreserveRatio(false);
        view.setSmooth(true);
        view.setCache(true);
        return view;
    }

    public static ImageView newImageView(Image image) {

        ImageView view = new ImageView(image);
        view.setFitHeight(image.getHeight());
        view.setFitWidth(image.getWidth());
        view.setPreserveRatio(false);
        view.setSmooth(true);
        view.setCache(true);
        return view;
    }

    public static ImageView newImageView(String filename, int width, int height) {
        Image image = new Image(UIUtil.class.getResourceAsStream(filename));

        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        return imageView;
    }

    // ------------------------------------------------------------------------------------------

    public static <T> CheckComboBox newCheckComboBox(Collection<T> beans, String propertyName,
            ListChangeListener<String> listener) {

        List choices = UIUtil.asUniqueSortedList(beans, propertyName);

        final CheckComboBox item = new CheckComboBox();
        item.setMaxWidth(Double.MAX_VALUE);
        item.getItems().add("Select All");
        item.getItems().add("Select None");
        if (choices != null) {
            item.getItems().addAll(FXCollections.observableArrayList(choices));
            item.getCheckModel().checkAll();
        }

        item.setSkin(new CustomCheckComboBoxSkin(item, "#Selections"));

        // and listen to the relevant events (e.g. when the selected indices or
        // selected items change).
        if (listener != null) {
            item.getCheckModel().getCheckedItems().addListener(listener);
        }

        return item;
    }

    // ------------------------------------------------------------------------------------------

    public static <T, S> ComboBox<S> newComboBoxBeans(T bean, String propertyName, Collection<S> beans,
            EventHandler<ActionEvent> action) {
        List choices = UIUtil.asUniqueSortedList(beans, propertyName);
        return newComboBox(bean, propertyName, choices, action);
    }

    public static <T, S> ComboBox<S> newComboBox(T bean, String propertyName, Collection<S> choices) {
        return newComboBox(bean, propertyName, choices, null);
    }

    public static <T, S> ComboBox<S> newComboBox(T bean, String propertyName, S choices[]) {
        return newComboBox(bean, propertyName, Arrays.asList(choices));
    }

    public static <T, S> ComboBox<S> newComboBox(T bean, String propertyName, Collection<S> choices,
            EventHandler<ActionEvent> action) {

        String title = LocaleUtil.getProperty(propertyName + ".label", propertyName);
        String tooltip = LocaleUtil.getProperty(propertyName + ".tooltip", propertyName);
        String prompt = LocaleUtil.getProperty(propertyName + ".prompt", propertyName);

        Property<S> property = buildObjectProperty(bean, propertyName);

        final ComboBox<S> item = new ComboBox<S>();
        item.setEditable(true);
        if (choices != null) {
            item.getItems().addAll(FXCollections.observableArrayList(choices));
        }
        if (tooltip != null) {
            item.setTooltip(new Tooltip(tooltip));
        }
        item.setValue(property.getValue());
        item.valueProperty().addListener(new ChangeListener<S>() {
            @Override
            public void changed(ObservableValue<? extends S> ov, S t, S t1) {
                property.setValue(t1);
            }
        });
        // Bindings.bindBidirectional(item.valueProperty(), property);
        item.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            item.setValue((S) newText);
        });
        return item;
    }

    // ------------------------------------------------------------------------------------------

    public static <T> ColorPicker newColorPicker(T bean, String propertyName) {
        return newColorPicker(buildObjectProperty(bean, propertyName));
    }

    public static ColorPicker newColorPicker(Property<Color> property) {
        final ColorPicker item = new ColorPicker();
        item.setEditable(true);
        Bindings.bindBidirectional(item.valueProperty(), property);
        return item;
    }

    // ------------------------------------------------------------------------------------------

    public static <T, S> ListView<T> newListView(S bean, String propertyName, Collection<T> choices) {
        return newListView(buildObjectProperty(bean, propertyName), choices);
    }

    public static <T> ListView<T> newListView(Property<T> bean, Collection<T> choices) {
        final ListView<T> item = new ListView<T>();
        if (choices != null) {
            item.getItems().addAll(choices);
        }
        item.setEditable(true);
        item.getSelectionModel().selectedItemProperty().addListener(new FieldPathValueListener(bean));
        return item;
    }

    public static <T> ListView<T> newListView(Collection<T> choices) {
        final ListView<T> item = new ListView<T>();
        item.getItems().addAll(choices);
        item.setEditable(true);
        return item;
    }

    // ------------------------------------------------------------------------------------------

    public static <T> ScrollPane newScrollListView(Property<T> bean, Collection<T> choices) {
        return newScrollPane(newListView(bean, choices));
    }

    public static <T, S> ScrollPane newScrollListView(S bean, String propertyName, Collection<T> choices) {
        return newScrollPane(newListView(bean, propertyName, choices));
    }

    // ------------------------------------------------------------------------------------------

    public static RadioButton newRadioButton(String id, String title, ToggleGroup group) {
        RadioButton rb = new RadioButton(title);
        rb.setId(id);
        rb.setToggleGroup(group);
        rb.setSelected(true);
        return rb;
    }

    public static <T> RadioButton newRadioButton(String title, T bean, String propertyName, ToggleGroup group) {
        RadioButton rb = new RadioButton(title);
        rb.setId(propertyName);
        rb.setToggleGroup(group);
        rb.selectedProperty().bindBidirectional(buildBooleanProperty(bean, propertyName));
        return rb;
    }

    public static RadioButton newRadioButton(String title, Property<Boolean> property, ToggleGroup group) {
        RadioButton rb = new RadioButton(title);
        rb.setToggleGroup(group);
        rb.selectedProperty().bindBidirectional(property);
        return rb;
    }

    // ------------------------------------------------------------------------------------------

    public static CheckBox newCheckBox(String id, String title, boolean value) {
        CheckBox box = new CheckBox();
        box.setId(id);
        box.setText(title);
        box.setSelected(value);
        return box;
    }

    public static <T> CheckBox newCheckBox(T bean, String propertyName) {
        return newCheckBox(null, bean, propertyName);
    }

    public static <T> CheckBox newCheckBox(String title, T bean, String propertyName) {

        Property property = buildBooleanProperty(bean, propertyName);

        CheckBox box = newCheckBox(property);
        if (title != null) {
            box.setText(title);
        }
        return box;
    }

    public static <T> CheckBox newCheckBox(Property<Boolean> property) {

        String propertyName = property.getName();

        String title = LocaleUtil.getProperty(propertyName + ".label", propertyName);
        String tooltip = LocaleUtil.getProperty(propertyName + ".tooltip", propertyName);
        String prompt = LocaleUtil.getProperty(propertyName + ".prompt", propertyName);

        CheckBox box = new CheckBox();
        box.setContentDisplay(ContentDisplay.RIGHT);
        if (title != null) {
            box.setText(title);
        }
        if (tooltip != null) {
            box.setTooltip(new Tooltip(tooltip));
        }
        box.selectedProperty().bindBidirectional(property);
        return box;
    }

    // ------------------------------------------------------------------------------------------

    public static Slider newSlider(String id, double minimumValue, double maximumValue, double currentValue) {
        Slider box = new Slider();
        box.setId(id);
        box.setMin(minimumValue);
        box.setMax(maximumValue);
        box.setShowTickLabels(true);
        box.setShowTickMarks(true);
        box.setValue(currentValue);
        return box;
    }

    public static <T> Slider newSlider(T bean, String propertyName, long minimumValue, long maximumValue) {
        return newSlider(buildLongProperty(bean, propertyName), minimumValue, maximumValue);
    }

    public static <T> Slider newSlider(T bean, String propertyName, double minimumValue, double maximumValue) {
        return newSlider(buildDoubleProperty(bean, propertyName), minimumValue, maximumValue);
    }

    public static <T> Slider newSlider(T bean, String propertyName, int minimumValue, int maximumValue) {
        return newSlider(buildIntProperty(bean, propertyName), minimumValue, maximumValue);
    }

    public static Slider newSlider(Property property, double minimumValue, double maximumValue) {
        Slider box = new Slider();
        box.setMin(minimumValue);
        box.setMax(maximumValue);
        box.setShowTickLabels(true);
        box.setShowTickMarks(true);
        box.valueProperty().bindBidirectional(property);
        return box;
    }

    // ------------------------------------------------------------------------------------------

    public static Spinner<Double> newSpinner(String id, double minimumValue, double maximumValue, double currentValue) {
        Spinner<Double> box = new Spinner<Double>();
        box.setId(id);
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(minimumValue, maximumValue,
                currentValue);
        box.setValueFactory(valueFactory);
        return box;
    }

    public static <T> Spinner<Double> newSpinner(T bean, String propertyName, double minimumValue, double maximumValue) {
        DoubleProperty property = buildDoubleProperty(bean, propertyName);

        Spinner<Double> box = new Spinner<Double>();
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(minimumValue, maximumValue,
                property.get());
        box.setValueFactory(valueFactory);
        return box;
    }

    public static <T> Spinner<Integer> newSpinner(T bean, String propertyName, long minimumValue, long maximumValue) {
        LongProperty property = buildLongProperty(bean, propertyName);

        Spinner<Integer> box = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory((int) minimumValue,
                (int) maximumValue, (int) property.get());
        box.setValueFactory(valueFactory);
        return box;
    }

    public static <T> Spinner<Integer> newSpinner(T bean, String propertyName, int minimumValue, int maximumValue) {
        IntegerProperty property = buildIntProperty(bean, propertyName);

        Spinner<Integer> box = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(minimumValue, maximumValue,
                property.get());
        box.setValueFactory(valueFactory);
        return box;
    }

    // ------------------------------------------------------------------------------------------

    public static Tab newTab(String title, Node content) {
        Tab tab = new Tab();
        tab.setId(title);
        tab.setText(title);
        tab.setTooltip(new Tooltip(title));
        // tab.setIcon(resourceManager.getIcon(title + ".icon"));
        tab.setContent(content);
        return tab;
    }

    public static Tab newScrollTab(String title, Node content) {
        Tab tab = new Tab();
        tab.setId(title);
        tab.setText(title);
        tab.setTooltip(new Tooltip(title));
        // tab.setIcon(resourceManager.getIcon(title + ".icon"));
        tab.setContent(newScrollPane(content));
        return tab;
    }

    public static ScrollPane newScrollPane(Node content) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setContent(content);
        return scrollPane;
    }

    public static ScrollPane newVerticalScrollPane(Node content) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(content);
        return scrollPane;
    }

    // ------------------------------------------------------------------------------------------

    public static <T> TableView<T> newTableView(String id) {
        TableView<T> table = new TableView<T>();
        table.setId(id);
        return table;
    }

    public static <T> TableView<T> newTableView(Table table) {

        TableView<T> tableview = new TableView<T>();
        tableview.setId(table.getName());
        tableview.setUserData(table);

        for (Column column : table.getColumns()) {
            TableColumn tableColumn = newTableColumn(column.getName(), column.getName(), 50);
            tableColumn.setUserData(tableColumn);
            tableview.getColumns().add(tableColumn);
        }
        return tableview;
    }

    public static TableView<Row> newTableView(String title, List<? extends Row> rows) {

        TableView<Row> tableview = new TableView<Row>();
        tableview.setId(title);

        if (rows.size() > 0) {
            Row row = rows.get(0);
            for (String name : row.keySet()) {
                TableColumn tableColumn = newTableColumn(name, name, 50);
                tableview.getColumns().add(tableColumn);
            }
        }
        return tableview;
    }

    public static <T> TableView<T> newTableView(String id, Class<T> clazz, Collection<T> beans) {

        TableView<T> tableview = new TableView<T>();
        tableview.setId(id);
        tableview.getItems().addAll(beans);

        Field[] fields = clazz.getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                TableColumn tableColumn = newTableColumn(field.getName(), field.getName(), 50);
                tableview.getColumns().add(tableColumn);
            }
        }
        return tableview;
    }

    public static ScrollPane newScrollTableView(String id) {
        TableView table = new TableView();
        table.setId(id);
        return newScrollPane(table);
    }

    public static <T> ScrollPane newScrollTableView(String id, Class<T> clazz, Collection<T> beans) {
        TableView<T> table = new TableView<T>();
        table.setId(id);

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            table.getColumns().add(newTableColumn(field.getName(), field.getName(), 50));
        }
        table.getItems().addAll(beans);
        return newScrollPane(table);
    }

    @SuppressWarnings("unchecked")
    public static <T> void updateTableView(Node ancestor, String id, T bean) {
        TableView<T> tableView = (TableView<T>) ancestor.lookup("#" + id);
        assertNotNull(tableView, "UI Item not found: " + id);
        tableView.getItems().add(bean);
    }

    @SuppressWarnings("unchecked")
    public static <T> void updateTableView(Node ancestor, String id, Collection<T> beans) {
        TableView<T> tableView = (TableView<T>) ancestor.lookup("#" + id);
        assertNotNull(tableView, "UI Item not found: " + id);
        tableView.getItems().addAll(beans);
    }

    // ------------------------------------------------------------------------------------------

    public static <S, T> TableColumn<S, T> newTableColumn(TableView<S> table, String title, String fieldName,
            double prefWidthPercent) {
        TableColumn<S, T> column = new TableColumn<S, T>(title);
        if (prefWidthPercent != 0.0) {
            column.prefWidthProperty().bind(table.widthProperty().multiply(prefWidthPercent));
        }
        column.setResizable(true);
        column.setCellValueFactory(new PropertyValueFactory<S, T>(fieldName));
        table.getColumns().add(column);
        return column;
    }

    public static <S, T> TableColumn<S, T> newTableColumn(String title, String fieldName, int minWidth) {
        TableColumn<S, T> column = new TableColumn<S, T>(title);
        if (minWidth > 0) {
            column.setMinWidth(minWidth);
        }
        column.setResizable(true);
        column.setCellValueFactory(new PropertyValueFactory<S, T>(fieldName));
        return column;
    }

    public static <S, T> TableColumn<S, T> newTableColumn(String title, String fieldName, int minWidth, Collection choices) {
        TableColumn<S, T> column = new TableColumn<S, T>(title);
        column.setMinWidth(minWidth);
        column.setResizable(true);
        column.setCellValueFactory(new PropertyValueFactory<S, T>(fieldName));
        column.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(choices)));
        return column;
    }

    // ------------------------------------------------------------------------------------------

    public static TitledPane newTitledPane(String title, Node child, boolean collapsible, boolean expanded) {
        return newTitledPane(title, title, child, collapsible, expanded);
    }

    public static TitledPane newTitledPane(String id, String title, Node child, boolean collapsible, boolean expanded) {
        TitledPane pane = new TitledPane(title, child);
        pane.setId(id);
        pane.setCollapsible(collapsible);
        pane.setExpanded(expanded);
        return pane;
    }

    public static <T> void updateTitledPane(Node ancestor, String id, T bean, String propertyName) {
        TitledPane item = (TitledPane) ancestor.lookup("#" + id);
        assertNotNull(item, "UI Item not found: " + id);
        item.textProperty().bindBidirectional(buildStringProperty(bean, propertyName));
    }

    // ------------------------------------------------------------------------------------------

    public static TilePane newTilePane(String id, String title, Orientation orientation) {
        TilePane box = new TilePane(orientation);
        box.setId(id);
        return box;
    }

    public static <T> TreeItem<T> newTreeItem(T bean, String filename, int width, int height) {
        return new TreeItem<T>(bean, newImageView(filename, width, height));
    }

    public static VBox newVBox(String title) {
        return newVBox(title, null);
    }

    public static VBox newVBox(String title, Node child) {
        VBox box = new VBox();
        box.setId(title);
        if (child != null) {
            box.getChildren().add(child);
        }
        return box;
    }

    public static WebView newWebView(String id, int width, int height) {
        WebView browser = new WebView();
        browser.setId(id);
        browser.setPrefSize(width, height);
        // browser.addHyperlinkListener(new SourceHyperLinkListener(this));
        return browser;
    }

    public static WebView newWebView(String id, String url, int width, int height) {
        WebView browser = new WebView();
        browser.setId(id);
        browser.setPrefSize(width, height);
        browser.getEngine().load(url);
        // browser.addHyperlinkListener(new SourceHyperLinkListener(this));
        return browser;
    }

    public static WebView newWebView(String contents) {
        WebView browser = new WebView();
        browser.getEngine().loadContent(contents);
        // browser.addHyperlinkListener(new SourceHyperLinkListener(this));
        return browser;
    }

    public static void updateWebViewWithURL(Node ancestor, String id, String url) {

        WebView item = (WebView) ancestor.lookup("#" + id);
        assertNotNull(item, "UI Item not found: " + id);

        WebEngine webEngine = item.getEngine();
        webEngine.load(url);
    }

    // ------------------------------------------------------------------------------------------

    public static <T> BooleanProperty buildBooleanProperty(T bean, String propertyName) {
        try {
            Method getter = bean.getClass().getMethod("is" + capitalize(propertyName));
            Method setter = bean.getClass().getMethod("set" + capitalize(propertyName), boolean.class);
            return JavaBeanBooleanPropertyBuilder.create().bean(bean).getter(getter).setter(setter).name(propertyName).build();
        } catch (Exception ex) {
            System.err.println("No Such Method: " + bean + ", " + propertyName);
            ex.printStackTrace();
        }
        return null;
    }

    public static <T> StringProperty buildStringProperty(T bean, String propertyName) {
        try {
            return JavaBeanStringPropertyBuilder.create().bean(bean).name(propertyName).build();
        } catch (Exception ex) {
            System.err.println("No Such Method: " + bean + ", " + propertyName);
            ex.printStackTrace();
        }
        return null;
    }

    public static <T> IntegerProperty buildIntegerProperty(T bean, String propertyName) {
        try {
            Method getter = bean.getClass().getMethod("get" + capitalize(propertyName));
            Method setter = bean.getClass().getMethod("set" + capitalize(propertyName), Integer.class);
            return JavaBeanIntegerPropertyBuilder.create().bean(bean).getter(getter).setter(setter).name(propertyName).build();
        } catch (Exception ex) {
            System.err.println("No Such Method: " + bean + ", " + propertyName);
            ex.printStackTrace();
        }
        return null;
    }

    public static <T> IntegerProperty buildIntProperty(T bean, String propertyName) {
        try {
            Method getter = bean.getClass().getMethod("get" + capitalize(propertyName));
            Method setter = bean.getClass().getMethod("set" + capitalize(propertyName), int.class);
            return JavaBeanIntegerPropertyBuilder.create().bean(bean).getter(getter).setter(setter).name(propertyName).build();
        } catch (Exception ex) {
            System.err.println("No Such Method: " + bean + ", " + propertyName);
            ex.printStackTrace();
        }
        return null;
    }

    public static <T> LongProperty buildLongProperty(T bean, String propertyName) {
        try {

            Method getter = bean.getClass().getMethod("get" + capitalize(propertyName));
            Method setter = bean.getClass().getMethod("set" + capitalize(propertyName), Long.class);
            return JavaBeanLongPropertyBuilder.create().bean(bean).getter(getter).setter(setter).name(propertyName).build();
        } catch (Exception ex) {
            System.err.println("No Such Method: " + bean + ", " + propertyName);
            ex.printStackTrace();
        }
        return null;
    }

    public static <T> DoubleProperty buildDoubleProperty(T bean, String propertyName) {
        try {
            Method getter = bean.getClass().getMethod("get" + capitalize(propertyName));
            Method setter = bean.getClass().getMethod("set" + capitalize(propertyName), Double.class);
            return JavaBeanDoublePropertyBuilder.create().bean(bean).getter(getter).setter(setter).name(propertyName).build();
        } catch (Exception ex) {
            System.err.println("No Such Method: " + bean + ", " + propertyName);
            ex.printStackTrace();
        }
        return null;
    }

    public static <T> ObjectProperty buildObjectProperty(T bean, String propertyName) {
        try {
            return JavaBeanObjectPropertyBuilder.create().bean(bean).name(propertyName).build();
        } catch (Exception ex) {
            System.err.println("No Such Method: " + bean + ", " + propertyName);
            ex.printStackTrace();
        }
        return null;
    }

    // ------------------------------------------------------------------------------------------

    public static <T, S> List<S> asList(Collection<T> beans, String name) {
        List<S> list = new ArrayList<S>();
        if (beans != null) {
            for (T bean : beans) {
                Object value = getValue(bean, name);
                if (value != null) {
                    list.add((S) value);
                }
            }
        }
        return list;
    }

    public static <T> List<String> asUniqueSortedList(Collection<T> beans, String name) {
        List<String> list = new ArrayList<String>();
        if (beans != null) {
            for (T bean : beans) {
                Object value = getValue(bean, name);
                if (value != null) {
                    String str = value.toString();
                    if (!list.contains(str)) {
                        list.add(str);
                    }
                }
            }
        }
        Collections.sort(list);
        return list;
    }

    public static List<String> asUniqueSortedList(List<Row> beans, String name) {
        List<String> list = new ArrayList<String>();
        if (beans != null) {
            for (Row bean : beans) {
                Object value = bean.get(name);
                if (value != null) {
                    String str = value.toString();
                    if (!list.contains(str)) {
                        list.add(str);
                    }
                }
            }
        }
        Collections.sort(list);
        return list;
    }

    public static <T> Object getValue(T bean, String name) {
        try {
            BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor prop = find(info.getPropertyDescriptors(), name);
            return prop.getReadMethod().invoke(bean);
        } catch (Throwable ex) {
            System.err.println("ERROR: UIUtil.getValue property not found " + bean + ", " + name);
        }
        return null;
    }

    public static PropertyDescriptor find(PropertyDescriptor[] props, String propertyName) {
        for (PropertyDescriptor pd : props) {
            if (pd.getName().equalsIgnoreCase(propertyName)) {
                return pd;
            }
        }
        return null;
    }

    /**
     * Method to handle error messages from inner classes.
     */

    public static void logError(String msg) {
        System.out.println(msg);
    }

    public static void logInfo(String msg) {
        System.out.println(msg);
    }

    public static void logException(String msg, Exception ex) {
        System.out.println(msg);
        ex.printStackTrace();
    }

    public static void showMessage(String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Title");
        alert.setContentText(msg);
        alert.setHeaderText("MastHead");
        alert.showAndWait();
    }

    public static void showTrace(String msg, Exception err) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void showError(String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Title");
        alert.setContentText(msg);
        alert.setHeaderText("MastHead");
        alert.showAndWait();
    }

    public static void showErrorDialog(String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void showException(String msg, Throwable e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();

        e.printStackTrace();
    }

    public static void showInformationDialog(String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static boolean askForConfirmation(String msg) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Title");
        alert.setHeaderText("MastHead");
        alert.setContentText(msg);
        Optional<ButtonType> result = alert.showAndWait();

        return (result.get() == ButtonType.OK) ? true : false;
    }

    public <T> T askForChoice(String msg, T choices[]) {
        ChoiceDialog<T> dialog = new ChoiceDialog<T>(choices[0], choices);
        dialog.setTitle("Title");
        dialog.setHeaderText("MastHead");
        dialog.setContentText(msg);

        Optional<T> result = dialog.showAndWait();
        return (result.isPresent()) ? result.get() : null;
    }

    public static String showOpenDialog(Stage stage, String title, String filename, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (filename != null) {
            fileChooser.setInitialDirectory(new File(filename).getParentFile());
        }
        if (extensions != null) {
            for (String extension : extensions) {
                fileChooser.getExtensionFilters().add(new ExtensionFilter(extension));
            }
        }
        File file = fileChooser.showOpenDialog(stage);
        return (file != null) ? file.getPath() : null;
    }

    public static String showSaveDialog(Stage stage, String title, String filename, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (filename != null) {
            fileChooser.setInitialDirectory(new File(filename).getParentFile());
        }
        if (extensions != null) {
            for (String extension : extensions) {
                fileChooser.getExtensionFilters().add(new ExtensionFilter(extension));
            }
        }

        File file = fileChooser.showSaveDialog(stage);
        return (file != null) ? file.getPath() : null;
    }

    public static String showTextDialog(String title, String value) {
        TextInputDialog dialog = new TextInputDialog(value);
        dialog.setTitle(title);
        dialog.setContentText(title);

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        return (result.isPresent()) ? result.get() : null;
    }

    public static void showStatus(String title) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.showAndWait();
    }

    public static void assertNotNull(Object o, String msg) {
        if (o == null) {
            System.out.println("assertNotNull: " + msg);
            throw new IllegalArgumentException(msg);
        }
    }

    private final static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    static class FieldPathValueListener<T> implements ChangeListener<T> {
        Property property;

        public FieldPathValueListener(Property property) {
            this.property = property;
        }

        @Override
        public void changed(final ObservableValue<? extends T> observable, final T oldValue, final T newValue) {

            System.out.println("Value changed from: " + oldValue + " to: " + newValue);
            if (newValue != null) {
                property.setValue(newValue);
            }
        }
    }
}

class InfoButtonAction implements EventHandler<ActionEvent> {

    String resource;
    Bounds bounds;

    public InfoButtonAction(String resource, Bounds bounds) {
        this.resource = resource;
        this.bounds = bounds;
    }

    @Override
    public void handle(ActionEvent e) {
        Dialogs.showHelpDialog(resource, bounds);
    }
}
