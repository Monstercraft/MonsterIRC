package org.monstercraft.irc.command.gamecommands;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.GameCommand;
import org.monstercraft.irc.util.IRCColor;

public class PrivateMessage extends GameCommand {

	ArrayList<String> first = new ArrayList<String>();

	public PrivateMessage(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("pm");
	}

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
		if (split.length < 4) {
			sender.sendMessage("Invalid usage!");
			sender.sendMessage("Proper usage: irc pm [user] [message]");
			return false;
		} else {
			if (!first.contains(split[2])) {
				IRC.getHandleManager()
						.getIRCHandler()
						.sendMessage(
								"You have revieved a private message from MonsterIRC!",
								split[2]);
				IRC.getHandleManager()
						.getIRCHandler()
						.sendMessage(
								"To reply type \"" + sender.getName()
										+ ":\" (message)", split[2]);
				first.add(split[2]);
			}
			StringBuffer result = new StringBuffer();
			for (int i = 3; i < split.length; i++) {
				result.append(split[i]);
				result.append(" ");
			}
			IRC.getHandleManager().getIRCHandler()
					.sendMessage(result.toString(), split[2]);
			sender.sendMessage(IRCColor.LIGHT_GRAY.getMinecraftColor()
					+ "([IRC] to " + split[2] + "): " + result.toString());
			return true;
		}
	}

	@Override
	public String getPermissions() {
		return "irc.pm";
	}

}
