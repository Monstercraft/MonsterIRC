package org.monstercraft.irc.command.irccommands;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.IRCCommand;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;

public class Mute extends IRCCommand {

	public Mute(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(String sender, String message, IRCChannel channel) {
		return IRC.getHandleManager().getIRCHandler().isConnected()
				&& message.startsWith(Variables.commandPrefix + "mute");
	}

	public boolean execute(String sender, String message, IRCChannel channel) {
		String user = message.substring(6);
		if (IRC.getHandleManager()
				.getIRCHandler()
				.isVoice(sender,
						IRC.getHandleManager().getIRCHandler().getVoiceList())
				|| IRC.getHandleManager()
						.getIRCHandler()
						.isOp(sender,
								IRC.getHandleManager().getIRCHandler()
										.getOpList())) {
			if (!Variables.muted.contains(user.toString().toLowerCase())) {
				Variables.muted.add(user.toString().toLowerCase());
				plugin.getSettings().saveMuteConfig();
				log("Player " + user.toString()
						+ " has been muted from talking via IRC.");
			} else {
				log("Player " + user.toString()
						+ "is already muted from talking via IRC.");
				return true;
			}
			return Variables.muted.contains(user.toString().toLowerCase());
		}
		return true;
	}

}
