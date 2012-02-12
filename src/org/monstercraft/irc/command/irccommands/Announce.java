package org.monstercraft.irc.command.irccommands;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.IRCCommand;
import org.monstercraft.irc.util.IRCColor;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;

public class Announce extends IRCCommand {

	public Announce(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(String sender, String message, IRCChannel channel) {
		return IRC.getHandleManager().getIRCHandler().isConnected(IRC.getIRCServer())
				&& message.startsWith(Variables.commandPrefix + "announce");
	}

	public boolean execute(String sender, String message, IRCChannel channel) {
		if (IRC.getHandleManager().getIRCHandler().isVoice(channel, sender)
				|| IRC.getHandleManager().getIRCHandler().isOp(channel, sender)) {
			plugin.getServer().broadcastMessage(
					"§4[IRC]<" + sender + ">: "
							+ IRCColor.formatMCMessage(message.substring(10)));
		}
		return true;
	}

}
