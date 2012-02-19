package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class PluginActionEvent extends IRCEvent {

	private static final long serialVersionUID = 8708860642802706979L;

	private IRCChannel channel;

	private String sender;

	private String action;

	public PluginActionEvent(IRCChannel channel, String sender, String action) {
		this.channel = channel;
		this.sender = sender;
		this.action = action;
	}

	@Override
	public void dispatch(EventListener el) {
		((IRCListener) el).onAction(channel, sender, action);
	}

	@Override
	public long getMask() {
		return EventMulticaster.IRC_ACTION_EVENT;
	}

}
