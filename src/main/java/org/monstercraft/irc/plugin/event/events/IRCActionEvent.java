package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class IRCActionEvent extends IRCEvent {

    public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCActionEvent";

    private static final HandlerList handlers = new HandlerList();

    private final IRCChannel channel;

    private final String sender;

    private final String action;

    public IRCActionEvent(final IRCChannel channel, final String sender,
            final String action) {
        this.channel = channel;
        this.sender = sender;
        this.action = action;
    }

    public IRCChannel getChannel() {
        return channel;
    }

    public String getSender() {
        return sender;
    }

    public String getAction() {
        return action;
    }

    @Override
    public HandlerList getHandlers() {
        return IRCActionEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return IRCActionEvent.handlers;
    }

}
