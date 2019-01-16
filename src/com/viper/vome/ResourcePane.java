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
import com.viper.vome.model.PersistentProperties;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class ResourcePane extends BorderPane {

    private String resourceName = null;
    private Session session = null;

    private ResourcePane(Session frame) {
        super();

        this.session = frame;

        setWidth(400);
        setHeight(400);

        GridPane pane = new GridPane();
        pane.add(UIUtil.newLabel("Resource:"), 1, 1);
        pane.add(UIUtil.newTextField(this, "resourceName",  0), 2, 1);
        pane.add(UIUtil.newButton("Resource", new ResourceNameBrowse()), 3, 1);

        setCenter(UIUtil.newScrollPane(pane));
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    class ApplyAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            try {
                PersistentProperties properties = new PersistentProperties(getResourceName());
                session.getChangeManager().fireChangeEvent(properties);
                properties.persist();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            setVisible(false);
        }
    }

    class ResourceNameBrowse implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            String filename = null;
            filename = UIUtil.showOpenDialog(session.getStage(), "Resource Name", filename);
            if (filename != null) {
                PersistentProperties properties = new PersistentProperties(filename);
                session.getChangeManager().fireChangeEvent(properties);
            }
        }
    }
}
