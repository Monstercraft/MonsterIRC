package org.monstercraft.irc.chat;

//added for herochat hooking
import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.IRC;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.channels.Channel;
import com.herocraftonline.dthielke.herochat.channels.ChannelManager;

public class hChat {

	private IRC plugin;
	public static HeroChat HeroChatHook;

	public hChat(IRC plugin) {
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
			System.out.println("[IRC] HeroChat not detected not hooking");
			return;
		}

		HeroChatHook = ((HeroChat) HeroChatPlugin);
		System.out.println("[IRC]HeroChat detected; hooking: "
				+ HeroChatPlugin.getDescription().getFullName());
	}

	public Channel getChannel(String channel) {
		ChannelManager cm = HeroChatHook.getChannelManager();
		return cm.getChannel(channel);
	}
}
