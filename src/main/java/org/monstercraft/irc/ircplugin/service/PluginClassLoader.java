package org.monstercraft.irc.ircplugin.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class PluginClassLoader extends ClassLoader {

	private final URL base;

	public PluginClassLoader(URL url) {
		this.base = url;
	}

	@Override
	public Class<?> loadClass(final String name, final boolean resolve)
			throws ClassNotFoundException {
		byte bytes[];
		Class<?> clazz = null;
		clazz = findLoadedClass(name);
		if (clazz == null) {
			try {
				InputStream is = getResourceAsStream(name.replace('.',
						File.separatorChar) + ".class");
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				int nextValue = is.read();
				while (-1 != nextValue) {
					byteStream.write(nextValue);
					nextValue = is.read();
				}
				bytes = byteStream.toByteArray();
				clazz = defineClass(name, bytes, 0, bytes.length);
				if (resolve) {
					resolveClass(clazz);
				}
			} catch (final Exception e) {
				clazz = super.loadClass(name, resolve);
			}
		}

		return clazz;
	}

	@Override
	public URL getResource(String name) {
		try {
			return new URL(base, name);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		try {
			return new URL(base, name).openStream();
		} catch (IOException e) {
			return null;
		}
	}

}
