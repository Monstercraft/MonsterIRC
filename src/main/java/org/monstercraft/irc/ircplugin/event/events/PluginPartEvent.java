package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class PluginPartEvent extends IRCEvent {

	private static final long serialVersionUID = 8708860642802706979L;

	private IRCChannel channel;

	private String user;

	public PluginPartEvent(IRCChannel channel, String user) {
		this.channel = channel;
		this.user = user;
	}

	@Override
	public void dispatch(EventListener el) {
		((IRCListener) el).onPart(channel, user);
	}

	@Override
	public long getMask() {
		return EventMulticaster.IRC_PART_EVENT;
	}

}
