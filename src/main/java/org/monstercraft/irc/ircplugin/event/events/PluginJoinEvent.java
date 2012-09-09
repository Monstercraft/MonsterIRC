package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class PluginJoinEvent extends IRCEvent {

    private static final long serialVersionUID = 8708860642802706979L;

    private final IRCChannel channel;

    private final String user;

    public PluginJoinEvent(final IRCChannel channel, final String user) {
        this.channel = channel;
        this.user = user;
    }

    @Override
    public void dispatch(final EventListener el) {
        ((IRCListener) el).onJoin(channel, user);
    }

    @Override
    public long getMask() {
        return EventMulticaster.IRC_JOIN_EVENT;
    }

}
