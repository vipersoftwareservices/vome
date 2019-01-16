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

package com.viper.vome.model;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Vector;

public class PersistentProperties extends Properties {

    String filename = null;

    public PersistentProperties(String filename) {
        super();

        this.filename = filename;
        try {
            if (filename.endsWith(".properties")) {
                load(new FileInputStream(filename));
            } else {
                loadFromXML(new FileInputStream(filename));
            }
        } catch (InvalidPropertiesFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            ; // Ignore this error, don't care if file is not there
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void persist() throws FileNotFoundException, IOException {
        if (filename.endsWith(".properties")) {
            super.store(new FileOutputStream(filename), "");
        } else {
            super.storeToXML(new FileOutputStream(filename), "");
        }
    }

    public void storeToXML() throws FileNotFoundException, IOException {
        super.storeToXML(new FileOutputStream(filename), "");
    }

    public Vector<String> getNames() {
        Vector<String> v = new Vector();
        Enumeration names = propertyNames();
        while (names.hasMoreElements()) {
            v.add((String) names.nextElement());
        }
        return v;
    }

    public String getFilename() {
        return filename;
    }

    public boolean hasProperty(String name) {
        return containsKey(name);
    }

    public String[] getMultipleProperty(String name) {
        String prop = getProperty(name);
        if (prop == null) {
            return null;
        }
        return prop.split(",");
    }

    public void setMultipleProperty(String name, String[] value) {
        setProperty(name, join(",", value));
    }

    public Color getColor(String name) {
        return getColor(name, Color.white);
    }

    public Color getColor(String name, Color defaultColor) {
        String color = getProperty(name);
        if (color == null || color.length() == 0) {
            return defaultColor;
        }
        return Color.decode(getProperty(name));
    }

    public void setColor(String name, Color color) {
        setProperty(name, encode(color));
    }

    private String encode(Color color) {
        return "#" + Integer.toString(color.getRed(), 16) + Integer.toString(color.getGreen(), 16)
                + Integer.toString(color.getBlue(), 16);
    }

    public String join(String delim, String... values) {
        if (values == null || values.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (sb.length() > 0) {
                sb.append(delim);
            }
            sb.append(value);
        }
        return sb.toString();
    }
}
