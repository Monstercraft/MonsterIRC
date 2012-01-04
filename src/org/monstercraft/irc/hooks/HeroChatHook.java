package org.monstercraft.irc.hooks;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.IRC;

import com.herocraftonline.dthielke.herochat.HeroChat;

public class HeroChatHook {
	
	public HeroChat HeroChatHook;
	private IRC plugin;
	
	public HeroChatHook(IRC plugin) {
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
			plugin.log("HeroChat not detected.");
			HeroChatHook = null;
			return;
		}

		HeroChatHook = ((HeroChat) HeroChatPlugin);
		plugin.log("HeroChat detected; hooking: "
				+ ((HeroChat) HeroChatPlugin).getDescription().getFullName());
	}

}
