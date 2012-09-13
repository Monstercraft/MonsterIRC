package org.monstercraft.irc.plugin.handles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.ircplugin.IRCPlugin;
import org.monstercraft.irc.ircplugin.PluginManifest;
import org.monstercraft.irc.ircplugin.service.FilePluginSource;
import org.monstercraft.irc.ircplugin.service.IRCPluginDefinition;
import org.monstercraft.irc.plugin.Configuration;

public class IRCPluginHandler extends MonsterIRC {

    private final HashMap<Integer, IRCPlugin> pluginsToRun = new HashMap<Integer, IRCPlugin>();
    private final HashMap<Integer, Thread> pluginThreads = new HashMap<Integer, Thread>();
    private final List<IRCPluginDefinition> plugins;

    public IRCPluginHandler(final MonsterIRC plugin) {
        plugins = new ArrayList<IRCPluginDefinition>();
        plugins.addAll(new FilePluginSource(IRCPluginHandler.getPluginsFolder())
                .list());
        for (final IRCPluginDefinition def : plugins) {
            try {
                IRC.log("Loading IRC plugin " + def.name + ".");
                runplugin(def.source.load(def));
            } catch (final Exception e) {
                IRC.debug(e);
            }
        }

    }

    private static File getPluginsFolder() {
        return new File(Configuration.Paths.PLUGINS);
    }

    public void runplugin(final IRCPlugin plugin) {
        final PluginManifest prop = plugin.getClass().getAnnotation(
                PluginManifest.class);
        final Thread t = new Thread(plugin, "plugin-" + prop.name());
        addpluginToPool(plugin, t);
        t.start();
    }

    private void addpluginToPool(final IRCPlugin plugin, final Thread t) {
        plugin.setID(pluginsToRun.size());
        pluginsToRun.put(pluginsToRun.size(), plugin);
        pluginThreads.put(pluginThreads.size(), t);
    }

    public void stopPlugins() {
        final Thread curThread = Thread.currentThread();
        for (int i = 0; i < pluginsToRun.size(); i++) {
            final IRCPlugin plugin = pluginsToRun.get(i);
            if (plugin != null && plugin.isActive()) {
                if (pluginThreads.get(i) == curThread) {
                    stopPlugin(i);
                }
            }
        }
        if (curThread == null) {
            throw new ThreadDeath();
        }
    }

    public void stopPlugin(final int id) {
        final IRCPlugin plugin = pluginsToRun.get(id);
        if (plugin != null) {
            plugin.deactivate(id);
            pluginsToRun.remove(id);
            pluginThreads.remove(id);
        }
    }
}
