/*
 * --------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * --------------------------------------------------------------
 *
 * @(#)filename.java    1.00 2003/06/15
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

package com.viper.vome.view.table;

public final class RowNumber extends Number implements Comparable<RowNumber> {

    long value = 0;

    public RowNumber(long number) {
        value = number;
    }

    public long getRowNumber() {
        return value;
    }

    public void setRowNumber(long value) {
        this.value = value;
    }

    /**
     * Returns the value of the specified number as an <code>int</code>. This
     * may involve rounding or truncation.
     * 
     * @return the numeric value represented by this object after conversion to
     *         type <code>int</code>.
     */
    public int intValue() {
        return (int) value;
    }

    /**
     * Returns the value of the specified number as a <code>long</code>. This
     * may involve rounding or truncation.
     * 
     * @return the numeric value represented by this object after conversion to
     *         type <code>long</code>.
     */
    public long longValue() {
        return value;
    }

    /**
     * Returns the value of the specified number as a <code>float</code>. This
     * may involve rounding.
     * 
     * @return the numeric value represented by this object after conversion to
     *         type <code>float</code>.
     */
    public float floatValue() {
        return value;
    }

    /**
     * Returns the value of the specified number as a <code>double</code>. This
     * may involve rounding.
     * 
     * @return the numeric value represented by this object after conversion to
     *         type <code>double</code>.
     */
    public double doubleValue() {
        return value;
    }

    /**
     * Compares two <code>Long</code> objects numerically.
     * 
     * @param anotherLong
     *            the <code>Long</code> to be compared.
     * @return the value <code>0</code> if this <code>Long</code> is equal to
     *         the argument <code>Long</code>; a value less than <code>0</code>
     *         if this <code>Long</code> is numerically less than the argument
     *         <code>Long</code>; and a value greater than <code>0</code> if
     *         this <code>Long</code> is numerically greater than the argument
     *         <code>Long</code> (signed comparison).
     */
    public int compareTo(RowNumber anotherLong) {
        long anotherVal = anotherLong.value;
        return (value < anotherVal ? -1 : (value == anotherVal ? 0 : 1));
    }

    public String toString() {
        return Long.toString(value);
    }
}
