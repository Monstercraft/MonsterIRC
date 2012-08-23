package org.monstercraft.irc.plugin.command.irccommand;

import java.util.ArrayList;

import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Tail extends IRCCommand {

	@Override
	public boolean canExecute(String sender, String message, IRCChannel channel) {
		return MonsterIRC.getHandleManager().getIRCHandler()
				.isConnected()
				&& IRC.isOp(channel, sender)
				&& message.toLowerCase().startsWith(
						Variables.commandPrefix + "tail");
	}

	@Override
	public boolean execute(String sender, String message, IRCChannel channel) {
		int size = 25;
		if (message.length() > 6) {
			if (validNumber(message.substring(6))) {
				size = Integer.parseInt(message.substring(6));
			}
		}
		ArrayList<String> records = MonsterIRC.getLogHandler().getLastRecords(
				size);
		if (records.isEmpty()) {
			IRC.sendNotice(sender, "No records found!");
			return true;
		}
		for (String s : records) {
			IRC.sendNotice(sender, s);
		}
		return true;
	}

	public boolean validNumber(String in) {
		try {

			Integer.parseInt(in);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}
}
