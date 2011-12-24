package org.monstercraft.irc.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.command.Command;
import org.monstercraft.irc.util.Variables;

public class Nick extends Command {

	public Nick(org.monstercraft.irc.IRC plugin) {
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
		return split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("nick");
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		if (split.length == 3) {
			Variables.name = split[2];
			if (plugin.IRC.isConnected()) {
				plugin.IRC.changeNick(Variables.name);
			}
			sender.sendMessage("Nick successfully changed to: "
					+ Variables.name);
			return true;
		} else {
			sender.sendMessage("Invalid Usage. Please use: nick [NAME]");
			return true;

		}
	}

	@Override
	public String getPermissions() {
		return "irc.nick";
	}

}
