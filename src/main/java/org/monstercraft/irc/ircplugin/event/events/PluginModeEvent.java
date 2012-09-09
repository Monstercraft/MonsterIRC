package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class PluginModeEvent extends IRCEvent {

    private static final long serialVersionUID = 8708860642802706979L;

    private final IRCChannel channel;

    private final String user;

    private final String mode;

    private final String sender;

    public PluginModeEvent(final IRCChannel channel, final String sender,
            final String user, final String mode) {
        this.sender = sender;
        this.mode = mode;
        this.channel = channel;
        this.user = user;
    }

    @Override
    public void dispatch(final EventListener el) {
        ((IRCListener) el).onMode(channel, sender, user, mode);
    }

    @Override
    public long getMask() {
        return EventMulticaster.IRC_MODE_EVENT;
    }

}
