package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class IRCQuitEvent extends IRCEvent {

    public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCQuitEvent";

    private static final HandlerList handlers = new HandlerList();

    private final IRCChannel channel;

    private final String name;

    public IRCQuitEvent(final IRCChannel channel, final String name) {
        this.channel = channel;
        this.name = name;
    }

    public IRCChannel getIRCChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    @Override
    public HandlerList getHandlers() {
        return IRCQuitEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return IRCQuitEvent.handlers;
    }

}
