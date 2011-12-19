package org.monstercraft.irc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.handlers.IRCHandler;

public class IRCPlayerListener extends PlayerListener {

	public IRCPlayerListener(IRC plugin) {
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}
		try {
			Player player = event.getPlayer();
			StringBuffer result = new StringBuffer();
			result.append("<" + player.getName() + ">" + ":");
			result.append(event.getMessage());
			IRCHandler.sendMessage(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
