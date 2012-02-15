package org.monstercraft.irc.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.GameCommand;
import org.monstercraft.irc.util.Variables;

public class Nick extends GameCommand {

	public Nick(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("nick");
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (IRC.getHandleManager().getPermissionsHandler() != null) {
				if (!IRC.getHandleManager().getPermissionsHandler()
						.hasCommandPerms(((Player) sender), this)) {
					sender.sendMessage("[IRC] You don't have permission to preform that command.");
					return false;
				}
			}else {
				sender.sendMessage("[IRC] PEX not detected, unable to run any IRC commands.");
				return false;
			}
		}
		if (split.length == 3) {
			Variables.name = split[2];
			if (IRC.getHandleManager().getIRCHandler().isConnected(IRC.getIRCServer())) {
				IRC.getHandleManager().getIRCHandler()
						.changeNick(IRC.getIRCServer(), Variables.name);
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
