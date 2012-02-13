package org.monstercraft.irc.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.GameCommand;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;

public class Connect extends GameCommand {

	public Connect(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc") && split[1].equalsIgnoreCase("connect")
				&& !IRC.getIRCServer().isConnected();
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
			} else {
				sender.sendMessage("[IRC] PEX not detected, unable to run any IRC commands.");
				return false;
			}
		}
		IRC.getIRCServer().connect();
		for (IRCChannel c : Variables.channels) {
			if (c.isAutoJoin()) {
				c.join();
			}
		}
		return true;
	}

	@Override
	public String getPermissions() {
		return "irc.connect";
	}

}