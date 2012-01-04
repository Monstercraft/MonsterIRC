package org.monstercraft.irc.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.command.Command;

public class ReloadConfig extends Command {

	public ReloadConfig(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(CommandSender sender, String[] split) {
		return plugin.IRC.isConnected() && split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("reloadconfig");
	}

	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (!plugin.perms.hasCommandPerms(((Player) sender), this)) {
				sender.sendMessage("[IRC] You don't have permission to preform that command.");
				return false;
			}
			sender.sendMessage("[IRC] You must run this command via the console.");
			return false;
		}
		plugin.IRC.disconnect();
		plugin.settings.loadConfigs();
		return plugin.IRC.connect();
	}

	@Override
	public String getPermissions() {
		return "irc.console";
	}

}
