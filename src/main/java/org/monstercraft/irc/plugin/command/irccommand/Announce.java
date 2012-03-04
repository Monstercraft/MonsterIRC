package org.monstercraft.irc.plugin.command.irccommand;

import org.bukkit.Bukkit;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.util.IRCColor;
import org.monstercraft.irc.plugin.util.Variables;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Announce extends IRCCommand {

	@Override
	public boolean canExecute(String sender, String message, IRCChannel channel) {
		return MonsterIRC.getHandleManager().getIRCHandler()
				.isConnected(MonsterIRC.getIRCServer())
				&& message.toLowerCase().startsWith(
						Variables.commandPrefix + "announce");
	}

	@Override
	public boolean execute(String sender, String message, IRCChannel channel) {
		if (MonsterIRC.getHandleManager().getIRCHandler()
				.isVoice(channel, sender)
				|| MonsterIRC.getHandleManager().getIRCHandler()
						.isOp(channel, sender)) {
			Bukkit.getServer().broadcastMessage(
					"§4[IRC]<" + sender + ">: "
							+ IRCColor.formatMCMessage(message.substring(10)));
		}
		return true;
	}

}
