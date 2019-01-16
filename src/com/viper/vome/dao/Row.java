/*
 * -----------------------------------------------------------------------------
 *                      VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * MIT License
 * 
 * Copyright (c) #{classname}.html #{util.YYYY()} Viper Software Services
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE
 *
 * -----------------------------------------------------------------------------
 */

package com.viper.vome.dao;

import java.util.HashMap;

public class Row extends HashMap<String, Object> {

    public Row() {
        super();
    }

    public Row(int columnCount) {
        super(columnCount);
    }

    public Object getValue(String columnName) {
        return get(columnName);
    }

    public String getString(String columnName) {
        Object value = get(columnName);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof byte[]) {
            return new String((byte[]) value);
        }
        return value.toString();
    }

    public int getInt(String columnName) {
        Object value = get(columnName);
        return (value == null) ? 0 : (Integer) value;
    }

    public int getInteger(String columnName) {
        Object value = get(columnName);
        return (value == null) ? 0 : (Integer) value;
    }

    public short getShort(String columnName) {
        Object value = get(columnName);
        return (value == null) ? 0 : (Short) value;
    }

    public long getLong(String columnName) {
        Object value = get(columnName);
        return (value == null) ? 0 : (Long) value;
    }

    public boolean getBoolean(String columnName) {
        Object value = get(columnName);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            if ("yes".equalsIgnoreCase((String) value)) {
                return true;
            }
            if ("true".equalsIgnoreCase((String) value)) {
                return true;
            }
        }
        return false;
    }

    public void setValue(String columnName, Object value) {
        put(columnName, value);
    }

}
