package org.monstercraft.irc.command.irccommands;

import org.monstercraft.irc.command.IRCCommand;
import org.monstercraft.irc.util.Variables;

public class Announce extends IRCCommand {

	public Announce(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(String sender, String message) {
		return plugin.getHandleManager().getIRCHandler().isConnected()
				&& message.startsWith(".announce");
	}

	public boolean execute(String sender, String message) {
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
			if (Variables.all) {
				plugin.getServer().broadcastMessage(
						"[IRC]<"
								+ sender
								+ ">: "
								+ plugin.getHandleManager().getIRCHandler()
										.removeColors(message.substring(10)));
			} else if (Variables.hc
					&& plugin.getHookManager().getHeroChatHook() != null) {
				plugin.getHookManager()
						.getHeroChatHook()
						.getChannelManager()
						.getChannel(Variables.announce)
						.sendMessage(
								"<" + sender + ">",
								plugin.getHandleManager().getIRCHandler()
										.removeColors(message.substring(10)),
								plugin.getHookManager().getHeroChatHook()
										.getChannelManager()
										.getChannel(Variables.announce)
										.getMsgFormat(), false);
			}
		}
		return true;
	}

}
