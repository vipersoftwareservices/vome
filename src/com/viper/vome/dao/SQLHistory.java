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

package com.viper.vome.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viper.vome.dao.Row;
import com.viper.vome.util.FileUtil;

public class SQLHistory {

    public static final String SQL = "sql";
    public static final String PERFORMANCE = "performance";
    public static final String TIMESTAMP = "timestamp";
    public static final String ITEM = "item";
    public static final String HISTORY = "history";

    private static final String Filename = "etc/history.xml";
    private static final List<Row> HistorySQL = new ArrayList<Row>();

    public static final Row startTimer(String sql) {
        Row row = new Row();

        row.put(SQL, sql);
        row.put(PERFORMANCE, 0);
        row.put(TIMESTAMP, System.currentTimeMillis());

        return row;
    }

    public static final void stopTimer(Row row) {
        load();

        row.put(PERFORMANCE, System.currentTimeMillis() - row.getLong(TIMESTAMP));

        HistorySQL.add(row);
    }

    public static final void stopTimer(Row row, Exception ex) {
        load();

        row.put(PERFORMANCE, System.currentTimeMillis() - row.getLong(TIMESTAMP));

        HistorySQL.add(row);
    }

    public static final void load() {
        try {
            if (HistorySQL.size() == 0) {
                readXMLFile(Filename);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void save() {
        try {
            writeXMLFile(Filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final List<Row> getHistory() {
        load();

        return HistorySQL;
    }

    private static final void readXMLFile(String filename) throws Exception {

        HistorySQL.clear();

        File fXmlFile = new File(filename);
        if (!fXmlFile.exists()) {
            return;
        }
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName(ITEM);
        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                Row row = new Row();
                row.put(SQL, eElement.getAttribute(SQL));
                row.put(PERFORMANCE, eElement.getAttribute(PERFORMANCE));
                row.put(TIMESTAMP, eElement.getAttribute(TIMESTAMP));

                HistorySQL.add(row);
            }
        }
    }

    private static final void writeXMLFile(String filename) throws Exception {

        StringBuilder buf = new StringBuilder();
        buf.append("<?xml version='1.0' encoding='UTF-8'?>");
        buf.append("<" + HISTORY + ">\n");

        for (Row row : HistorySQL) {

            buf.append("<" + ITEM + " ");
            buf.append(SQL + "=\"" + row.get(SQL) + "\" ");
            buf.append(PERFORMANCE + "=\"" + row.get(PERFORMANCE) + "\" ");
            buf.append(TIMESTAMP + "=\"" + row.get(TIMESTAMP) + "\" ");
            buf.append(" />\n");

        }
        buf.append("</" + HISTORY + ">\n");

        FileUtil.writeFile(Filename, buf.toString());
    }
}
