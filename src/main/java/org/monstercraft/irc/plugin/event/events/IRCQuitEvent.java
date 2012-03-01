package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class IRCQuitEvent extends IRCEvent {

	public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCQuitEvent";

	private static final HandlerList handlers = new HandlerList();

	private IRCChannel channel;

	private String name;

	public IRCQuitEvent(IRCChannel channel, String name) {
		this.channel = channel;
		this.name = name;
	}

	public IRCChannel getIRCChannel() {
		return channel;
	}

	public String getName() {
		return name;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
