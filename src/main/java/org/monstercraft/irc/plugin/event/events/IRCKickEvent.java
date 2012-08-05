package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class IRCKickEvent extends IRCEvent {

	public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCKickEvent";

	private static final HandlerList handlers = new HandlerList();

	private IRCChannel channel;

	private String name;

	private String kicker;

	private String reason;

	public IRCKickEvent(IRCChannel channel, String kicker, String name,
			String reason) {
		this.name = name;
		this.channel = channel;
	}

	public IRCChannel getIRCChannel() {
		return channel;
	}

	public String getName() {
		return name;
	}

	public String getKicker() {
		return kicker;
	}

	public String getReason() {
		return reason;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
