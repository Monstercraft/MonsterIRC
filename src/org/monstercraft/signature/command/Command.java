package org.monstercraft.signature.command;

import org.bukkit.command.CommandSender;

public abstract class Command {

	public abstract boolean canExecute(CommandSender sender, String[] split);

	public abstract boolean execute(CommandSender sender, String[] split);
}