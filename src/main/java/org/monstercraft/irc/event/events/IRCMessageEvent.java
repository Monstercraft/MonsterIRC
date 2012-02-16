package org.monstercraft.irc.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.event.IRCEvent;
import org.monstercraft.irc.wrappers.IRCChannel;

public class IRCMessageEvent extends IRCEvent {

	public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCMessageEvent";

	private static final HandlerList handlers = new HandlerList();

	private static final long serialVersionUID = 6167425751903134777L;

	private IRCChannel channel;

	private String name;

	private String message;

	public IRCMessageEvent(IRCChannel channel, String name, String message) {
		super(CUSTOM_TYPE);
		this.name = name;
		this.channel = channel;
		this.message = message;
	}

	public IRCChannel getIRCChannel() {
		return channel;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
