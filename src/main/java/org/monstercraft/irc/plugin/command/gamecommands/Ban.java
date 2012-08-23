package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Ban extends GameCommand {

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc") && split[1].equalsIgnoreCase("ban");
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (MonsterIRC.getHandleManager().getPermissionsHandler() != null) {
				if (!MonsterIRC.getHandleManager().getPermissionsHandler()
						.hasCommandPerms(((Player) sender), this)) {
					sender.sendMessage("[IRC] You don't have permission to preform that command.");
					return true;
				}
			} else {
				sender.sendMessage("[IRC] Permissions not detected, unable to run any IRC commands.");
				return true;
			}
		}
		if (split.length < 2) {
			sender.sendMessage("Invalid usage, proper usage:/irc ban [user]");
			return true;
		} else {
			sender.sendMessage("Attempting to kick & ban " + split[2] + "!");
			for (IRCChannel c : Variables.channels) {
				MonsterIRC.getHandleManager().getIRCHandler()
						.ban(split[2].toString(), c.getChannel());
			}
			return true;
		}
	}

	@Override
	public String getPermission() {
		return "irc.ban";
	}

	@Override
	public String[] getHelp() {
		return new String[] {
				ColorUtils.RED.getMinecraftColor() + "Command: "
						+ ColorUtils.WHITE.getMinecraftColor() + "Ban",
				ColorUtils.RED.getMinecraftColor()
						+ "Description: "
						+ ColorUtils.WHITE.getMinecraftColor()
						+ "Ban a user from the IRC channels that the bot is OP in.",
				ColorUtils.RED.getMinecraftColor() + "Usage: "
						+ ColorUtils.WHITE.getMinecraftColor()
						+ "/ban (IRC User)" };
	}

	@Override
	public String getCommandName() {
		return "Ban";
	}

}
