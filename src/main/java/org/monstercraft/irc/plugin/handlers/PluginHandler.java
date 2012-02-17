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

public class PluginHandler extends IRC {

	private final HashMap<Integer, IRCPlugin> pluginsToRun = new HashMap<Integer, IRCPlugin>();
	private final HashMap<Integer, Thread> scriptThreads = new HashMap<Integer, Thread>();
	private final List<PluginDefinition> plugins;

	public PluginHandler() {
		this.plugins = new ArrayList<PluginDefinition>();
		plugins.addAll(new FilePluginSource(getPluginsFolder()).list());
		for (PluginDefinition def : plugins) {
			try {
				log("Starting IRC plugin " + def.name);
				runScript(def.source.load(def));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static File getPluginsFolder() {
		return new File(Constants.SETTINGS_PATH + File.separator
				+ Constants.PLUGINS_FOLDER);
	}

	public void runScript(IRCPlugin plugin) {
		PluginManifest prop = plugin.getClass().getAnnotation(
				PluginManifest.class);
		Thread t = new Thread(plugin, "Script-" + prop.name());
		addScriptToPool(plugin, t);
		t.start();
	}

	private void addScriptToPool(IRCPlugin plugin, Thread t) {
		plugin.setID(pluginsToRun.size());
		pluginsToRun.put(pluginsToRun.size(), plugin);
		scriptThreads.put(scriptThreads.size(), t);
	}

	public void stopScript() {
		Thread curThread = Thread.currentThread();
		for (int i = 0; i < pluginsToRun.size(); i++) {
			IRCPlugin script = pluginsToRun.get(i);
			if (script != null && script.isActive()) {
				if (scriptThreads.get(i) == curThread) {
					stopPlugin(i);
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
			scriptThreads.remove(id);
		}
	}

}
