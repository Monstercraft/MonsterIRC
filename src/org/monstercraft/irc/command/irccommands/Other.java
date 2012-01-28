package org.monstercraft.irc.command.irccommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.IRCCommand;

public class Other extends IRCCommand {

	public Other(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(String sender, String message, String channel) {
		return IRC.getHandleManager().getIRCHandler().isConnected();
	}

	public boolean execute(String sender, String message, String channel) {
		if (IRC.getHandleManager()
				.getIRCHandler()
				.isOp(sender,
						IRC.getHandleManager().getIRCHandler().getOpList())) {
			try {
				CommandSender console = Bukkit.getConsoleSender();
				plugin.getServer().dispatchCommand(console,
						message.substring(message.indexOf(".") + 1));
			} catch (CommandException e) {
				IRC.getHandleManager()
						.getIRCHandler()
						.sendMessage(
								sender + ": Error executing ingame command! "
										+ e.toString(), sender);
			}
		}
		return true;
	}
}
