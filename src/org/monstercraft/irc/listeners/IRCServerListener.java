package org.monstercraft.irc.listeners;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.hooks.HeroChatHook;
import org.monstercraft.irc.hooks.PermissionsHook;
import org.monstercraft.irc.hooks.mcMMOHook;

public class IRCServerListener extends ServerListener {

	private IRC plugin;

	public IRCServerListener(IRC plugin) {
		this.plugin = plugin;
	}

	public void onPluginEnable(PluginEnableEvent event) {
		String PluginName = event.getPlugin().getDescription().getName();
		if (plugin != null) {
			if (PluginName.equals("Permissions")) {
				plugin.permissions = new PermissionsHook(plugin);
			} else if (PluginName.equals("mcMMO")) {
				plugin.mcmmo = new mcMMOHook(plugin);
			}else if (PluginName.equals("HeroChat")) {
				plugin.herochat = new HeroChatHook(plugin);
			}
		}
	}
}