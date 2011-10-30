package org.monstercraft.irc.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.handlers.IRCHandler;
import org.monstercraft.irc.util.Variables;

public class Commands {

	public static boolean processCommands(CommandSender sender,
			Command command, String label, String[] args) {
		String[] split = new String[args.length + 1];
		split[0] = label;
		for (int a = 0; a < args.length; a++) {
			split[a + 1] = args[a];
		}

		if (split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("nick")
				&& (!(sender instanceof Player))) {
			if (split.length == 3) {
				Variables.name = split[2];
				IRCHandler.changeNick(Variables.name);
				return true;
			} else {
				sender.sendMessage("Invalid Usage. Please use: nick [NAME]");
				return true;
			}
		}

		if (IRCHandler.isConnected()) {
			if (split[0].equalsIgnoreCase("irc")
					&& split[1].equalsIgnoreCase("say")) {
				String s = "";
				for (int i = 2; i < split.length; i++) {
					s = s + split[i] + " ";
				}
				IRCHandler.sendMessage(s);
				IRC.server.broadcastMessage("[IRC] " + ": " + s);
				return true;
			}
			
			if (split[0].equalsIgnoreCase("irc")
					&& split[1].equalsIgnoreCase("pm")) {
				String s = "";
				for (int i = 3; i < split.length; i++) {
					s = s + split[i] + " ";
				}
				IRCHandler.sendPrivateMessage(split[2], s);
				sender.sendMessage("[IRC]<to: " + split[2] + ">: " + s);
				return true;
			}

			if (split[0].contains("irc")
					&& split[1].equalsIgnoreCase("disconnect")
					&& (!(sender instanceof Player))) {
				IRCHandler.disconnect();
				return true;
			}
		} else if (!IRCHandler.isConnected()) {
			if (split[0].contains("irc")
					&& split[1].equalsIgnoreCase("connect")
					&& (!(sender instanceof Player))) {
				IRCHandler.connect();
				return true;
			}
		}
		return true;
	}

}
