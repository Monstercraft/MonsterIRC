package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.Variables;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Leave extends GameCommand {

	public Leave(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc") && split[1].equalsIgnoreCase("leave");
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
				IRC.getHandleManager().getIRCHandler().part(c);
				return true;
			}
		}
		sender.sendMessage("[IRC] Could not join that channel!");
		return false;
	}

	@Override
	public String getPermissions() {
		return "irc.leave";
	}

}
