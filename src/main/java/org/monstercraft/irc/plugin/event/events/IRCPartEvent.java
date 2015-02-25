package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class IRCPartEvent extends IRCEvent {

    public static HandlerList getHandlerList() {
        return IRCPartEvent.handlers;
    }

    public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCPartEvent";

    private static final HandlerList handlers = new HandlerList();

    private final IRCChannel channel;

    private final String user;

    public IRCPartEvent(final IRCChannel channel, final String user) {
        this.channel = channel;
        this.user = user;
    }

    @Override
    public HandlerList getHandlers() {
        return IRCPartEvent.handlers;
    }

    public IRCChannel getIRCChannel() {
        return channel;
    }

    public String getUser() {
        return user;
    }

}
