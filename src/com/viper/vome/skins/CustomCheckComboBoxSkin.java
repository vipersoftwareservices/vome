/**
 * Copyright (c) 2013, ControlsFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.viper.vome.skins;

import java.util.Collections;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;

import org.controlsfx.control.CheckComboBox;

import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

public class CustomCheckComboBoxSkin<T> extends BehaviorSkinBase<CheckComboBox<T>, BehaviorBase<CheckComboBox<T>>> {

    /**************************************************************************
     * 
     * Static fields
     * 
     **************************************************************************/

    /**************************************************************************
     * 
     * fields
     * 
     **************************************************************************/

    // visuals
    private final ComboBox<T> comboBox;
    private final ListCell<T> buttonCell;

    // data
    private final ObservableList<T> items;
    private final ReadOnlyUnbackedObservableList<Integer> selectedIndices;
    private final ReadOnlyUnbackedObservableList<T> selectedItems;

    private static final int MAX_ITEMS = 1;

    /**************************************************************************
     * 
     * Constructors
     * 
     **************************************************************************/

    public CustomCheckComboBoxSkin(final CheckComboBox<T> control, final String label) {
        super(control, new BehaviorBase<>(control, Collections.<KeyBinding> emptyList()));

        this.items = control.getItems();

        selectedIndices = (ReadOnlyUnbackedObservableList<Integer>) control.getCheckModel().getCheckedIndices();
        selectedItems = (ReadOnlyUnbackedObservableList<T>) control.getCheckModel().getCheckedItems();

        comboBox = new ComboBox<T>(items) {
            protected javafx.scene.control.Skin<?> createDefaultSkin() {
                return new ComboBoxListViewSkin<T>(this) {
                    // overridden to prevent the popup from disappearing
                    @Override
                    protected boolean isHideOnClickEnabled() {
                        return false;
                    }
                };
            }
        };

        // installs a custom CheckBoxListCell cell factory
        comboBox.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
            public ListCell<T> call(ListView<T> listView) {
                return new CheckBoxListCell<T>(new Callback<T, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(T item) {
                        return control.getItemBooleanProperty(item);
                    }
                });
            };
        });

        // we render the selection into a custom button cell, so that it can
        // be pretty printed (e.g. 'Item 1, Item 2, Item 10').
        buttonCell = new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                // we ignore whatever item is selected, instead choosing
                // to display the selected item text using commas to separate
                // each item
                final StringBuilder sb = new StringBuilder();
                if (selectedItems.size() <= MAX_ITEMS) {
                    for (int i = 0; i < selectedItems.size(); i++) {
                        if (sb.length() > 0) {
                            sb.append(", "); //$NON-NLS-1$
                        }
                        sb.append(selectedItems.get(i));
                    }
                } else {
                    sb.append(selectedItems.size());
                    sb.append(" ");
                    sb.append(label);

                }
                setText(sb.toString());

            }
        };
        comboBox.setButtonCell(buttonCell);

        selectedIndices.addListener(new ListChangeListener<Integer>() {
            @Override
            public void onChanged(final Change<? extends Integer> c) {
                // we update the display of the ComboBox button cell by
                // just dumbly updating the index every time selection changes.
                buttonCell.updateIndex(1);
            }
        });

        getChildren().add(comboBox);
    }

    /**************************************************************************
     * 
     * Overriding public API
     * 
     **************************************************************************/

    /**************************************************************************
     * 
     * Implementation
     * 
     **************************************************************************/

    /**************************************************************************
     * 
     * Support classes / enums
     * 
     **************************************************************************/

}
