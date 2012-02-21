package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.Variables;

public class Nick extends GameCommand {

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("nick");
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
		if (split.length == 3) {
			Variables.name = split[2];
			if (MonsterIRC.getHandleManager().getIRCHandler()
					.isConnected(MonsterIRC.getIRCServer())) {
				MonsterIRC.getHandleManager().getIRCHandler()
						.changeNick(MonsterIRC.getIRCServer(), Variables.name);
			}
			sender.sendMessage("Nick successfully changed to: "
					+ Variables.name);
			return true;
		} else {
			sender.sendMessage("Invalid Usage. Please use: nick [NAME]");
			return true;

		}
	}

	@Override
	public String getPermissions() {
		return "irc.nick";
	}

}
