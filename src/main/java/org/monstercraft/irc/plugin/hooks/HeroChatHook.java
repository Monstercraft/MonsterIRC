package org.monstercraft.irc.plugin.hooks;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.util.Variables;

import com.herocraftonline.dthielke.herochat.HeroChat;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class HeroChatHook extends IRC {

	private HeroChat HeroChatHook;
	private IRC plugin;

	/**
	 * Creates an instance of the HeroChatHook class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public HeroChatHook(final IRC plugin) {
		this.plugin = plugin;
		initHeroChatHook();
	}

	private void initHeroChatHook() {
		if (HeroChatHook != null) {
			return;
		}
		Plugin HeroChatPlugin = plugin.getServer().getPluginManager()
				.getPlugin("HeroChat");

		if (HeroChatPlugin == null) {
			log("HeroChat 4 not detected.");
			HeroChatHook = null;
			return;
		}

		if (!HeroChatPlugin.isEnabled()) {
			log("HeroChat 4 not enabled.");
			HeroChatHook = null;
			return;
		}

		HeroChatHook = ((HeroChat) HeroChatPlugin);
		log("HeroChat detected; hooking: "
				+ ((HeroChat) HeroChatPlugin).getDescription().getFullName());
		String ver = ((HeroChat) HeroChatPlugin).getDescription().getVersion();
		if (!ver.startsWith("5")) {
			Variables.hc4 = true;
		}
	}

	/**
	 * Fetches the hook into HeroChat.
	 * 
	 * @return the hook into HeroChat.
	 */
	public HeroChat getHook() {
		return HeroChatHook;
	}

}