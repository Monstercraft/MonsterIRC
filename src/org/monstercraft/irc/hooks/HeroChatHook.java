package org.monstercraft.irc.hooks;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.IRC;

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
			log("HeroChat not detected.");
			HeroChatHook = null;
			return;
		}

		HeroChatHook = ((HeroChat) HeroChatPlugin);
		log("HeroChat detected; hooking: "
				+ ((HeroChat) HeroChatPlugin).getDescription().getFullName());
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
