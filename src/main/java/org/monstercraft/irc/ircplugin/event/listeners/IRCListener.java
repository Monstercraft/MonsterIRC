package org.monstercraft.irc.ircplugin.event.listeners;

import java.util.EventListener;

import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

public interface IRCListener extends EventListener {

    public abstract void onAction(IRCChannel channel, String sender,
            String message);

    public abstract void onConnect(IRCServer server);

    public abstract void onDisconnect(IRCServer server);

    public abstract void onJoin(IRCChannel channel, String user, String message);

    public abstract void onKick(IRCChannel channel, String kicker, String user,
            String reason);

    public abstract void onMessage(IRCChannel channel, String sender,
            String message);

    public abstract void onMode(IRCChannel channel, String sender, String user,
            String mode);

    public abstract void onNickChange(IRCChannel channel, String oldNick,
            String newNick);

    public abstract void onPart(IRCChannel channel, String user);

    public abstract void onPrivateMessage(String to, String from, String message);

    public abstract void onQuit(IRCChannel channel, String user);
}
