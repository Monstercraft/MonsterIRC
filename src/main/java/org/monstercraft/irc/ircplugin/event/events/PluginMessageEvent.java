package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class PluginMessageEvent extends IRCEvent {

	private static final long serialVersionUID = 8708860642802706979L;

	private IRCChannel channel;

	private String sender;

	private String message;

	public PluginMessageEvent(IRCChannel channel, String sender, String message) {
		this.channel = channel;
		this.sender = sender;
		this.message = message;
	}

	@Override
	public void dispatch(EventListener el) {
		((IRCListener) el).messageReceived(channel, sender, message);
	}

	@Override
	public long getMask() {
		return EventMulticaster.IRC_MESSAGE_EVENT;
	}

}
