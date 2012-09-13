package org.monstercraft.irc.ircplugin.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.ircplugin.IRCPlugin;
import org.monstercraft.irc.ircplugin.PluginManifest;

public class FilePluginSource implements IRCPluginSource {
    private final File[] files;

    public FilePluginSource(final File... file) {
        files = file;
    }

    @Override
    public LinkedList<IRCPluginDefinition> list() {
        final LinkedList<IRCPluginDefinition> defs = new LinkedList<IRCPluginDefinition>();
        for (final File file : files) {
            list(file, defs);
        }
        return defs;
    }

    private void list(final File file,
            final LinkedList<IRCPluginDefinition> defs) {
        if (file != null) {
            if (file.isDirectory()) {
                try {
                    final ClassLoader loader = new IRCPluginClassLoader(file
                            .toURI().toURL());
                    for (final File item : file.listFiles()) {
                        FilePluginSource.load(item, defs, loader);
                    }
                } catch (final IOException ignored) {
                }
            } else if (FilePluginSource.isJar(file)) {
                try {
                    final ClassLoader ldr = new IRCPluginClassLoader(
                            FilePluginSource.getJarUrl(file));
                    FilePluginSource.load(ldr, defs, new JarFile(file));
                } catch (final IOException ignored) {
                }
            }
        }
        for (final IRCPluginDefinition def : defs) {
            def.source = this;
        }
    }

    @Override
    public IRCPlugin load(final IRCPluginDefinition def)
            throws InstantiationException, IllegalAccessException {
        return def.clazz.asSubclass(IRCPlugin.class).newInstance();
    }

    public static void load(final File file,
            final LinkedList<IRCPluginDefinition> defs, ClassLoader loader)
            throws IOException {
        if (FilePluginSource.isJar(file)) {
            FilePluginSource.load(
                    new IRCPluginClassLoader(FilePluginSource.getJarUrl(file)),
                    defs, new JarFile(file));
        } else {
            if (loader == null) {
                loader = new IRCPluginClassLoader(file.getParentFile().toURI()
                        .toURL());
            }
            FilePluginSource.load(loader, defs, file, "");
        }
    }

    private static void load(final ClassLoader loader,
            final LinkedList<IRCPluginDefinition> plugins, final JarFile jar) {
        final Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            final JarEntry e = entries.nextElement();
            final String name = e.getName().replace('/', '.');
            final String ext = ".class";
            if (name.endsWith(ext) && !name.contains("$")) {
                FilePluginSource.load(loader, plugins,
                        name.substring(0, name.length() - ext.length()),
                        jar.getName());
            }
        }
    }

    private static void load(final ClassLoader loader,
            final LinkedList<IRCPluginDefinition> plugins, final File file,
            final String prefix) {
        if (file.isDirectory()) {
            if (!file.getName().startsWith(".")) {
                for (final File f : file.listFiles()) {
                    FilePluginSource.load(loader, plugins, f,
                            prefix + file.getName() + ".");
                }
            }
        } else {
            String name = prefix + file.getName();
            final String ext = ".class";
            if (name.endsWith(ext) && !name.startsWith(".")
                    && !name.contains("!") && !name.contains("$")) {
                name = name.substring(0, name.length() - ext.length());
                FilePluginSource.load(loader, plugins, name,
                        file.getAbsolutePath());
            }
        }
    }

    private static void load(final ClassLoader loader,
            final LinkedList<IRCPluginDefinition> plugins, final String name,
            final String path) {
        Class<?> clazz;
        try {
            clazz = loader.loadClass(name);
        } catch (final Exception e) {
            IRC.log(name + " is not a valid plugin and was ignored!");
            e.printStackTrace();
            return;
        } catch (final VerifyError e) {
            IRC.log(name + " is not a valid plugin and was ignored!");
            return;
        }
        if (clazz.isAnnotationPresent(PluginManifest.class)) {
            final IRCPluginDefinition def = new IRCPluginDefinition();
            final PluginManifest manifest = clazz
                    .getAnnotation(PluginManifest.class);
            def.id = 0;
            def.name = manifest.name();
            def.clazz = clazz;
            plugins.add(def);
        }
    }

    public static URL getJarUrl(final File file) throws IOException {
        URL url = file.toURI().toURL();
        url = new URL("jar:" + url.toExternalForm() + "!/");
        return url;
    }

    private static boolean isJar(final File file) {
        return file.getName().endsWith(".jar");
    }

}