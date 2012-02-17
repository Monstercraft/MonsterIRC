package org.monstercraft.irc.plugin.event.listeners;

import java.util.EventListener;

import org.monstercraft.irc.wrappers.IRCChannel;

public interface IRCListener extends EventListener {

	public abstract void messageReceived(IRCChannel channel, String sender,
			String message);
}
