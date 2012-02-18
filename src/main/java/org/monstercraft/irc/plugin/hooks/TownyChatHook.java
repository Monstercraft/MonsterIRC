package org.monstercraft.irc.plugin.hooks;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.util.Variables;

import com.herocraftonline.dthielke.herochat.HeroChat;
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
				.getPlugin("HeroChat");

		if (TownyChatPlugin == null) {
			log("HeroChat 4 not detected.");
			TownyChatHook = null;
			return;
		}

		if (!TownyChatPlugin.isEnabled()) {
			log("HeroChat 4 not enabled.");
			TownyChatHook = null;
			return;
		}

		TownyChatHook = ((Chat) TownyChatPlugin);
		log("HeroChat detected; hooking: "
				+ ((HeroChat) TownyChatPlugin).getDescription().getFullName());
		String ver = ((HeroChat) TownyChatPlugin).getDescription().getVersion();
		if (!ver.startsWith("5")) {
			Variables.hc4 = true;
		}
	}

	/**
	 * Fetches the hook into HeroChat.
	 * 
	 * @return the hook into HeroChat.
	 */
	public Chat getHook() {
		return TownyChatHook;
	}

}