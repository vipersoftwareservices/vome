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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

/**
 * A <code>Transferable</code> which implements the capability required to
 * transfer a <code>String</code>.
 * 
 * This <code>Transferable</code> properly supports
 * <code>DataFlavor.stringFlavor</code> and all equivalent flavors. Support
 * for <code>DataFlavor.plainTextFlavor</code> and all equivalent flavors is
 * <b>deprecated</b>. No other <code>DataFlavor</code>s are supported.
 * 
 * @see java.awt.datatransfer.DataFlavor#stringFlavor
 * @see java.awt.datatransfer.DataFlavor#plainTextFlavor
 */
public class TableSelection implements Transferable, ClipboardOwner {

	public static DataFlavor CELL_FLAVOR = new DataFlavor(java.util.List.class, "Table Cell");

	private static final DataFlavor[] flavors = { CELL_FLAVOR };

	private List data;

	/**
	 * Creates a <code>Transferable</code> capable of transferring the
	 * specified <code>String</code>.
	 */
	public TableSelection(List data) {
		this.data = data;
	}

	/**
	 * Returns an array of flavors in which this <code>Transferable</code> can
	 * provide the data. <code>DataFlavor.stringFlavor</code> is properly
	 * supported. Support for <code>DataFlavor.plainTextFlavor</code> is
	 * <b>deprecated</b>.
	 * 
	 * @return an array of length two, whose elements are <code>DataFlavor.
	 *         stringFlavor</code>
	 *         and <code>DataFlavor.plainTextFlavor</code>
	 */
	public DataFlavor[] getTransferDataFlavors() {
		// returning flavors itself would allow client code to modify
		// our internal behavior
		return flavors.clone();
	}

	/**
	 * Returns whether the requested flavor is supported by this
	 * <code>Transferable</code>.
	 * 
	 * @param flavor the requested flavor for the data
	 * @return true if <code>flavor</code> is equal to
	 *         <code>DataFlavor.stringFlavor</code> or
	 *         <code>DataFlavor.plainTextFlavor</code>; false if
	 *         <code>flavor</code> is not one of the above flavors
	 * @throws NullPointerException if flavor is <code>null</code>
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (int i = 0; i < flavors.length; i++) {
			if (flavor.equals(flavors[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the <code>Transferable</code>'s data in the requested
	 * <code>DataFlavor</code> if possible. If the desired flavor is
	 * <code>DataFlavor.stringFlavor</code>, or an equivalent flavor, the
	 * <code>String</code> representing the selection is returned. If the
	 * desired flavor is <code>DataFlavor.plainTextFlavor</code>, or an
	 * equivalent flavor, a <code>Reader</code> is returned. <b>Note:</b> The
	 * behavior of this method for </code>DataFlavor.plainTextFlavor</code>
	 * and equivalent <code>DataFlavor</code>s is inconsistent with the
	 * definition of <code>DataFlavor.plainTextFlavor</code>.
	 * 
	 * @param flavor the requested flavor for the data
	 * @return the data in the requested flavor, as outlined above
	 * @throws UnsupportedFlavorException if the requested data flavor is not
	 *             equivalent to either <code>DataFlavor.stringFlavor</code>
	 *             or <code>DataFlavor.plainTextFlavor</code>
	 * @throws IOException if an IOException occurs while retrieving the data.
	 *             By default, StringSelection never throws this exception, but
	 *             a subclass may.
	 * @throws NullPointerException if flavor is <code>null</code>
	 * @see java.io.Reader
	 */
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		// JCK Test StringSelection0007: if 'flavor' is null, throw NPE
		if (flavor.equals(CELL_FLAVOR)) {
			return data;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}
}