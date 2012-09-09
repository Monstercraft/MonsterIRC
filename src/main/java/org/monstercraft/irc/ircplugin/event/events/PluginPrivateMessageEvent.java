package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;

public class PluginPrivateMessageEvent extends IRCEvent {

    private static final long serialVersionUID = 8708860642802706979L;

    private final String to;

    private final String from;

    private final String message;

    public PluginPrivateMessageEvent(final String to, final String from,
            final String message) {
        this.to = to;
        this.from = from;
        this.message = message;
    }

    @Override
    public void dispatch(final EventListener el) {
        ((IRCListener) el).onPrivateMessage(to, from, message);
    }

    @Override
    public long getMask() {
        return EventMulticaster.IRC_PRIVATE_MESSAGE_EVENT;
    }

}
