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
		if (event.isCancelled()) {
			return;
		}
		try {
			Player player = event.getPlayer();
			if (this.plugin.HeroChat.getChannel(Variables.hc) != null) {
				if (this.plugin.HeroChat.getChannel(Variables.hc).getPlayers()
						.contains(player.getName())) {
					StringBuffer result = new StringBuffer();
					result.append("<" + player.getName() + ">" + ":");
					result.append(event.getMessage());
					plugin.handle.sendMessage(result.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
