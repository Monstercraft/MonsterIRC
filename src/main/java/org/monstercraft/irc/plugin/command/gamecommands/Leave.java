package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Leave extends GameCommand {

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc") && split[1].equalsIgnoreCase("leave");
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
				sender.sendMessage("[IRC] PEX not detected, unable to run any IRC commands.");
				return true;
			}
		}
		if (split.length < 2) {
			sender.sendMessage("[IRC] Please specify a channel to leave!");
			return false;
		}
		if (split[2] == null) {
			sender.sendMessage("[IRC] Please specify a channel to leave!");
			return false;
		}
		for (IRCChannel c : Variables.channels) {
			if (c.getChannel().equalsIgnoreCase(split[2])) {
				MonsterIRC.getHandleManager().getIRCHandler().part(c);
				return true;
			}
		}
		sender.sendMessage("[IRC] Could not part from that channel!");
		return true;
	}

	@Override
	public String getPermission() {
		return "irc.leave";
	}

	@Override
	public String[] getHelp() {
		return new String[] {
				ColorUtils.RED.getMinecraftColor() + "Command: "
						+ ColorUtils.WHITE.getMinecraftColor() + "Leave",
				ColorUtils.RED.getMinecraftColor() + "Description: "
						+ ColorUtils.WHITE.getMinecraftColor()
						+ "Makes the bot part from a IRC channel.",
				ColorUtils.RED.getMinecraftColor() + "Usage: "
						+ ColorUtils.WHITE.getMinecraftColor()
						+ "/irc leave (channel)" };
	}

	@Override
	public String getCommandName() {
		return "Leave";
	}

}
