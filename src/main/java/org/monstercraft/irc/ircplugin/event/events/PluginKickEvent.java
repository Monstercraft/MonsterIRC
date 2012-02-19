package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class PluginKickEvent extends IRCEvent {

	private static final long serialVersionUID = 8708860642802706979L;

	private IRCChannel channel;

	private String user;

	private String reason;

	public PluginKickEvent(IRCChannel channel, String user, String reason) {
		this.channel = channel;
		this.user = user;
		this.reason = reason;
	}

	@Override
	public void dispatch(EventListener el) {
		((IRCListener) el).onKick(channel, user, reason);
	}

	@Override
	public long getMask() {
		return EventMulticaster.IRC_KICK_EVENT;
	}

}
