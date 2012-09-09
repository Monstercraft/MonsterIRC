package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

public class PluginDisconnectEvent extends IRCEvent {

    private static final long serialVersionUID = 8708860642802706979L;

    private final IRCServer server;

    public PluginDisconnectEvent(final IRCServer server) {
        this.server = server;
    }

    @Override
    public void dispatch(final EventListener el) {
        ((IRCListener) el).onDisconnect(server);
    }

    @Override
    public long getMask() {
        return EventMulticaster.IRC_DISCONNECT_EVENT;
    }

}
