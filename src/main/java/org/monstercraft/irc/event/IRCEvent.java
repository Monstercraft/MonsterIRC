package org.monstercraft.irc.event;

import org.bukkit.event.Event;

public abstract class IRCEvent extends Event {

	private static final long serialVersionUID = -2527203293811080247L;

	public IRCEvent(String type) {
		super(type);
	}

}
