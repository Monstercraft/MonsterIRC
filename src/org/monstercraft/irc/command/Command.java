package org.monstercraft.irc.command;

import org.bukkit.command.CommandSender;
import org.monstercraft.irc.IRC;

public abstract class Command extends IRC {
	
	protected IRC plugin;
	
	public Command(IRC plugin) {
		this.plugin = plugin;
	}

	public abstract String getPermissions();
	
	public abstract boolean canExecute(CommandSender sender, String[] split);

	public abstract boolean execute(CommandSender sender, String[] split);
}