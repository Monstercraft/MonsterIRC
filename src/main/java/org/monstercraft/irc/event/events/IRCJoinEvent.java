package org.monstercraft.irc.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.event.IRCEvent;
import org.monstercraft.irc.wrappers.IRCChannel;

public class IRCJoinEvent extends IRCEvent {

	public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCJoinEvent";

	private static final HandlerList handlers = new HandlerList();

	private static final long serialVersionUID = 6167425751903134777L;

	private IRCChannel channel;

	private String user;

	public IRCJoinEvent(IRCChannel channel, String user) {
		super(CUSTOM_TYPE);
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

}
