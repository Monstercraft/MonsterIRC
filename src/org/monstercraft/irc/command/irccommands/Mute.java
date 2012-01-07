package org.monstercraft.irc.command.irccommands;

import org.monstercraft.irc.command.IRCCommand;
import org.monstercraft.irc.util.Variables;

public class Mute extends IRCCommand {

	public Mute(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(String sender, String message) {
		return plugin.getHandleManager().getIRCHandler().isConnected()
				&& message.startsWith(".mute");
	}

	public boolean execute(String sender, String message) {
		String user = message.substring(6);
		if (plugin
				.getHandleManager()
				.getIRCHandler()
				.isVoice(
						sender,
						plugin.getHandleManager().getIRCHandler()
								.getVoiceList())
				|| plugin
						.getHandleManager()
						.getIRCHandler()
						.isOp(sender,
								plugin.getHandleManager().getIRCHandler()
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
