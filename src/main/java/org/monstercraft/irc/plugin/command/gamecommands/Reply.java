package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;

public class Reply extends GameCommand {

	@Override
	public String getPermission() {
		return "irc.reply";
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc") && split[1].equalsIgnoreCase("r");
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
		if (Variables.reply.get((Player) sender) != null) {
			if (split.length < 2) {
				sender.sendMessage("Invalid usage!");
				sender.sendMessage("Proper usage: irc r [message]");
				return true;
			} else {
				StringBuffer result = new StringBuffer();
				result.append("[MC] " + sender.getName() + ": ");
				for (int i = 2; i < split.length; i++) {
					result.append(split[i]);
					result.append(" ");
				}
				MonsterIRC
						.getHandleManager()
						.getIRCHandler()
						.sendMessage(Variables.reply.get((Player) sender),
								result.toString());
				sender.sendMessage(ColorUtils.LIGHT_GRAY.getMinecraftColor()
						+ "([IRC] to "
						+ Variables.reply.get((Player) sender)
						+ "): "
						+ result.toString().substring(
								7 + sender.getName().length()));
				return true;
			}
		} else {
			sender.sendMessage("Nothing to reply to!");
			return true;
		}
	}

	@Override
	public String getCommandName() {
		return "r";
	}

	@Override
	public String[] getHelp() {
		return new String[] {
				ColorUtils.RED.getMinecraftColor() + "Command: "
						+ ColorUtils.WHITE.getMinecraftColor() + "Reply",
				ColorUtils.RED.getMinecraftColor() + "Description: "
						+ ColorUtils.WHITE.getMinecraftColor()
						+ "Replys to the last IRC PM.",
				ColorUtils.RED.getMinecraftColor() + "Usage: "
						+ ColorUtils.WHITE.getMinecraftColor()
						+ "/irc r (message)" };
	}

}
