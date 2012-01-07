package org.monstercraft.irc.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.command.GameCommand;
import org.monstercraft.irc.util.Variables;

public class Ban extends GameCommand {

	public Ban(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc") && split[1].equalsIgnoreCase("ban");
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (!plugin.getHandleManager().getPermissionsHandler()
					.hasCommandPerms(((Player) sender), this)) {
				sender.sendMessage("[IRC] You don't have permission to preform that command.");
				return false;
			}
		}
		plugin.getHandleManager().getIRCHandler()
				.ban(split[2].toString(), Variables.channel);
		return true;
	}

	@Override
	public String getPermissions() {
		return "irc.ban";
	}

}
