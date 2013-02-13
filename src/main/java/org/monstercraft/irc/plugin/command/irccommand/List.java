package org.monstercraft.irc.plugin.command.irccommand;

import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class List extends IRCCommand {

    @Override
    public boolean canExecute(final String sender, final String message,
            final IRCChannel channel) {
        return MonsterIRC.getHandleManager().getIRCHandler().isConnected()
                && message.toLowerCase().startsWith(
                        Variables.commandPrefix + "list");
    }

    @Override
    public boolean execute(final String sender, final String message,
            final IRCChannel channel) {
    	final Player[] players = Bukkit.getServer().getOnlinePlayers();
		Vector<String> messages = new Vector<String>();
		StringBuilder sb = new StringBuilder();
		int last = players.length - 1;
		int max = 424;

		sb.append("Players currently online");
		sb.append("(");
		sb.append(players.length);
		sb.append("/");
		sb.append(Bukkit.getServer().getMaxPlayers());
		sb.append("): ");

		for (Player player : players) {
			if (player.getName().length() + sb.length() <= max) {
				sb.append(player.getName());
				if (!player.equals(players[last])) {
					sb.append(", ");
				}
			} else {
				messages.add(sb.toString());
				sb = new StringBuilder(max);
				sb.append(player.getName());
			}
		}
		messages.add(sb.toString());

		if (IRC.isVoicePlus(channel, sender)) {//
			for (String s : messages) {
				IRC.sendMessageToChannel(channel, s);
			}
		} else {
			IRC.sendNotice(sender, sb.toString());
		}
		return true;
    }
}
