/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2003/06/15
 *
 * Copyright 1998-2009 by Viper Software Services
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

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.viper.vome.dao.Connection;
import com.viper.vome.dao.Database;
import com.viper.vome.dao.Table;
import com.viper.vome.jfx.UIUtil;
import com.viper.vome.model.Parameter;

import javafx.scene.layout.BorderPane;

public class DatabaseParameters extends BorderPane {

    public DatabaseParameters() {

        Session session = Session.getInstance();

        loadMetaData();

        // This generic code path handles all non-parameter methods in the
        // database meta data object
        List<Parameter> rows = new ArrayList<Parameter>();

        for (Method method : Database.class.getDeclaredMethods()) {
            Class types[] = method.getParameterTypes();
            Class returnType = method.getReturnType();
            if ((types == null || types.length == 0) && returnType != ResultSet.class) {
                // rows.add(mkRow(new Parameter(), session.getDatabases().getMetadata(), method));
            }
        }

        setCenter(UIUtil.newScrollTableView("table-database-parameters", Parameter.class, rows));
    }

    private void loadMetaData() {
        try {
            Session session = Session.getInstance();
            Connection connection = session.openConnection();
            // if (session.getDatabases().getMetadata() == null) {
            // driver.loadMetaData(writer, session.getDatabases());
            // }
        } catch (Throwable t) {
            System.out.println("loadMetaData: " + t);
        }
    }

    private Parameter mkRow(Parameter row, List<Table> meta, Method method) {
        try {
            if (method != null || meta != null) {
                row.setParameter(method.getName());
                row.setValue("" + method.invoke(meta));
                row.setDefinition(getDefinition(method.getName()));
            }
        } catch (Exception ex) {
            System.out.println("Method name: " + method.getName());
        }
        return row;
    }

    private String getDefinition(String name) throws Exception {
        Session session = Session.getInstance();
        StringBuffer buf = new StringBuffer();
        String str = session.getMetaData().getString(name);
        char previous = '\n';
        char prevchar = '\n';
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (previous == '@' && Character.isWhitespace(c)) {
                previous = c;
                buf.append("</b>");
                buf.append(c);
            } else if (prevchar == '\n' && c == '\n') {
                ;
            } else if (c == '@') {
                previous = '@';
                buf.append("<br>\n<b>");
            } else {
                buf.append(c);
            }
            prevchar = c;
        }
        return buf.toString();
    }
}
