package org.monstercraft.irc.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.command.Command;
import org.monstercraft.irc.handlers.IRCHandler;

public class Connect extends Command {
	
	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc") && split[1].equalsIgnoreCase("connect")
				&& (!(sender instanceof Player))
				&& !IRCHandler.isConnected();
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		return IRCHandler.connect();
	}

}
