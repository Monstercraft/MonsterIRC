package org.monstercraft.irc.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.event.IRCEvent;
import org.monstercraft.irc.wrappers.IRCChannel;

public class IRCModeEvent extends IRCEvent {

	public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCModeEvent";

	private static final HandlerList handlers = new HandlerList();

	private static final long serialVersionUID = 6167425751903134777L;

	private IRCChannel channel;

	private String name;

	private String message;

	private String mode;

	public IRCModeEvent(IRCChannel channel, String name, String mode,
			String message) {
		super(CUSTOM_TYPE);
		this.name = name;
		this.channel = channel;
		this.message = message;
		this.mode = mode;
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

	public String getMode() {
		return mode;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
