package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

public class PluginConnectEvent extends IRCEvent {

	private static final long serialVersionUID = 8708860642802706979L;

	private IRCServer server;

	public PluginConnectEvent(IRCServer server) {
		this.server = server;
	}

	@Override
	public void dispatch(EventListener el) {
		((IRCListener) el).onConnect(server);
	}

	@Override
	public long getMask() {
		return EventMulticaster.IRC_CONNECT_EVENT;
	}

}
