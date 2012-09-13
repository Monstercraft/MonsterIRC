package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;

public class IRCPrivateMessageEvent extends IRCEvent {

    public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCPrivateMessageEvent";

    private static final HandlerList handlers = new HandlerList();

    private final String from;

    private final String to;

    private final String message;

    public IRCPrivateMessageEvent(final String to, final String from,
            final String message) {
        this.to = to;
        this.from = from;
        this.message = message;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public HandlerList getHandlers() {
        return IRCPrivateMessageEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return IRCPrivateMessageEvent.handlers;
    }

}
