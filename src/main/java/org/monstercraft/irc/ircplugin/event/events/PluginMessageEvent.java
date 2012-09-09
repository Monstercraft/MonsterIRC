package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class PluginMessageEvent extends IRCEvent {

    private static final long serialVersionUID = 8708860642802706979L;

    private final IRCChannel channel;

    private final String sender;

    private final String message;

    public PluginMessageEvent(final IRCChannel channel, final String sender,
            final String message) {
        this.channel = channel;
        this.sender = sender;
        this.message = message;
    }

    @Override
    public void dispatch(final EventListener el) {
        ((IRCListener) el).onMessage(channel, sender, message);
    }

    @Override
    public long getMask() {
        return EventMulticaster.IRC_MESSAGE_EVENT;
    }

}
