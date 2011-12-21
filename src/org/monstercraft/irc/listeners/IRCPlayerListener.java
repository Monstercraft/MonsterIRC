package org.monstercraft.irc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.util.Variables;

public class IRCPlayerListener extends PlayerListener {
	private IRC plugin;

	public IRCPlayerListener(IRC plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		try {
			Player player = event.getPlayer();
			if (!plugin.mcmmo.mcMMOHook.getPlayerProfile(player)
					.getAdminChatMode()) {
				if (plugin.herochat.HeroChatHook.getChannelManager()
						.getActiveChannel(player.getName()).getName()
						.equals(Variables.hc)) {
					StringBuffer result = new StringBuffer();
					result.append("<" + player.getName() + ">" + " ");
					result.append(event.getMessage());
					plugin.IRC.sendMessage(result.toString());
					System.out.print(player.getDisplayName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
