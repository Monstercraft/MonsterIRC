package org.monstercraft.irc.plugin.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.ircplugin.IRCPlugin;
import org.monstercraft.irc.ircplugin.PluginManifest;
import org.monstercraft.irc.ircplugin.service.FilePluginSource;
import org.monstercraft.irc.ircplugin.service.PluginDefinition;
import org.monstercraft.irc.plugin.util.Constants;

public class IRCPluginHandler extends IRC {

	private final HashMap<Integer, IRCPlugin> pluginsToRun = new HashMap<Integer, IRCPlugin>();
	private final HashMap<Integer, Thread> pluginThreads = new HashMap<Integer, Thread>();
	private final List<PluginDefinition> plugins;

	public IRCPluginHandler(IRC plugin) {
		this.plugins = new ArrayList<PluginDefinition>();
		plugins.addAll(new FilePluginSource(getPluginsFolder()).list());
		for (PluginDefinition def : plugins) {
			try {
				log("Loading IRC plugin " + def.name + ".");
				runplugin(def.source.load(def));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static File getPluginsFolder() {
		return new File(Constants.SETTINGS_PATH + File.separator
				+ Constants.PLUGINS_FOLDER);
	}

	public void runplugin(IRCPlugin plugin) {
		PluginManifest prop = plugin.getClass().getAnnotation(
				PluginManifest.class);
		Thread t = new Thread(plugin, "plugin-" + prop.name());
		addpluginToPool(plugin, t);
		t.start();
	}

	private void addpluginToPool(IRCPlugin plugin, Thread t) {
		plugin.setID(pluginsToRun.size());
		pluginsToRun.put(pluginsToRun.size(), plugin);
		pluginThreads.put(pluginThreads.size(), t);
	}

	public void stopplugin() {
		Thread curThread = Thread.currentThread();
		for (int i = 0; i < pluginsToRun.size(); i++) {
			IRCPlugin plugin = pluginsToRun.get(i);
			if (plugin != null && plugin.isActive()) {
				if (pluginThreads.get(i) == curThread) {
					stopPlugin(i);
					getEventManager().removeListener(plugin);
				}
			}
		}
		if (curThread == null) {
			throw new ThreadDeath();
		}
	}

	public void stopPlugin(int id) {
		IRCPlugin plugin = pluginsToRun.get(id);
		if (plugin != null) {
			plugin.deactivate(id);
			pluginsToRun.remove(id);
			pluginThreads.remove(id);
		}
	}
}
