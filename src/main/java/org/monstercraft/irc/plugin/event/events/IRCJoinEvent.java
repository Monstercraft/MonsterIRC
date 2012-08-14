package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class IRCJoinEvent extends IRCEvent {

	public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCJoinEvent";

	private static final HandlerList handlers = new HandlerList();

	private IRCChannel channel;

	private String user;

	public IRCJoinEvent(IRCChannel channel, String user) {
		this.channel = channel;
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public IRCChannel getIRCChannel() {
		return channel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
