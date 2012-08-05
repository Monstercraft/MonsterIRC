package org.monstercraft.irc.plugin.managers.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

public class IRCEventListener implements IRCListener {

	private final MonsterIRC instance;

	public IRCEventListener(MonsterIRC instance) {
		this.instance = instance;
	}

	@Override
	public void onMessage(IRCChannel channel, String sender, String message) {
		if (message.startsWith(Variables.commandPrefix)) {
			instance.getCommandManager().onIRCCommand(sender, message, channel);
		} else if (!Variables.passOnName
				&& !Variables.muted.contains(sender.toLowerCase())) {
			IRC.sendMessageToGame(channel, sender, message);
		} else if (Variables.passOnName && message.startsWith(Variables.name)
				&& !Variables.muted.contains(sender.toLowerCase())) {
			IRC.sendMessageToGame(channel, sender,
					message.substring(Variables.name.length()));
		}
	}

	@Override
	public void onPrivateMessage(String to, String from, String message) {
		Player p = Bukkit.getPlayer(to);
		if (p != null) {
			p.sendMessage(ColorUtils.LIGHT_GRAY.getMinecraftColor()
					+ "([IRC] from " + from + "):" + message);
			Variables.reply.put(p, from);
		}
	}

	@Override
	public void onKick(IRCChannel channel, String kicker, String user,
			String reason) {
		if (channel.showIRCEvents()) {
			onMessage(channel, channel.getChannel(), user
					+ " has been kicked from " + channel.getChannel() + " by "
					+ kicker + "(" + reason + ")");
		}
	}

	@Override
	public void onAction(IRCChannel channel, String sender, String message) {
		if (channel.showIRCEvents()) {
			onMessage(channel, channel.getChannel(), "* " + sender + " "
					+ message);
		}
	}

	@Override
	public void onMode(IRCChannel channel, String sender, String user,
			String mode) {
		if (channel.showIRCEvents()) {
			onMessage(channel, channel.getChannel(), sender + " gave mode "
					+ mode + " to " + user + ".");
		}
		mode = mode.toLowerCase();
		if (mode.contains("+v")) {
			channel.getVoiceList().add(user);
		} else if (mode.contains("-v")) {
			channel.getVoiceList().remove(user);
		} else if (mode.contains("+o")) {
			channel.getOpList().add(user);
		} else if (mode.contains("-o")) {
			channel.getOpList().remove(user);
		} else if (mode.contains("+h")) {
			channel.getHOpList().add(user);
		} else if (mode.contains("-h")) {
			channel.getHOpList().remove(user);
		} else if (mode.contains("+a")) {
			channel.getAdminList().add(user);
		} else if (mode.contains("-a")) {
			channel.getAdminList().remove(user);
		} else if (mode.contains("+q")) {
			channel.getOpList().add(user);
		} else if (mode.contains("-q")) {
			channel.getOpList().remove(user);
		}
	}

	@Override
	public void onPart(IRCChannel channel, String user) {
		if (channel.showIRCEvents()) {
			onMessage(channel, channel.getChannel(), user + " has left "
					+ channel.getChannel());
		}
	}

	@Override
	public void onQuit(IRCChannel channel, String user) {
		if (channel.showIRCEvents()) {
			onMessage(channel, channel.getChannel(), user + " has quit "
					+ channel.getChannel());
		}
	}

	@Override
	public void onJoin(IRCChannel channel, String user) {
		if (channel.showIRCEvents()) {
			onMessage(channel, channel.getChannel(), user + " has joined "
					+ channel.getChannel());
		}
	}

	// unused events
	public void onConnect(IRCServer server) {
	}

	public void onDisconnect(IRCServer server) {
	}

}
