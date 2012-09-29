package org.monstercraft.irc.plugin.managers.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.util.IRCRank;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCClient;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

public class IRCEventListener implements IRCListener {

    private final MonsterIRC instance;

    public IRCEventListener(final MonsterIRC instance) {
        this.instance = instance;
    }

    @Override
    public void onMessage(final IRCChannel channel, final String sender,
            final String message) {
        if (message.startsWith(Variables.commandPrefix)) {
            instance.getCommandManager().onIRCCommand(sender, message, channel);
        } else if (!Variables.passOnName
                && !Variables.muted.contains(sender.toLowerCase())) {
            IRC.sendMessageToGame(channel, sender, message);
        } else if (Variables.passOnName && message.startsWith(Variables.name)
                && !Variables.muted.contains(sender.toLowerCase())) {
            IRC.sendMessageToGame(channel, sender,
                    message.substring(Variables.name.length()));
        }
    }

    @Override
    public void onPrivateMessage(final String to, final String from,
            final String message) {
        final Player p = Bukkit.getPlayer(to);
        if (p != null) {
            p.sendMessage(ColorUtils.LIGHT_GRAY.getMinecraftColor()
                    + "([IRC] from " + from + "):" + message);
            Variables.reply.put(p, from);
        }
    }

    @Override
    public void onKick(final IRCChannel channel, final String kicker,
            final String user, final String reason) {
        if (!channel.getBlockedEvents().contains("irc_kick")) {
            onMessage(channel, channel.getChannel(), user
                    + " has been kicked from " + channel.getChannel() + " by "
                    + kicker + "(" + reason + ")");
        }
    }

    @Override
    public void onAction(final IRCChannel channel, final String sender,
            final String message) {
        if (!channel.getBlockedEvents().contains("irc_action")) {
            onMessage(channel, channel.getChannel(), "* " + sender + " "
                    + message);
        }
    }

    @Override
    public void onMode(final IRCChannel channel, final String sender,
            final String user, String mode) {
        if (!channel.getBlockedEvents().contains("irc_mode")) {
            onMessage(channel, channel.getChannel(), sender + " gave mode "
                    + mode + " to " + user + ".");
        }
        mode = mode.toLowerCase();
        String add = mode.toLowerCase();
        String remove = mode.toLowerCase();
        if (mode.contains("+") && mode.contains("-")) {
            final int posIndex = mode.indexOf('+');
            final int negIndex = mode.indexOf('-');
            if (posIndex < negIndex) {
                remove = mode.substring(negIndex, mode.length());
                add = mode.substring(0, negIndex);
            } else {
                add = mode.substring(posIndex, mode.length());
                remove = mode.substring(0, posIndex);
            }
        }
        final IRCClient client = channel.getUser(user);
        if (add.contains("+")) {
            if (add.contains("q")) {
                client.addRank(IRCRank.OWNER);
            }
            if (add.contains("o")) {
                client.addRank(IRCRank.OP);
            }
            if (add.contains("h")) {
                client.addRank(IRCRank.HALFOP);
            }
            if (add.contains("a")) {
                client.addRank(IRCRank.ADMIN);
            }
            if (add.contains("v")) {
                client.addRank(IRCRank.VOICE);
            }
        } else if (remove.contains("-")) {
            if (remove.contains("q")) {
                client.removeRank(IRCRank.OWNER);
            }
            if (remove.contains("o")) {
                client.removeRank(IRCRank.OP);
            }
            if (remove.contains("h")) {
                client.removeRank(IRCRank.HALFOP);
            }
            if (remove.contains("a")) {
                client.removeRank(IRCRank.ADMIN);
            }
            if (remove.contains("v")) {
                client.removeRank(IRCRank.VOICE);
            }
        }

    }

    @Override
    public void onPart(final IRCChannel channel, final String user) {
        if (!channel.getBlockedEvents().contains("irc_part")) {
            onMessage(channel, channel.getChannel(), user + " has left "
                    + channel.getChannel());
        }
        channel.removeUser(user);
    }

    @Override
    public void onQuit(final IRCChannel channel, final String user) {
        if (!channel.getBlockedEvents().contains("irc_quit")) {
            onMessage(channel, channel.getChannel(), user + " has quit "
                    + channel.getChannel());
        }
        channel.removeUser(user);
    }

    @Override
    public void onJoin(final IRCChannel channel, final String user,
            final String hostmask) {
        if (!channel.getBlockedEvents().contains("irc_join")) {
            onMessage(channel, channel.getChannel(), user + " has joined "
                    + channel.getChannel());
        }
        try {
            channel.addUser(user, null, hostmask);
        } catch (final Exception e) {
            IRC.debug(e);
        }
    }

    @Override
    public void onNickChange(final IRCChannel channel, final String oldNick,
            final String newNick) {
        if (!channel.getBlockedEvents().contains("irc_nick")) {
            onMessage(channel, channel.getChannel(), oldNick
                    + " is now known as " + newNick);
        }
        channel.getUser(oldNick).updateNick(newNick);
    }

    // unused events
    @Override
    public void onConnect(final IRCServer server) {
    }

    @Override
    public void onDisconnect(final IRCServer server) {
    }

}
