package org.monstercraft.irc.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.Command;

public class Disconnect extends Command {

	public Disconnect(IRC plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc")
				&& split[1].equalsIgnoreCase("disconnect")
				&& (!(sender instanceof Player)) && plugin.handle.isConnected();
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		return plugin.handle.disconnect();
	}

}
