package org.monstercraft.irc.command.irccommands;

import org.bukkit.command.CommandException;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.IRCCommand;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;
import org.monstercraft.irc.wrappers.IRCCommandSender;

public class Other extends IRCCommand {

	public Other(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(String sender, String message, IRCChannel channel) {
		return IRC.getHandleManager().getIRCHandler().isConnected()
				&& !message.startsWith(Variables.commandPrefix + "announce")
				&& !message.startsWith(Variables.commandPrefix + "list")
				&& !message.startsWith(Variables.commandPrefix + "mute")
				&& !message.startsWith(Variables.commandPrefix + "unmute");
	}

	public boolean execute(String sender, String message, IRCChannel channel) {
		if (IRC.getHandleManager()
				.getIRCHandler()
				.isOp(sender,
						IRC.getHandleManager().getIRCHandler().getOpList())) {
			try {
				IRCCommandSender console = new IRCCommandSender(plugin, sender);
				plugin.getServer().dispatchCommand(console,
						message.substring(message.indexOf(Variables.commandPrefix) + 1));
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
