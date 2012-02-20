package org.monstercraft.irc.ircplugin.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.monstercraft.irc.ircplugin.IRCPlugin;
import org.monstercraft.irc.ircplugin.PluginManifest;
import org.monstercraft.irc.ircplugin.util.Methods;

public class FilePluginSource implements IRCPluginSource {

	private File file;

	public FilePluginSource(File file) {
		this.file = file;
	}

	@Override
	public List<IRCPluginDefinition> list() {
		LinkedList<IRCPluginDefinition> defs = new LinkedList<IRCPluginDefinition>();
		if (file != null) {
			if (file.isDirectory()) {
				try {
					final ClassLoader ldr = new IRCPluginClassLoader(file
							.toURI().toURL());
					for (final File f : file.listFiles()) {
						if (isJar(f)) {
							load(new IRCPluginClassLoader(getJarUrl(f)), defs,
									new JarFile(f));
						} else {
							load(ldr, defs, f, "");
						}
					}
				} catch (final IOException ignored) {
				}
			} else if (isJar(file)) {
				try {
					final ClassLoader ldr = new IRCPluginClassLoader(
							getJarUrl(file));
					load(ldr, defs, new JarFile(file));
				} catch (final IOException ignored) {
				}
			}
		}
		return defs;
	}

	@Override
	public IRCPlugin load(IRCPluginDefinition def) {
		if (!(def instanceof IRCPluginDefinition)) {
			throw new IllegalArgumentException("Invalid definition!");
		}
		try {
			if (IRCPlugin.class.isAssignableFrom(def.clazz)) {
				return def.clazz.asSubclass(IRCPlugin.class).newInstance();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private void load(ClassLoader loader,
			LinkedList<IRCPluginDefinition> plugins, JarFile jar) {
		try {
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				final JarEntry e = entries.nextElement();
				final String name = e.getName()
						.replace(File.separatorChar, '.');
				final String ext = ".class";
				if (name.endsWith(ext) && !name.contains("$")) {
					load(loader, plugins,
							name.substring(0, name.length() - ext.length()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void load(final ClassLoader loader,
			final LinkedList<IRCPluginDefinition> Plugins, final File file,
			final String prefix) {
		if (file.isDirectory()) {
			if (!file.getName().startsWith(".")) {
				for (final File f : file.listFiles()) {
					load(loader, Plugins, f, prefix + file.getName() + ".");
				}
			}
		} else {
			String name = prefix + file.getName();
			final String ext = ".class";
			if (name.endsWith(ext) && !name.startsWith(".")
					&& !name.contains("!") && !name.contains("$")) {
				name = name.substring(0, name.length() - ext.length());
				load(loader, Plugins, name);
			}
		}
	}

	private void load(ClassLoader loader,
			LinkedList<IRCPluginDefinition> Plugins, String name) {
		Class<?> clazz;
		try {
			clazz = loader.loadClass(name);
		} catch (Exception e) {
			Methods.log(name + " is not a valid Plugin and was ignored!");
			e.printStackTrace();
			return;
		} catch (VerifyError e) {
			Methods.log(name + " is not a valid Plugin and was ignored!");
			return;
		}
		if (clazz.isAnnotationPresent(PluginManifest.class)) {
			IRCPluginDefinition def = new IRCPluginDefinition();
			PluginManifest manifest = clazz.getAnnotation(PluginManifest.class);
			def.name = manifest.name();
			def.id = 0;
			def.clazz = clazz;
			def.source = this;
			Plugins.add(def);
		}
	}

	private boolean isJar(File file) {
		return file.getName().endsWith(".jar")
				|| file.getName().endsWith(".dat");
	}

	private URL getJarUrl(File file) throws IOException {
		return new URL("jar:" + file.toURI().toURL().toExternalForm() + "!/");
	}

}
