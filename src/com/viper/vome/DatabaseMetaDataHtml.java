/*
 * --------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * --------------------------------------------------------------
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
 * ---------------------------------------------------------------
 */

package com.viper.vome;

import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

public class DatabaseMetaDataHtml {

	private static ResourceBundle _bundle = ResourceBundle.getBundle("dbmetadata");

	/**
	 * Given the database meta data object from jdbc, generate into the string
	 * buffer the html formatted data of the meta data object. Reflection is
	 * used to process all the methods in the meta data object. The
	 * formatMethodFromDatabaseMetaData will format all the easy no-parameter
	 * methods in the database meta data. The more complex methds will require
	 * specialized routines for html formatting. The design of this method
	 * allows for the handling of methods availabel in the database meta data
	 * object, but also works even hem those methods are missing as in older
	 * versions.
	 * 
	 * @param buf
	 * @param dmd
	 */
	public void formatMetaData(StringBuffer buf, DatabaseMetaData dmd) {
		try {
			buf.append("<h1>Database Meta Data</h1><br>\n");

			Method methods[] = DatabaseMetaData.class.getMethods();
			Arrays.sort(methods, 0, methods.length - 1);

			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				try {
					String name = method.getName();
					Class types[] = method.getParameterTypes();
					Class returnType = method.getReturnType();

					if ((types == null || types.length == 0) && returnType != ResultSet.class) {
						continue;
					}

					buf.append("<p>");
					buf.append("<hr fgcolor=\"lightgreen\" align=\"center\" width=\"95%\">\n");
					buf.append("<h2>" + method.getName() + "</h2>");
					appendDefintion(buf, method.getName());

					if ("getCatalogs".equals(name)) {
						// formatTableFromResultSet(buf, dmd.getCatalogs(),
						// "<catalogs>");
					} else if ("getSchemas".equals(name)) {
						// formatTableFromResultSet(buf, dmd.getSchemas(),
						// "<schemas>");
						// This should be the supportsConvert(int fromType, int
						// toType) method.
					} else {
						System.out.println("ERROR: unsupported method : " + name);
					}

				} catch (Exception e) {
					printSQLException(e);
					e.printStackTrace();
				}
			}
			buf.append("</table>\n");

		} catch (Exception e) {
			printSQLException(e);
			e.printStackTrace();
		}
	}

	private void printSQLException(Exception err) {
		if (err == null || !(err instanceof SQLException))
			return;

		SQLException sqlerr = (SQLException) err;
		while (sqlerr != null) {
			System.out.println(sqlerr.getErrorCode() + " : " + sqlerr.toString());
			if (sqlerr.getErrorCode() != 1044) {
				err.printStackTrace();
			}
			sqlerr = sqlerr.getNextException();
		}
	}

	private void appendDefintion(StringBuffer buf, String name) {
		try {
			String str = _bundle.getString(name);
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
		} catch (Exception e) {
			System.out.println("Definitions not found: " + name);
		}
	}
}