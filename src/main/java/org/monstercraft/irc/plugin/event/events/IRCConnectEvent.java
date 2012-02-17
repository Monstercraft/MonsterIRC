package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

public class IRCConnectEvent extends IRCEvent {

	public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCConnectEvent";

	private static final HandlerList handlers = new HandlerList();

	private static final long serialVersionUID = 6167425751903134777L;

	private IRCServer server;

	public IRCConnectEvent(IRCServer server) {
		super(CUSTOM_TYPE);
		this.server = server;
	}

	public IRCServer getIRCServer() {
		return server;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
