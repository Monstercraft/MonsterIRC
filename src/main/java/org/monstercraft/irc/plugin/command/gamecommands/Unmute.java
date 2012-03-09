package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.GameCommand;

public class Unmute extends GameCommand {

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc") && split[1].equalsIgnoreCase("unmute");
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
		if (Variables.muted.contains(split[2].toString().toLowerCase())) {
			Variables.muted.remove(split[2].toString().toLowerCase());
			MonsterIRC.getSettingsManager().saveMuted();
			sender.sendMessage("Player " + split[2].toString()
					+ " has been unmuted from talking via IRC.");
		} else {
			sender.sendMessage("Player " + split[2].toString()
					+ " is not muted from talking via IRC.");
			return true;
		}
		return !Variables.muted.contains(split[2].toString().toLowerCase());
	}

	@Override
	public String getPermission() {
		return "irc.unmute";
	}

}
