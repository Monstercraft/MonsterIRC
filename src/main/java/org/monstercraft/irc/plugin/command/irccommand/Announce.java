package org.monstercraft.irc.plugin.command.irccommand;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.util.IRCColor;
import org.monstercraft.irc.plugin.util.Variables;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Announce extends IRCCommand {

	public Announce(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(String sender, String message, IRCChannel channel) {
		return IRC.getHandleManager().getIRCHandler()
				.isConnected(IRC.getIRCServer())
				&& message.toLowerCase().startsWith(
						Variables.commandPrefix + "announce");
	}

	@Override
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
