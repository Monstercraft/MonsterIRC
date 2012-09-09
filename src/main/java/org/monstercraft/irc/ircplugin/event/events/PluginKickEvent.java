package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class PluginKickEvent extends IRCEvent {

    private static final long serialVersionUID = 8708860642802706979L;

    private final IRCChannel channel;

    private final String user;

    private final String reason;

    private final String kicker;

    public PluginKickEvent(final IRCChannel channel, final String kicker,
            final String user, final String reason) {
        this.channel = channel;
        this.user = user;
        this.reason = reason;
        this.kicker = kicker;
    }

    @Override
    public void dispatch(final EventListener el) {
        ((IRCListener) el).onKick(channel, kicker, user, reason);
    }

    @Override
    public long getMask() {
        return EventMulticaster.IRC_KICK_EVENT;
    }

}
