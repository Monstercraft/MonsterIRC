package org.monstercraft.irc.command.commands;

import org.bukkit.command.CommandSender;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.Command;
import org.monstercraft.irc.handlers.IRCHandler;

public class Say extends Command {

	public boolean canExecute(CommandSender sender, String[] split) {
		return IRCHandler.isConnected() && split[0].equalsIgnoreCase("irc") && split[1].equalsIgnoreCase("say");
	}

	public boolean execute(CommandSender sender, String[] split) {
		String s = "";
		for (int i = 2; i < split.length; i++) {
			s = s + split[i] + " ";
		}
		IRCHandler.sendMessage(s);
		IRC.server.broadcastMessage("[IRC] " + ": " + s);
		return true;
	}

}
