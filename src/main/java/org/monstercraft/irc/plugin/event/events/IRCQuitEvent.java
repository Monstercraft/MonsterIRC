package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class IRCQuitEvent extends IRCEvent {

	public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCQuitEvent";

	private static final HandlerList handlers = new HandlerList();

	private static final long serialVersionUID = 6167425751903134777L;

	private IRCChannel channel;

	private String name;

	public IRCQuitEvent(IRCChannel channel, String name) {
		super(CUSTOM_TYPE);
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
