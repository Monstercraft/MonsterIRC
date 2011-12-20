package org.monstercraft.irc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.handlers.IRCHandler;
import org.monstercraft.irc.util.Variables;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.channels.Channel;

public class IRCPlayerListener extends PlayerListener {
	private HeroChat hook;
	private IRC plugin;

	public IRCPlayerListener(IRC plugin) {
		this.plugin = plugin;
		initHeroChatHook();
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		try {
			Player player = event.getPlayer();
			for (Channel c : hook.getChannelManager().getChannels()) {
				if (c.getName().equals(Variables.hc)) {
					if (c.getPlayers().contains(player.getName())) {
						System.out.print(hook.getChannelManager()
								.getActiveChannel(Variables.hc).getName());
						StringBuffer result = new StringBuffer();
						result.append("<" + player.getName() + ">" + ":");
						result.append(event.getMessage());
						IRCHandler.sendMessage(result.toString());
						System.out.print(player.getDisplayName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initHeroChatHook() {
		if (hook != null) {
			return;
		}
		Plugin HeroChatPlugin = plugin.getServer().getPluginManager()
				.getPlugin("HeroChat");

		if (HeroChatPlugin == null) {
			System.out.println("[IRC] HeroChat not detected.");
			return;
		}

		hook = ((HeroChat) HeroChatPlugin);
		System.out.println("[IRC] HeroChat detected; hooking: "
				+ ((HeroChat) HeroChatPlugin).getDescription().getFullName());
	}
}
