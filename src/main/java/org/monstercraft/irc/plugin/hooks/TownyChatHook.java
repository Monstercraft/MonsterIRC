package org.monstercraft.irc.plugin.hooks;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.IRC;

import com.palmergames.bukkit.TownyChat.Chat;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class TownyChatHook extends IRC {

	private Chat TownyChatHook;
	private IRC plugin;

	/**
	 * Creates an instance of the TownyChatHook class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public TownyChatHook(final IRC plugin) {
		this.plugin = plugin;
		initTownyChatHook();
	}

	private void initTownyChatHook() {
		if (TownyChatHook != null) {
			return;
		}
		Plugin TownyChatPlugin = plugin.getServer().getPluginManager()
				.getPlugin("TownyChat");

		if (TownyChatPlugin == null) {
			log("TownyChat not detected.");
			TownyChatHook = null;
			return;
		}

		if (!TownyChatPlugin.isEnabled()) {
			log("HeroChat 4 not enabled.");
			TownyChatHook = null;
			return;
		}

		TownyChatHook = ((Chat) TownyChatPlugin);
		log("TownyChat detected; hooking: "
				+ ((Chat) TownyChatPlugin).getDescription().getFullName());
	}

	/**
	 * Fetches the hook into TownyChat.
	 * 
	 * @return The hook into TownyChat.
	 */
	public Chat getHook() {
		return TownyChatHook;
	}

}