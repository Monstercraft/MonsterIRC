package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class IRCNickEvent extends IRCEvent {

    public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCNickEvent";

    private static final HandlerList handlers = new HandlerList();

    private final IRCChannel channel;

    private final String oldNick;

    private final String newNick;

    public IRCNickEvent(final IRCChannel channel, final String oldNick,
            final String newNick) {
        this.oldNick = oldNick;
        this.channel = channel;
        this.newNick = newNick;
    }

    public IRCChannel getIRCChannel() {
        return channel;
    }

    public String getOldNick() {
        return oldNick;
    }

    public String getNewNick() {
        return newNick;
    }

    @Override
    public HandlerList getHandlers() {
        return IRCNickEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return IRCNickEvent.handlers;
    }

}
