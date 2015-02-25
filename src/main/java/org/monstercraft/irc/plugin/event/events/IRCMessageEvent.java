package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class IRCMessageEvent extends IRCEvent {

    public static HandlerList getHandlerList() {
        return IRCMessageEvent.handlers;
    }

    public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCMessageEvent";

    private static final HandlerList handlers = new HandlerList();

    private final IRCChannel channel;

    private final String name;

    private final String message;

    public IRCMessageEvent(final IRCChannel channel, final String name,
            final String message) {
        this.name = name;
        this.channel = channel;
        this.message = message;
    }

    @Override
    public HandlerList getHandlers() {
        return IRCMessageEvent.handlers;
    }

    public IRCChannel getIRCChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

}
