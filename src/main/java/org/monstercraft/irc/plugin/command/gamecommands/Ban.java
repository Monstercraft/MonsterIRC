package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.Variables;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Ban extends GameCommand {

	public Ban(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc") && split[1].equalsIgnoreCase("ban");
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
		for (IRCChannel c : Variables.channels) {
			if (c.getChannel().equalsIgnoreCase(split[3])) {
				IRC.getHandleManager()
						.getIRCHandler()
						.ban(IRC.getIRCServer(), split[2].toString(),
								c.getChannel());
				return true;
			}
		}
		sender.sendMessage("Invalid usage, proper usage:/irc ban [user] [channel]");
		return false;
	}

	@Override
	public String getPermissions() {
		return "irc.ban";
	}

}
