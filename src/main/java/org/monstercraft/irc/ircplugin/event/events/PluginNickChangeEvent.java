package org.monstercraft.irc.ircplugin.event.events;

import java.util.EventListener;

import org.monstercraft.irc.ircplugin.event.EventMulticaster;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class PluginNickChangeEvent extends IRCEvent {

    private static final long serialVersionUID = 8708860642802706979L;

    private final String oldNick;

    private final String newNick;

    private final IRCChannel channel;

    public PluginNickChangeEvent(final IRCChannel channel,
            final String oldNick, final String newNick) {
        this.oldNick = oldNick;
        this.newNick = newNick;
        this.channel = channel;
    }

    @Override
    public void dispatch(final EventListener el) {
        ((IRCListener) el).onNickChange(channel, oldNick, newNick);
    }

    @Override
    public long getMask() {
        return EventMulticaster.IRC_NICK_EVENT;
    }

}
