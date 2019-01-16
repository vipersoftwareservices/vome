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

package com.viper.vome.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PropertyChangeManager extends ArrayList<PropertyChangeListener> {

    private final Map<String, Object> cache = new HashMap<String, Object>();

    public PropertyChangeManager() {
        super();
    }

    public void fireChangeEvent(Object source) {
        if (source != null) {
            PropertyChangeEvent event = new PropertyChangeEvent(source, null,
                    null, null);
            fireChangeEvent(event);
        }
    }

    public void fireChangeEvent(Object source, String propertyName) {
        if (source != null) {
            PropertyChangeEvent event = new PropertyChangeEvent(source,
                    propertyName, null, source);
            fireChangeEvent(event);
        }
    }

    public void fireChangeEvent(Object source, String propertyName,
            Object newValue) {
        if (source != null) {
            PropertyChangeEvent event = new PropertyChangeEvent(source,
                    propertyName, null, newValue);
            fireChangeEvent(event);
        }
    }

    public void fireChangeEvent(Object source, String propertyName,
            Object oldValue, Object newValue) {
        if (source != null) {
            PropertyChangeEvent event = new PropertyChangeEvent(source,
                    propertyName, oldValue, newValue);
            fireChangeEvent(event);
        }
    }

    private void fireChangeEvent(PropertyChangeEvent event) {
        cache.put(event.getPropertyName(), event.getNewValue());
        for (PropertyChangeListener listener : this) {
            fireChangeEvent(listener, event);
        }
    }

    private void fireChangeEvent(PropertyChangeListener listener,
            PropertyChangeEvent event) {
        try {
            listener.propertyChange(event);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public Object getValue(String key) {
        return cache.get(key);
    }
}
