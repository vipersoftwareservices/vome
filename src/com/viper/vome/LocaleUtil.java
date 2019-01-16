/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2012/01/15
 *
 * Copyright 1998-2012 by Viper Software Services
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
 * @version 1.0, 01/15/2012
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.vome;

import java.io.InputStream;
import java.util.Properties;

public class LocaleUtil {

    private static final String PropertyFilename = "/com/viper/vome/DatabaseViewer.xml";
    private static Properties bundle = null;

    public static final Properties getBundle() {

        if (bundle != null) {
            return bundle;
        }

        bundle = new Properties();
        try {
            final InputStream in = LocaleUtil.class.getResourceAsStream(PropertyFilename);
            bundle.loadFromXML(in);
            in.close();
        } catch (Exception ex) {
            System.err.println("Exception encountered while reading from " + PropertyFilename);
        }
        return bundle;
    }

    public static final String getProperty(String key) {
        return getBundle().getProperty(key);
    }

    public static final String getProperty(String key, String defaultValue) {
        if (getBundle().containsKey(key)) {
            return getBundle().getProperty(key);
        }
        return defaultValue;
    }

    public static final int getPropertyInt(String name, int defaultValue) {
        return Integer.parseInt(getBundle().getProperty(name)); // TODO , "" + defaultValue));
    }
}
