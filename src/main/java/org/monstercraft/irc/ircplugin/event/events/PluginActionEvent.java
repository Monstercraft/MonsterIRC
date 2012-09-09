package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class PluginActionEvent extends IRCEvent {

    private static final long serialVersionUID = 8708860642802706979L;

    private final IRCChannel channel;

    private final String sender;

    private final String action;

    public PluginActionEvent(final IRCChannel channel, final String sender,
            final String action) {
        this.channel = channel;
        this.sender = sender;
        this.action = action;
    }

    @Override
    public void dispatch(final EventListener el) {
        ((IRCListener) el).onAction(channel, sender, action);
    }

    @Override
    public long getMask() {
        return EventMulticaster.IRC_ACTION_EVENT;
    }

}
