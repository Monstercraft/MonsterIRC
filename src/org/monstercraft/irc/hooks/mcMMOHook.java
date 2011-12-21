package org.monstercraft.irc.hooks;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.IRC;

import com.gmail.nossr50.mcMMO;

public class mcMMOHook {
	
	public mcMMO mcMMOHook;
	private IRC plugin;
	
	public mcMMOHook(IRC plugin) {
		this.plugin = plugin;
		initmcMMOHook();
	}
	
	private void initmcMMOHook() {
		if (mcMMOHook != null) {
			return;
		}
		Plugin mcMMOPlugin = plugin.getServer().getPluginManager()
				.getPlugin("mcMMO");

		if (mcMMOPlugin == null) {
			System.out.println("[IRC] mcMMO not detected.");
			return;
		}

		mcMMOHook = ((mcMMO) mcMMOPlugin);
		System.out.println("[IRC] mcMMO detected; hooking: "
				+ ((mcMMO) mcMMOPlugin).getDescription().getFullName());
	}

}
