
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

import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class Conversions {

    private static final Map<Integer, Class<?>> conversions = new HashMap<Integer, Class<?>>();

    static {
        conversions.put(Types.CHAR, String.class);
        conversions.put(Types.NCHAR, String.class);
        conversions.put(Types.VARCHAR, String.class);
        conversions.put(Types.NVARCHAR, String.class);
        conversions.put(Types.LONGVARCHAR, String.class);
        conversions.put(Types.LONGNVARCHAR, String.class);
        conversions.put(Types.NUMERIC, BigDecimal.class);
        conversions.put(Types.DECIMAL, BigDecimal.class);

        conversions.put(Types.BIT, Boolean.class);
        conversions.put(Types.BOOLEAN, Boolean.class);
        conversions.put(Types.TINYINT, Byte.class);
        conversions.put(Types.SMALLINT, Short.class);

        conversions.put(Types.INTEGER, Integer.class);
        conversions.put(Types.BIGINT, Long.class);
        conversions.put(Types.REAL, Float.class);
        conversions.put(Types.FLOAT, Double.class);
        conversions.put(Types.DOUBLE, Double.class);
        conversions.put(Types.BINARY, Byte[].class);
        conversions.put(Types.VARBINARY, Byte[].class);
        conversions.put(Types.LONGVARBINARY, Byte[].class);
        conversions.put(Types.DATE, java.sql.Date.class);
        conversions.put(Types.TIME, java.sql.Time.class);
        conversions.put(Types.TIMESTAMP, java.sql.Timestamp.class);

        conversions.put(Types.ARRAY, String.class);
        conversions.put(Types.BLOB, String.class);
        conversions.put(Types.CLOB, String.class);
        conversions.put(Types.NCLOB, String.class);
        conversions.put(Types.DATALINK, Object.class);
        conversions.put(Types.DISTINCT, Object.class);
        conversions.put(Types.JAVA_OBJECT, Object.class);
        conversions.put(Types.NULL, Object.class);
        conversions.put(Types.OTHER, Object.class);
        conversions.put(Types.REF, Object.class);
        conversions.put(Types.ROWID, Object.class);
        conversions.put(Types.SQLXML, Object.class);
        conversions.put(Types.STRUCT, Object.class);

    }

    public static final Map<Integer, Class<?>> getConversions() {
        return conversions;
    }

    public static final Class<?> getConversionClass(int dataType) {
        return conversions.get(dataType);
    }
}
