package org.monstercraft.irc.plugin.managers.hooks;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;

import com.gmail.nossr50.mcMMO;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class mcMMOHook extends MonsterIRC {

	private mcMMO mcMMOHook;
	private MonsterIRC plugin;

	/**
	 * Creates a hook into mcmmo.
	 * 
	 * @param plugin
	 *            The IRC plugin.
	 */
	public mcMMOHook(final MonsterIRC plugin) {
		this.plugin = plugin;
		initmcMMOHook();
	}

	/**
	 * Hooks into mcmmo.
	 */
	private void initmcMMOHook() {
		if (mcMMOHook != null) {
			return;
		}
		Plugin mcMMOPlugin = plugin.getServer().getPluginManager()
				.getPlugin("mcMMO");

		if (mcMMOPlugin == null) {
			IRC.log("mcMMO not detected.");
			mcMMOHook = null;
			return;
		}

		if (!mcMMOPlugin.isEnabled()) {
			IRC.log("mcMMO not enabled.");
			mcMMOHook = null;
			return;
		}

		mcMMOHook = ((mcMMO) mcMMOPlugin);
		IRC.log("mcMMO detected; hooking: "
				+ ((mcMMO) mcMMOPlugin).getDescription().getFullName());
	}

	/**
	 * Fetches the hook into mcMMO.
	 * 
	 * @return the hook into mcMMO.
	 */
	public mcMMO getHook() {
		return mcMMOHook;
	}

}
