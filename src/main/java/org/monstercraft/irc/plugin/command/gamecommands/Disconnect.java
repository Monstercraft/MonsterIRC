package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;

public class Disconnect extends GameCommand {

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc")
				&& split[1].equalsIgnoreCase("disconnect");
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
		sender.sendMessage("Successfully disconnected!");
		return MonsterIRC.getHandleManager().getIRCHandler()
				.disconnect(MonsterIRC.getIRCServer());
	}

	@Override
	public String getPermission() {
		return "irc.disconnect";
	}

	@Override
	public String[] getHelp() {
		return new String[] {
				ColorUtils.RED.getMinecraftColor() + "Command: "
						+ ColorUtils.WHITE.getMinecraftColor() + "Disconnect",
				ColorUtils.RED.getMinecraftColor() + "Description: "
						+ ColorUtils.WHITE.getMinecraftColor()
						+ "Disconnects the bot from IRC channel.",
				ColorUtils.RED.getMinecraftColor() + "Usage: "
						+ ColorUtils.WHITE.getMinecraftColor() + "/connect" };
	}

	@Override
	public String getCommandName() {
		return "Diconnect";
	}

}
