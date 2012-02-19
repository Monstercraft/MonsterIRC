package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;

public class PluginPrivateMessageEvent extends IRCEvent {

	private static final long serialVersionUID = 8708860642802706979L;

	private String to;

	private String from;

	private String message;

	public PluginPrivateMessageEvent(String to, String from, String message) {
		this.to = to;
		this.from = from;
		this.message = message;
	}

	@Override
	public void dispatch(EventListener el) {
		((IRCListener) el).onPrivateMessage(to, from, message);
	}

	@Override
	public long getMask() {
		return EventMulticaster.IRC_PRIVATE_MESSAGE_EVENT;
	}

}
