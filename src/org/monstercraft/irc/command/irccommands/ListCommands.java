package org.monstercraft.irc.command.irccommands;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.IRCCommand;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;

public class ListCommands extends IRCCommand {

	public ListCommands(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(String sender, String message, IRCChannel channel) {
		return IRC.getHandleManager().getIRCHandler()
				.isConnected(IRC.getIRCServer())
				&& message.startsWith(Variables.commandPrefix + "listcommands");
	}

	public boolean execute(String sender, String message, IRCChannel channel) {
		if (IRC.getHandleManager().getIRCHandler().isOp(channel, sender)) {
			StringBuilder sb = new StringBuilder();
			sb.append("As an OP you can use ");
			for (String string : channel.getOpCommands()) {
				sb.append("\"" + string + "\" ");
			}
			for (String string : channel.getVoiceCommands()) {
				sb.append("\"" + string + "\" ");
			}
			for (String string : channel.getUserCommands()) {
				sb.append("\"" + string + "\" ");
			}
			IRC.getHandleManager().getIRCHandler()
					.sendNotice(sb.toString(), sender);
			return true;
		} else if (IRC.getHandleManager().getIRCHandler()
				.isVoice(channel, sender)) {
			StringBuilder sb = new StringBuilder();
			sb.append("As an Voice you can use ");
			for (String string : channel.getVoiceCommands()) {
				sb.append("\"" + string + "\" ");
			}
			for (String string : channel.getUserCommands()) {
				sb.append("\"" + string + "\" ");
			}
			IRC.getHandleManager().getIRCHandler()
					.sendNotice(sb.toString(), sender);
			return true;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("As an User you can use ");
			for (String string : channel.getUserCommands()) {
				sb.append("\"" + string + "\" ");
			}
			IRC.getHandleManager().getIRCHandler()
					.sendNotice(sb.toString(), sender);
			return true;
		}
	}
}
