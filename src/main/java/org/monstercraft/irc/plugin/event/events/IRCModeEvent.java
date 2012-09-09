package org.monstercraft.irc.plugin.event.events;

import org.bukkit.event.HandlerList;
import org.monstercraft.irc.plugin.event.IRCEvent;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class IRCModeEvent extends IRCEvent {

    public static final String CUSTOM_TYPE = "org.monstercraft.irc.event.events.IRCModeEvent";

    private static final HandlerList handlers = new HandlerList();

    private final IRCChannel channel;

    private final String name;

    private final String message;

    private final String mode;

    public IRCModeEvent(final IRCChannel channel, final String name,
            final String mode, final String message) {
        this.name = name;
        this.channel = channel;
        this.message = message;
        this.mode = mode;
    }

    public IRCChannel getIRCChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getMode() {
        return mode;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
