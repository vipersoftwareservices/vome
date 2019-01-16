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

package com.viper.test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.junit.Assert;

import com.viper.vome.dao.Row;

public class AbstractTestCase extends Assert {

    public static double PRECISION = 0.0001;

    public static String getCallerMethodName() throws Exception {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    // --------------------------------------------------------------------------
    // Used to compare objects in JUnit testing
    // --------------------------------------------------------------------------
    public static void assertEqualProperties(Object bean1, Object bean2) throws Exception {
        Method methods[] = bean1.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            String methodName = methods[i].getName();
            if (methodName.startsWith("get")) {
                Object value1 = methods[i].invoke(bean1);
                Object value2 = methods[i].invoke(bean2);
                assertEquals(methodName, value1, value2);
            } else if (methodName.startsWith("is")) {
                Object value1 = methods[i].invoke(bean1);
                Object value2 = methods[i].invoke(bean2);
                assertEquals(methodName, value1, value2);
            }
        }
    }

    public void assertNotEmpty(String message, String str) {
        assertNotNull(message, str);
        assertTrue(message, str.length() > 0);
    }

    public static void assertEqualsIgnoreCase(String expected, String actual) {
        assertEqualsIgnoreCase("", expected, actual);
    }

    public static void assertEqualsIgnoreCase(String message, String expected, String actual) {
        assertEquals(message, expected.toLowerCase(), actual.toLowerCase());
    }

    public static void assertEqualsSorta(String expected, String actual) {
        try {
            Assert.assertEquals(escape(clean(expected)), escape(clean(actual)));
        } catch (Error e) {
            System.out.println("expect: " + clean(expected));
            System.out.println("actual: " + clean(actual));
            throw e;
        }
    }

    public static void assertEqualsSorta(String message, String expected, String actual) {
        try {
            Assert.assertEquals(message, escape(clean(expected)), escape(clean(actual)));
        } catch (Error e) {
            System.out.println(message);
            System.out.println("expect: " + clean(expected));
            System.out.println("actual: " + clean(actual));
            throw e;
        }
    }

    public static void assertEqualsIgnoreWhiteSpace(String message, String expected, String actual) {
        try {
            Assert.assertEquals(message, escape(clean(expected)), escape(clean(actual)));
        } catch (Error e) {
            System.out.println(message);
            System.out.println("expect: " + clean(expected));
            System.out.println("actual: " + clean(actual));
            throw e;
        }
    }

    /**
     * Asserts that two Strings are equal.
     * 
     * @param message
     * @param expected
     * @param actual
     */
    public static void assertEquals(String message, String expected, String actual) {
        try {
            Assert.assertEquals(message, expected, actual);
        } catch (Error e) {
            if (message != null) {
                System.out.println(message);
            }
            System.out.println("expect: " + expected);
            System.out.println("actual: " + actual);
            throw e;
        }
    }

    public static void assertEquals(String expected, String actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two Strings are equal.
     * 
     * @param message
     * @param expected
     * @param actual
     */
    public static void assertEquals(String message, Row expected, Row actual) {
        try {
            Assert.assertNotNull(message, expected);
            Assert.assertNotNull(message, actual);
            Assert.assertEquals(message, expected.size(), actual.size());
        } catch (Error e) {
            if (message != null) {
                System.out.println(message);
            }
            System.out.println("expect: " + expected);
            System.out.println("actual: " + actual);
            throw e;
        }
    }

    public static void assertEquals(Row expected, Row actual) {
        assertEquals(null, expected, actual);
    }

    // -------------------------------------------------------------------------

    public static void assertBetween(String message, int minimum, int maximum, int actual) {
        try {
            Assert.assertTrue(actual >= minimum && actual <= maximum);
        } catch (Error e) {
            System.out.println(message + " was " + actual + " but was expected to be between " + minimum + " and " + maximum);
            throw e;
        }
    }

    public static void assertBetween(String message, long minimum, long maximum, long actual) {
        try {
            Assert.assertTrue(actual >= minimum && actual <= maximum);
        } catch (Error e) {
            System.out.println(message + " was " + actual + " but was expected to be between " + minimum + " and " + maximum);
            throw e;
        }
    }

    public static void assertBetween(String message, float minimum, float maximum, float actual) {
        try {
            Assert.assertTrue(actual >= minimum && actual <= maximum);
        } catch (Error e) {
            System.out.println(message + " was " + actual + " but was expected to be between " + minimum + " and " + maximum);
            throw e;
        }
    }

    public static void assertBetween(String message, double minimum, double maximum, double actual) {
        try {
            Assert.assertTrue(actual >= minimum && actual <= maximum);
        } catch (Error e) {
            System.out.println(message + " was " + actual + " but was expected to be between " + minimum + " and " + maximum);
            throw e;
        }
    }

    public static <T> void assertNotEmpty(String msg, Collection<T> actual) throws Exception {
        assertNotNull(msg + ", is null", actual);
        assertTrue(msg + ", size must be greater then zero " + actual.size(), actual.size() >= 1);
    }

    public static <T> void assertNotEmpty(String msg, Collection<T> actual, int minSize) throws Exception {
        assertNotNull(msg + ", is null", actual);
        assertTrue(msg + ", size " + actual.size() + " vs " + minSize, actual.size() >= minSize);
    }

    public static void assertEquals(String msg, BigDecimal v1, BigDecimal v2, double precision) {
        assertTrue(msg + ":" + v1 + " vs " + v2, equals(v1, v2, precision));
    }

    public static String escape(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static String pretty(String str) {
        return str.replace('\t', ' ').replace(">", ">\n");
    }

    public static String clean(String str) {
        return removeExtraWhiteSpace(str);
    }

    public static String removeWhiteSpace(String str) {
        return (str == null) ? null : str.replaceAll("\\s+", "");
    }

    public static String removeExtraWhiteSpace(String str) {
        return (str == null) ? null : str.replaceAll("\\s+", " ");
    }

    public static String removeLines(String str) {
        if (str == null) {
            return null;
        }
        return str.trim().replaceAll("\\s*[\\n\\r]", "").replaceAll("[\\n\\r]", "");
    }

    public static boolean equals(Double v1, Double v2, double precision) {
        System.err.println("Equals-Double: " + v1 + ", " + v2 + ", " + precision);
        return (Math.abs(v2 - v1) < precision);
    }

    public static boolean equals(BigDecimal v1, BigDecimal v2, double precision) {
        System.err.println("Equals-BigDecimal: " + v1 + ", " + v2 + ", " + precision);
        return (Math.abs(v2.doubleValue() - v1.doubleValue()) < precision);
    }

    public static boolean equals(Float v1, Float v2, float precision) {
        return (Math.abs(v2 - v1) < precision);
    }

    public static boolean equals(Date v1, Date v2) {
        long precision = 24 * 3600 * 1000;
        System.err.println("Equals-Date: " + v1.getTime() + ", " + v2.getTime() + ", " + precision);
        return Math.abs(v1.getTime() - v2.getTime()) < precision;
    }

}
