package org.monstercraft.irc.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.GameCommand;
import org.monstercraft.irc.plugin.util.Variables;

public class Unmute extends GameCommand {

	public Unmute(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc") && split[1].equalsIgnoreCase("unmute");
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
		if (Variables.muted.contains(split[2].toString().toLowerCase())) {
			Variables.muted.remove(split[2].toString().toLowerCase());
			IRC.getSettingsManager().saveMuted();
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
	public String getPermissions() {
		return "irc.unmute";
	}

}
