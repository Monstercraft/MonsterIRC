package org.monstercraft.irc.listeners;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.hooks.HeroChatHook;
import org.monstercraft.irc.hooks.PermissionsHook;
import org.monstercraft.irc.hooks.mcMMOHook;

/**
 * This class listens for certain plugins so we are able to hook into them.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRCServerListener extends ServerListener {

	private IRC plugin;

	/**
	 * Creates an instance of the IRCServerListener class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public IRCServerListener(final IRC plugin) {
		this.plugin = plugin;
	}

	public void onPluginEnable(PluginEnableEvent event) {
		String PluginName = event.getPlugin().getDescription().getName();
		if (plugin != null) {
			if (PluginName.equals("Permissions")) {
				IRC.getHookManager().setPermissionsHook(
						new PermissionsHook(plugin));
				IRC.getHandleManager().setPermissionsHandler(
						IRC.getHookManager().getPermissionsHook());
			} else if (PluginName.equals("mcMMO")) {
				IRC.getHookManager().setmcMMOHook(new mcMMOHook(plugin));
			} else if (PluginName.equals("HeroChat")) {
				IRC.getHookManager().setHeroChatHook(
						new HeroChatHook(plugin));
			}
		}
	}
}