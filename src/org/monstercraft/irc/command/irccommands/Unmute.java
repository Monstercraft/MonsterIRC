package org.monstercraft.irc.command.irccommands;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.IRCCommand;
import org.monstercraft.irc.util.Variables;

public class Unmute extends IRCCommand {

	public Unmute(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(String sender, String message) {
		return IRC.getHandleManager().getIRCHandler().isConnected()
				&& message.startsWith(".unmute");
	}

	public boolean execute(String sender, String message) {
		String user = message.substring(8);
		if (IRC
				.getHandleManager()
				.getIRCHandler()
				.isVoice(
						sender,
						IRC.getHandleManager().getIRCHandler()
								.getVoiceList())
				|| IRC
						.getHandleManager()
						.getIRCHandler()
						.isOp(sender,
								IRC.getHandleManager().getIRCHandler()
										.getOpList())) {
			if (Variables.muted.contains(user.toString().toLowerCase())) {
				Variables.muted.remove(user.toString().toLowerCase());
				plugin.getSettings().saveMuteConfig();
				log("Player " + user.toString()
						+ " has been unmuted from talking via IRC.");
			} else {
				log("Player " + user.toString()
						+ " is not muted from talking via IRC.");
				return true;
			}
			return !Variables.muted.contains(user.toLowerCase());
		}
		return true;
	}

}
