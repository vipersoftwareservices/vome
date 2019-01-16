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

import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class CustomRowFactory<T> implements Callback<TableView<T>, TableRow> {
    @Override
    public TableRow call(TableView<T> param) {
        return new TableRow() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                Session session = Session.getInstance();

                if (session.getBandingInterval() <= 1) {
                    getProperties().remove("-fx-control-inner-background");
                    getProperties().remove("-fx-text-inner-color");

                } else if ((getIndex() % session.getBandingInterval()) == 0) {
                    getProperties().put("-fx-control-inner-background", session.getBandingColor());
                    getProperties().put("-fx-text-inner-color", session.getFontColor());

                } else {
                    getProperties().put("-fx-control-inner-background", session.getNonBandingColor());
                    getProperties().put("-fx-text-inner-color", session.getFontColor());
                }
            }
        };
    }
}
