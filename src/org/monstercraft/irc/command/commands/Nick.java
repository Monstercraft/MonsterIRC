package org.monstercraft.irc.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.command.Command;
import org.monstercraft.irc.handlers.IRCHandler;
import org.monstercraft.irc.util.Variables;

public class Nick extends Command {

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("nick")
				&& (!(sender instanceof Player));
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		if (split.length == 3) {
			Variables.name = split[2];
			if (IRCHandler.isConnected()) {
				IRCHandler.changeNick(Variables.name);
			}
			sender.sendMessage("Nick successfully changed to: "
					+ Variables.name);
			return true;
		} else {
			sender.sendMessage("Invalid Usage. Please use: nick [NAME]");
			return true;

		}
	}

}
