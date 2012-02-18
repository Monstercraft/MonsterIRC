package org.monstercraft.irc.plugin.hooks;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.IRC;

import com.gmail.nossr50.mcMMO;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class mcMMOHook extends IRC {

	private mcMMO mcMMOHook;
	private IRC plugin;

	/**
	 * Creates a hook into mcmmo.
	 * 
	 * @param plugin
	 *            The IRC plugin.
	 */
	public mcMMOHook(final IRC plugin) {
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
			log("mcMMO not detected.");
			mcMMOHook = null;
			return;
		}

		if (!mcMMOPlugin.isEnabled()) {
			log("mcMMO not enabled.");
			mcMMOHook = null;
			return;
		}

		mcMMOHook = ((mcMMO) mcMMOPlugin);
		log("mcMMO detected; hooking: "
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
