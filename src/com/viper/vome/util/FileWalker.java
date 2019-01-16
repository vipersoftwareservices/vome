/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2008/06/15
 *
 * Copyright 1998-2008 by Viper Software Services
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
 * @version 1.0, 06/15/2008 
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.vome.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileWalker {

	private final static List<String> ignoreFiles = new ArrayList<String>();
	static {
		ignoreFiles.add("CVS");
	}

	public static List<String> find(String filename, String pattern) {
		List<String> list = new ArrayList<String>();
		for (String ignoreFile : ignoreFiles) {
			if (filename.endsWith(ignoreFile)) {
				return list;
			}
		}

		if (filename.matches(pattern)) {
			list.add(filename);
		}

		File file = new File(filename);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					list.addAll(find(f.getPath(), pattern));
				}
			}
		}
		return list;
	}

	public static String getOutFilename(String indir, String infilename, String outdir) {
		return outdir + infilename.substring(indir.length());
	}

	public static String getOutFilename(String indir, String infilename, String outdir, String extension) {
        return outdir
                + infilename.substring(indir.length(),
                        infilename.lastIndexOf('.')) + '.' + extension;
	}
}