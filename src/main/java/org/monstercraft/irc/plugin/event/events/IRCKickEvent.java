package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class IRCKickEvent extends IRCEvent {

	public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCKickEvent";

	private static final HandlerList handlers = new HandlerList();

	private static final long serialVersionUID = 6167425751903134777L;

	private IRCChannel channel;

	private String name;

	public IRCKickEvent(IRCChannel channel, String name) {
		super(CUSTOM_TYPE);
		this.name = name;
		this.channel = channel;
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
