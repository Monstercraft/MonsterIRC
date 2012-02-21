package org.monstercraft.irc.plugin.command.irccommand;

import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.util.Variables;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Commands extends IRCCommand {

	@Override
	public boolean canExecute(String sender, String message, IRCChannel channel) {
		return MonsterIRC.getHandleManager().getIRCHandler()
				.isConnected(MonsterIRC.getIRCServer())
				&& message.startsWith(Variables.commandPrefix + "commands");
	}

	@Override
	public boolean execute(String sender, String message, IRCChannel channel) {
		if (MonsterIRC.getHandleManager().getIRCHandler().isOp(channel, sender)) {
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
			IRC.sendNotice(sender, sb.toString());
			return true;
		} else if (MonsterIRC.getHandleManager().getIRCHandler()
				.isVoice(channel, sender)) {
			StringBuilder sb = new StringBuilder();
			sb.append("As an Voice you can use ");
			for (String string : channel.getVoiceCommands()) {
				sb.append("\"" + string + "\" ");
			}
			for (String string : channel.getUserCommands()) {
				sb.append("\"" + string + "\" ");
			}
			IRC.sendNotice(sender, sb.toString());
			return true;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("As an User you can use ");
			for (String string : channel.getUserCommands()) {
				sb.append("\"" + string + "\" ");
			}
			IRC.sendNotice(sender, sb.toString());
			return true;
		}
	}
}
