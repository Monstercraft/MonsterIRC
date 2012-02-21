package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.command.GameCommand;

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
		return MonsterIRC.getHandleManager().getIRCHandler()
				.disconnect(MonsterIRC.getIRCServer());
	}

	@Override
	public String getPermissions() {
		return "irc.disconnect";
	}

}
