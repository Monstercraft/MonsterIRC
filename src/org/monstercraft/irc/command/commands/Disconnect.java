package org.monstercraft.irc.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.command.Command;

public class Disconnect extends Command {

	public Disconnect(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (!plugin.perms.hasCommandPerms(((Player) sender),
					this)) {
				return false;
			}
		}
		return split[0].contains("irc")
				&& split[1].equalsIgnoreCase("disconnect")
				&& plugin.IRC.isConnected();
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		return plugin.IRC.disconnect();
	}

	@Override
	public String getPermissions() {
		return "irc.disconnect";
	}

}
