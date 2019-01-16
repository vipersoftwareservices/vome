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

package com.viper.vome.beans;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class JAXBUtils {

    private static final int AVERAGE_MESSAGE_SIZE = 2048;
    private static final TransformerFactory XformFactory = TransformerFactory.newInstance();
    static {
        XformFactory.setURIResolver(new XsltResolver());
    }

    private static final Hashtable<String, JAXBContext> contextStore = new Hashtable<String, JAXBContext>();
    private static Transformer identityTransformer = null;

    private JAXBUtils() {
    }

    public static JAXBUtils getInstance() {
        return new JAXBUtils();
    }

   

    public static Document newDocument() throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.newDocument();
    }

    public static String translate(Source source, String xsl) throws Exception {
        return translate(source, readSource(xsl));
    }

    public static String translate(Node node, String xsl) throws Exception {
        return translate(new DOMSource(node), readSource(xsl));
    }

    public static String translate(Source source, Source xslSource) {
        return translate(source, xslSource, null);
    }

    public static String translate(Source source, Source xslSource, Map<String, String> params) {
        try {
            StringWriter stringWriter = new StringWriter(AVERAGE_MESSAGE_SIZE);
            Result result = new StreamResult(stringWriter);
            Transformer xform = XformFactory.newTransformer(xslSource);
            if (params != null) {
                for (String name : params.keySet()) {
                    xform.setParameter(name, params.get(name));
                }
            }
            xform.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readString(Node node) throws Exception {
        return readString(new DOMSource(node));
    }

    public static String readString(Source source) throws Exception {
        StringWriter stringWriter = new StringWriter(AVERAGE_MESSAGE_SIZE);
        if (identityTransformer == null) {
            identityTransformer = TransformerFactory.newInstance().newTransformer();
            identityTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        identityTransformer.transform(source, new StreamResult(stringWriter));

        stringWriter.close();
        return stringWriter.getBuffer().toString();
    }

    public static String readString(List<Object> any) throws Exception {
        StringBuilder buf = new StringBuilder();
        for (Object o : any) {
            if (o instanceof Node) {
                buf.append(readString(new DOMSource((Node) o)));

            } else if (o instanceof String) {
                buf.append((String) o);

            } else if (o instanceof JAXBElement) {
                JAXBElement element = JAXBElement.class.cast(o);
                if (element.getValue() instanceof Node) {
                    buf.append(readString((Node) element.getValue()));
                } else {
                    buf.append("<" + element.getValue() + "/>");
                }

            } else {
                buf.append(getString(o, null));
            }
        }
        return buf.toString();
    }

    public static Source readSource(String str) throws Exception {
        return new StreamSource(new java.io.StringReader(str));
    }

    // -------------------------------------------------------------------------
    public static <T> T getObject(Class<T> clazz, Source source) throws Exception {
        return getJAXBContext(clazz).createUnmarshaller().unmarshal(source, clazz).getValue();
    }

    public static <T> T getObject(Class<T> clazz, String str) throws Exception {
        return getObject(clazz, readSource(str));
    }

    public static <T> T getObject(Class<T> clazz, File file) throws Exception {
        return getObject(clazz, new StreamSource(file));
    }

    public static <T> T unmarshal(Class<T> clazz, Reader reader) throws Exception {
        return getObject(clazz, new StreamSource(reader));
    }

    public static <T> T unmarshal(Class<T> clazz, InputStream inputstream) throws Exception {
        return getObject(clazz, new StreamSource(inputstream));
    }

    public static <T> T unmarshal(Class<T> clazz, File file) throws Exception {
        return getObject(clazz, new StreamSource(file));
    }

    // -------------------------------------------------------------------------

    public static <T> Source getSource(T bean, Map<String, Object> properties) throws Exception {
        return new StreamSource(new StringReader(getString(bean, properties)));
    }

    public static <T> String getString(T bean, Map<String, Object> properties) throws Exception {
        if (bean == null) {
            throw new Exception("Passed object is null");
        }
        StringWriter out = new StringWriter();
        marshal(out, bean, properties);
        return out.toString();
    }

    public static <T> void marshal(Writer writer, T bean, Map<String, Object> properties) throws Exception {
        if (bean == null) {
            throw new Exception("Passed object is null");
        }
        createMarshaller(bean.getClass(), properties).marshal(bean, writer);
        writer.flush();
    }

    public static <T> void marshal(File file, T bean, Map<String, Object> properties) throws Exception {
        if (bean == null) {
            throw new Exception("Passed object is null");
        }
        createMarshaller(bean.getClass(), properties).marshal(bean, file);
    }

    /**
     * This function to return a url encode value
     * 
     * @param str
     * @return
     * @throws JAXBException
     */
    public static String encode(String str) throws Exception {
        return URLEncoder.encode(str.trim(), "UTF-8");
    }

    // -------------------------------------------------------------------------
    static class XsltResolver implements URIResolver {
        public XsltResolver() {

        }

        public Source resolve(String href, String base) {
            if (href.startsWith("res:")) {
                return new StreamSource(getClass().getResourceAsStream(href.substring(4)));
            }
            File file = new File(base + href);
            return (file.exists()) ? new StreamSource(file) : null;
        }
    }

    // -------------------------------------------------------------------------

    public static <T> Marshaller createMarshaller(Class<T> clazz, Map<String, Object> properties) throws Exception {

        Marshaller m = getJAXBContext(clazz).createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        if (properties != null) {
            for (String name : properties.keySet()) {
                m.setProperty(name, properties.get(name));
            }
        }
        return m;
    }

    public static <T> JAXBContext getJAXBContext(Class<T> clazz) throws Exception {
        String packagename = clazz.getPackage().getName();
        JAXBContext context = contextStore.get(packagename);
        if (context != null) {
            return context;
        }
        String classname = clazz.getName();
        context = contextStore.get(classname);
        if (context != null) {
            return context;
        }
        if (classExists(packagename + ".ObjectFactory")) {
            context = JAXBContext.newInstance(packagename);
            contextStore.put(packagename, context);
        } else {
            context = JAXBContext.newInstance(clazz);
            contextStore.put(classname, context);
        }
        if (context == null) {
            throw new Exception("No JAXBContext for package " + classname);
        }
        return context;
    }

    public static boolean classExists(String classname) {
        try {
            return (Class.forName(classname) != null);
        } catch (Exception ex) {
            return false;
        }
    }

    public static void clearFactoryStore() {
        contextStore.clear();
    }
}
