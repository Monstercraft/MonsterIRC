package org.monstercraft.irc.plugin.command.irccommand;

import org.bukkit.Bukkit;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Announce extends IRCCommand {

	@Override
	public boolean canExecute(String sender, String message, IRCChannel channel) {
		return MonsterIRC.getHandleManager().getIRCHandler()
				.isConnected(MonsterIRC.getIRCServer())
				&& IRC.isVoicePlus(channel, sender)
				&& message.toLowerCase().startsWith(
						Variables.commandPrefix + "announce");
	}

	@Override
	public boolean execute(String sender, String message, IRCChannel channel) {
		Bukkit.getServer().broadcastMessage(
				ColorUtils.formatIRCMessage("[IRC]<" + sender + ">: "
						+ message.substring(10)));
		return true;
	}

}
