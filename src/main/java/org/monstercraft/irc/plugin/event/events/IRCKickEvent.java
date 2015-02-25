package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class IRCKickEvent extends IRCEvent {

    public static HandlerList getHandlerList() {
        return IRCKickEvent.handlers;
    }

    public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCKickEvent";

    private static final HandlerList handlers = new HandlerList();

    private final IRCChannel channel;

    private final String name;

    private String kicker;

    private String reason;

    public IRCKickEvent(final IRCChannel channel, final String kicker,
            final String name, final String reason) {
        this.name = name;
        this.channel = channel;
    }

    @Override
    public HandlerList getHandlers() {
        return IRCKickEvent.handlers;
    }

    public IRCChannel getIRCChannel() {
        return channel;
    }

    public String getKicker() {
        return kicker;
    }

    public String getName() {
        return name;
    }

    public String getReason() {
        return reason;
    }

}
