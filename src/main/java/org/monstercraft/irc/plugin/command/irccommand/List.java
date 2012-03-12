package org.monstercraft.irc.plugin.command.irccommand;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class List extends IRCCommand {

	@Override
	public boolean canExecute(String sender, String message, IRCChannel channel) {
		return MonsterIRC.getHandleManager().getIRCHandler()
				.isConnected(MonsterIRC.getIRCServer())
				&& IRC.isVoicePlus(channel, sender)
				&& message.toLowerCase().startsWith(
						Variables.commandPrefix + "list");
	}

	@Override
	public boolean execute(String sender, String message, IRCChannel channel) {
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		StringBuilder sb = new StringBuilder();
		sb.append("Players currently online" + "(" + players.length + "/"
				+ Bukkit.getServer().getMaxPlayers() + "): ");
		for (int i = 0; i < players.length; i++) {
			if (i == players.length - 1) {
				sb.append(players[i].getName());
			} else {
				sb.append(players[i].getName() + ", ");
			}
		}
		IRC.sendMessage(channel, sb.toString());
		return true;
	}
}
