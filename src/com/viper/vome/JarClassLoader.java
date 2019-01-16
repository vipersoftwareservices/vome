/*
 * -----------------------------------------------------------------------------
 *               VIPER SOFTWARE SERVICES
 * -----------------------------------------------------------------------------
 *
 * @(#)filename.java	1.00 2008/01/15
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
 * @version 1.0, 01/15/2008
 *
 * @note 
 *        
 * -----------------------------------------------------------------------------
 */

package com.viper.vome;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

public class JarClassLoader extends SecureClassLoader {

	/* The context to be used when loading classes and resources */
	private AccessControlContext acc;
	private URL urls[] = null;

	public JarClassLoader(URL url) {
		this(new URL[] { url }, ClassLoader.getSystemClassLoader());
	}

	public JarClassLoader(URL urls[], ClassLoader parent) {
		super(parent);

		this.urls = urls;
	}

	/**
	 * Locate the named class in a jar-file, contained inside the jar file which
	 * was used to load <u>this</u> class.
	 */
	protected Class findClass(final String name) throws ClassNotFoundException {

		// Make sure not to load duplicate classes.
		Class cls = findLoadedClass(name);
		if (cls != null)
			return cls;

		try {
			return AccessController.doPrivileged(new PrivilegedExceptionAction<Class>() {
				public Class run() throws ClassNotFoundException {
					String path = name.replace('.', '/').concat(".class");
					byte b[] = loadClassData(path);
					if (b == null) {
						throw new ClassNotFoundException(name);
					}
					return defineClass(name, b, 0, b.length);
				}
			}, acc);
		} catch (java.security.PrivilegedActionException pae) {
			throw (ClassNotFoundException) pae.getException();
		}
	}

	private byte[] loadClassData(String name) throws ClassNotFoundException {
		for (URL url : urls) {
			JarInputStream in = null;
			try {
				in = new JarInputStream(url.openStream());
				JarEntry entry = null;
				for (entry = in.getNextJarEntry(); entry != null; entry = in.getNextJarEntry()) {
					System.out.println("Compare: " + entry.getName() + " to " + name);
					if (entry.getName().equals(name)) {
						System.out.println("Extracting: " + entry.getName() + " to " + name);
						return readBytes(in);
					}
				}
				in.close();
			} catch (IOException ioe) {
				throw new ClassNotFoundException(name, ioe);
			}
		}
		return null;
	}

	private boolean isSealed(String name, Manifest man) {
		String path = name.replace('.', '/').concat("/");
		Attributes attr = man.getAttributes(path);
		String sealed = null;
		if (attr != null) {
			sealed = attr.getValue(Name.SEALED);
		}
		if (sealed == null) {
			if ((attr = man.getMainAttributes()) != null) {
				sealed = attr.getValue(Name.SEALED);
			}
		}
		return "true".equalsIgnoreCase(sealed);
	}

	/**
	 * Defines a new package by name in this ClassLoader. The attributes
	 * contained in the specified Manifest will be used to obtain package
	 * version and sealing information. For sealed packages, the additional URL
	 * specifies the code source URL from which the package was loaded.
	 * 
	 * @param name
	 *            the package name
	 * @param man
	 *            the Manifest containing package version and sealing
	 *            information
	 * @param url
	 *            the code source url for the package, or null if none
	 * @exception IllegalArgumentException
	 *                if the package name duplicates an existing package either
	 *                in this class loader or one of its ancestors
	 * @return the newly defined Package object
	 */
	protected Package definePackage(String name, Manifest man) throws IllegalArgumentException {
		String path = name.replace('.', '/').concat("/");
		String specTitle = null, specVersion = null, specVendor = null;
		String implTitle = null, implVersion = null, implVendor = null;
		String sealed = null;
		URL sealBase = null;

		Attributes attr = man.getAttributes(path);
		if (attr != null) {
			specTitle = attr.getValue(Name.SPECIFICATION_TITLE);
			specVersion = attr.getValue(Name.SPECIFICATION_VERSION);
			specVendor = attr.getValue(Name.SPECIFICATION_VENDOR);
			implTitle = attr.getValue(Name.IMPLEMENTATION_TITLE);
			implVersion = attr.getValue(Name.IMPLEMENTATION_VERSION);
			implVendor = attr.getValue(Name.IMPLEMENTATION_VENDOR);
			sealed = attr.getValue(Name.SEALED);
		}
		attr = man.getMainAttributes();
		if (attr != null) {
			if (specTitle == null) {
				specTitle = attr.getValue(Name.SPECIFICATION_TITLE);
			}
			if (specVersion == null) {
				specVersion = attr.getValue(Name.SPECIFICATION_VERSION);
			}
			if (specVendor == null) {
				specVendor = attr.getValue(Name.SPECIFICATION_VENDOR);
			}
			if (implTitle == null) {
				implTitle = attr.getValue(Name.IMPLEMENTATION_TITLE);
			}
			if (implVersion == null) {
				implVersion = attr.getValue(Name.IMPLEMENTATION_VERSION);
			}
			if (implVendor == null) {
				implVendor = attr.getValue(Name.IMPLEMENTATION_VENDOR);
			}
			if (sealed == null) {
				sealed = attr.getValue(Name.SEALED);
			}
		}
		if (sealed != null) {
			try {
				sealBase = new URL(sealed);
			} catch (MalformedURLException mux) {
				// Would use IllegalArgumentException, but it don't have the
				// chained constructor.
				throw new RuntimeException("Error in " + Name.SEALED + " manifest attribute: " + sealed, mux);
			}
		}
		return definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
	}

	/**
	 * @param in
	 * @return
	 * @throws IOException
	 */

	private byte[] readBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
		int n;
		while ((n = in.read()) != -1) {
			out.write(n);
		}
		out.flush();
		out.close();
		in.close();

		return out.toByteArray();
	}
}