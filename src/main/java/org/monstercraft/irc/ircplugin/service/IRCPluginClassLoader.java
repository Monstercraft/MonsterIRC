package org.monstercraft.irc.ircplugin.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class IRCPluginClassLoader extends ClassLoader {

	private final URL base;

	public IRCPluginClassLoader(URL url) {
		this.base = url;
	}

	public Class<?> loadClass(final String name, final boolean resolve)
			throws ClassNotFoundException {
		Class<?> clazz = findLoadedClass(name);
		if (clazz == null) {
			try {
				clazz = Class.forName(name);
				return clazz;
			} catch (ClassNotFoundException e) {
				clazz = null;
			}
		}
		if (clazz == null) {
			try {
				InputStream in = getResourceAsStream(name.replace('.', '/')
						+ ".class");
				byte[] buffer = new byte[4096];
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int n;
				while ((n = in.read(buffer, 0, 4096)) != -1) {
					out.write(buffer, 0, n);
				}
				byte[] bytes = out.toByteArray();
				clazz = defineClass(name, bytes, 0, bytes.length);
				if (resolve) {
					resolveClass(clazz);
				}
			} catch (Exception e) {
				if (clazz == null) {
					clazz = findSystemClass(name);
				}
				if (clazz == null) {
					System.out.println("Clazz is still null " + name);
					super.loadClass(name, resolve);
				}
			}
		}

		return clazz;
	}

	public URL getResource(String name) {
		try {
			return new URL(base, name);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public InputStream getResourceAsStream(String name) {
		try {
			return new URL(base, name).openStream();
		} catch (IOException e) {
			return null;
		}
	}

}