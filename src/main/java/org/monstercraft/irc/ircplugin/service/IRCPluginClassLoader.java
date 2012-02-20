package org.monstercraft.irc.ircplugin.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.monstercraft.irc.ircplugin.IRCPlugin;
import org.monstercraft.irc.ircplugin.PluginManifest;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;

public class IRCPluginClassLoader extends ClassLoader {

	private final URL base;

	public IRCPluginClassLoader(URL url) {
		this.base = url;
	}

	public Class<?> loadClass(final String name, final boolean resolve)
			throws ClassNotFoundException {
		Class<?> clazz = findLoadedClass(name);
		if (clazz == null && name.contains("org.monstercraft.irc")) {
			if (name.equalsIgnoreCase("org.monstercraft.irc.ircplugin.event.listeners.irclistener")) {
				clazz = IRCListener.class;
			} else if (name
					.equalsIgnoreCase("org.monstercraft.irc.ircplugin.ircplugin")) {
				clazz = IRCPlugin.class;
			} else if (name
					.equalsIgnoreCase("org.monstercraft.irc.ircplugin.PluginManifest")) {
				clazz = PluginManifest.class;
			}
			return clazz;
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