package org.monstercraft.irc.command.commands;

import org.bukkit.command.CommandSender;
import org.monstercraft.irc.command.Command;
import org.monstercraft.irc.handlers.IRCHandler;

public class Say extends Command {

	public boolean canExecute(CommandSender sender, String[] split) {
		return IRCHandler.isConnected() && split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("say");
	}

	public boolean execute(CommandSender sender, String[] split) {
		StringBuffer result = new StringBuffer();
		StringBuffer result2 = new StringBuffer();
		result.append("<" + sender.getName() + ">" + ":");
		for (int i = 2; i < split.length; i++) {
			result.append(split[i]);
			result.append(" ");
			result2.append(split[i]);
			result2.append(" ");
		}

		IRCHandler.sendMessage(result.toString());
		server.broadcastMessage(result.toString());
		return true;
	}

}
