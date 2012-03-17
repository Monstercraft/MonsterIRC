package org.monstercraft.irc.plugin.command.gamecommands;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;

public class PrivateMessage extends GameCommand {

	ArrayList<String> first = new ArrayList<String>();

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("pm");
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (MonsterIRC.getHandleManager().getPermissionsHandler() != null) {
				if (!MonsterIRC.getHandleManager().getPermissionsHandler()
						.hasCommandPerms((Player) sender, this)) {
					sender.sendMessage("[IRC] You don't have permission to preform that command.");
					return true;
				}
			} else {
				sender.sendMessage("[IRC] PEX not detected, unable to run any IRC commands.");
				return true;
			}
		}
		if (split.length < 4) {
			sender.sendMessage("Invalid usage!");
			sender.sendMessage("Proper usage: irc pm [user] [message]");
			return true;
		} else {
			if (!first.contains(split[2])) {
				MonsterIRC
						.getHandleManager()
						.getIRCHandler()
						.sendMessage(split[2],
								"You have revieved a private message from MonsterIRC!");
				MonsterIRC
						.getHandleManager()
						.getIRCHandler()
						.sendMessage(
								split[2],
								"To reply type \"" + sender.getName()
										+ ":\" (message)");
				first.add(split[2]);
			}
			StringBuffer result = new StringBuffer();
			result.append("[MC] " + sender.getName() + ": ");
			for (int i = 3; i < split.length; i++) {
				result.append(split[i]);
				result.append(" ");
			}
			MonsterIRC.getHandleManager().getIRCHandler()
					.sendMessage(split[2], result.toString());
			sender.sendMessage(ColorUtils.LIGHT_GRAY.getMinecraftColor()
					+ "([IRC] to "
					+ split[2]
					+ "): "
					+ result.toString()
							.substring(7 + sender.getName().length()));
			Variables.reply.put((Player) sender, split[2]);
			return true;
		}
	}

	@Override
	public String getPermission() {
		return "irc.pm";
	}

}
