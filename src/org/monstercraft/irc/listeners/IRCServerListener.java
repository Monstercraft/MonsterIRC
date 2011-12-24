package org.monstercraft.irc.listeners;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.monstercraft.irc.IRC;

public class IRCServerListener extends ServerListener {

	private IRC plugin;

	public IRCServerListener(IRC plugin) {
		this.plugin = plugin;
	}

	public void onPluginEnable(PluginEnableEvent event) {
		String PluginName = event.getPlugin().getDescription().getName();
		if (this.plugin != null) {
			if (PluginName.equals("Permissions")) {
				this.plugin.detectPermissions();
			}
		}
	}
}