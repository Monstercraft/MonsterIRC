package org.monstercraft.irc.plugin.command.irccommand;

import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.util.Variables;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class List extends IRCCommand {

	public List(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(String sender, String message, IRCChannel channel) {
		return IRC.getHandleManager().getIRCHandler()
				.isConnected(IRC.getIRCServer())
				&& message.toLowerCase().startsWith(
						Variables.commandPrefix + "list");
	}

	@Override
	public boolean execute(String sender, String message, IRCChannel channel) {
		if (IRC.getHandleManager().getIRCHandler().isOp(channel, sender)
				|| IRC.getHandleManager().getIRCHandler()
						.isVoice(channel, sender)) {
			Player[] players = plugin.getServer().getOnlinePlayers();
			StringBuilder sb = new StringBuilder();
			sb.append("Players currently online" + "(" + players.length + "/"
					+ plugin.getServer().getMaxPlayers() + "): ");
			for (int i = 0; i < players.length; i++) {
				if (i == players.length - 1) {
					sb.append(players[i].getName());
				} else {
					sb.append(players[i].getName() + ", ");
				}
			}
			IRC.getHandleManager().getIRCHandler()
					.sendMessage(sb.toString(), channel.getChannel());
			return true;
		}
		return false;
	}
}
