package org.monstercraft.irc.ircplugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.towny.TownyChatListener;
import org.monstercraft.irc.plugin.util.ChatType;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.util.IRCRank;
import org.monstercraft.irc.plugin.util.StringUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCServer;
import org.monstercraft.support.MonsterTickets;

import com.gmail.nossr50.util.Users;

public class IRC {

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param channel
     *            The channel to send it to.
     * @param message
     *            The message to send.
     */
    public static void ban(final String channel, final String nick) {
        MonsterIRC.getHandleManager().getIRCHandler().ban(nick, channel);
    }

    /**
     * Logs debugging messages to the console.
     *
     * @param error
     *            The message to print.
     */
    public static void debug(final Exception error) {
        IRC.logger.log(Level.SEVERE, "[MonsterIRC - Critical error detected!]");
        error.printStackTrace();
    }

    /**
     * Logs debugging messages to the console.
     *
     * @param error
     *            The message to print.
     */
    public static void debug(final String error) {
        if (Variables.debug) {
            IRC.logger.log(Level.WARNING, "[MonsterIRC - Debug] " + error);
        }
    }

    /**
     * Logs debugging messages to the console.
     *
     * @param error
     *            The message to print.
     */
    public static void debug(final String error, final boolean console) {
        if (console) {
            IRC.logger.log(Level.WARNING, "[MonsterIRC - Debug] " + error);
        }
    }

    /**
     *
     * @param channel
     * @return
     */
    public static IRCChannel getChannel(final String channel) {
        for (final IRCChannel c : MonsterIRC.getChannels()) {
            if (c.getChannel().equalsIgnoreCase(channel)) {
                return c;
            }
        }
        return null;
    }

    public static Plugin getIRCPlugin() {
        return Bukkit.getServer().getPluginManager().getPlugin("MonsterIRC");
    }

    /**
     * Fetches the logger.
     *
     * @return The logger.
     */
    public static Logger getLogger() {
        return IRC.logger;
    }

    public static IRCServer getServer() {
        return IRC.server;
    }

    /**
     * Fetches the list of Admins in the current IRC channel.
     *
     * @return True if the user is admin; otherwise false.
     */
    public static boolean isAdmin(final IRCChannel channel, final String sender) {
        return channel.getUser(sender).getRanks().contains(IRCRank.ADMIN);
    }

    /**
     * Fetches the list of Admins in the current IRC channel.
     *
     * @return True if the user is admin; otherwise false.
     */
    public static boolean isAdmin(final String channel, final String sender) {
        final IRCChannel c = IRC.getChannel(channel);
        if (c != null) {
            return c.getUser(sender).getRanks().contains(IRCRank.ADMIN);
        }
        return false;
    }

    /**
     * Fetches the list of Operaters in the current IRC channel.
     *
     * @return The list of Operators.
     */
    public static boolean isHalfOP(final IRCChannel channel, final String sender) {
        return channel.getUser(sender).getRanks().contains(IRCRank.HALFOP);
    }

    /**
     * Fetches the list of Operaters in the current IRC channel.
     *
     * @return The list of Operators.
     */
    public static boolean isHalfOP(final String channel, final String sender) {
        final IRCChannel c = IRC.getChannel(channel);
        if (c != null) {
            return c.getUser(sender).getRanks().contains(IRCRank.HALFOP);
        }
        return false;
    }

    /**
     * Fetches the list of Operaters in the current IRC channel.
     *
     * @return The list of Operators.
     */
    public static boolean isOp(final IRCChannel channel, final String sender) {
        return channel.getUser(sender).getRanks().contains(IRCRank.OP);
    }

    /**
     * Fetches the list of Operaters in the current IRC channel.
     *
     * @return The list of Operators.
     */
    public static boolean isOp(final String channel, final String sender) {
        final IRCChannel c = IRC.getChannel(channel);
        if (c != null) {
            return c.getUser(sender).getRanks().contains(IRCRank.OP);
        }
        return false;
    }

    /**
     * Fetches the list of Operaters in the current IRC channel.
     *
     * @return The list of Operators.
     */
    public static boolean isOwner(final IRCChannel channel, final String sender) {
        return channel.getUser(sender).getRanks().contains(IRCRank.OWNER);
    }

    /**
     * Fetches the list of Operaters in the current IRC channel.
     *
     * @return The list of Operators.
     */
    public static boolean isOwner(final String channel, final String sender) {
        final IRCChannel c = IRC.getChannel(channel);
        if (c != null) {
            return c.getUser(sender).getRanks().contains(IRCRank.OWNER);
        }
        return false;
    }

    /**
     * Fetches the list of Voices in the current IRC channel.
     *
     * @return The list of Voices.
     */
    public static boolean isVoice(final IRCChannel channel, final String sender) {
        return channel.getUser(sender).getRanks().contains(IRCRank.VOICE);
    }

    /**
     * Fetches the list of Voices in the current IRC channel.
     *
     * @return The list of Voices.
     */
    public static boolean isVoice(final String channel, final String sender) {
        final IRCChannel c = IRC.getChannel(channel);
        if (c != null) {
            return c.getUser(sender).getRanks().contains(IRCRank.VOICE);
        }
        return false;
    }

    public static boolean isVoicePlus(final IRCChannel channel,
            final String sender) {
        return channel.getUser(sender).getHighestRank().toInt() >= IRCRank.VOICE
                .toInt();
    }

    public static boolean isVoicePlus(final String channel, final String sender) {
        return IRC.isVoicePlus(IRC.getChannel(channel), sender);
    }

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param channel
     *            The channel to send it to.
     * @param message
     *            The message to send.
     */
    public static void kick(final String channel, final String nick,
            final String reason) {
        MonsterIRC.getHandleManager().getIRCHandler()
                .kick(nick, channel, reason);
    }

    /**
     * Logs a message to the console.
     *
     * @param msg
     *            The message to print.
     */
    public static void log(final String msg) {
        IRC.logger.log(Level.INFO, "[MonsterIRC] " + msg);
    }

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param channel
     *            The channel to send it to.
     * @param message
     *            The message to send.
     */
    public static void mode(final String channel, final String nick,
            final String mode) {
        MonsterIRC.getHandleManager().getIRCHandler().mode(nick, channel, mode);
    }

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param channel
     *            The channel to send it to.
     * @param message
     *            The message to send.
     */
    public static void sendMessageToChannel(final IRCChannel channel,
            final String message) {
        MonsterIRC.getHandleManager().getIRCHandler()
                .sendMessage(channel.getChannel(), message);
    }

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param channel
     *            The channel to send it to.
     * @param message
     *            The message to send.
     */
    public static void sendMessageToChannel(final String channel,
            final String message) {
        for (final IRCChannel c : MonsterIRC.getChannels()) {
            if (c.getChannel().equalsIgnoreCase(channel)) {
                IRC.sendMessageToChannel(c, message);
                return;
            }
        }
    }

    /**
     * Handles a message accoradingly.
     *
     * @param c
     *            The IRCChannel to handle the message for.
     * @param name
     *            The sender's name.
     * @param message
     *            The message to handle.
     */
    public static void sendMessageToGame(final IRCChannel c, final String name,
            final String message) {
        if (c.getBlockedEvents().contains("irc_message")) {
            return;
        }
        Variables.linesToGame++;
        try {
            if (c.getChatType() == ChatType.MCMMOADMINCHAT) {
                if (Bukkit.getServer().getPluginManager().getPlugin("mcMMO") != null) {
                    final String format = ColorUtils.CYAN.getMinecraftColor()
                            + "{" + ColorUtils.WHITE.getMinecraftColor()
                            + "[IRC] " + StringUtils.getPrefix(name)
                            + StringUtils.getDisplayName(name)
                            + StringUtils.getSuffix(name)
                            + ColorUtils.CYAN.getMinecraftColor() + "} "
                            + message;
                    for (final Player p : IRC.getIRCPlugin().getServer()
                            .getOnlinePlayers()) {
                        if (p.isOp()
                                || Users.getProfile(p.getName())
                                        .getAdminChatMode()) {
                            p.sendMessage(ColorUtils.formatIRCtoGame(format,
                                    message));
                        }
                    }
                }
            } else if (c.getChatType() == ChatType.MTADMINCHAT) {
                if (Bukkit.getServer().getPluginManager()
                        .getPlugin("MonsterTickets") != null) {
                    MonsterTickets.sendAdminChatMessage(name, message);
                }
            } else if (c.getChatType() == ChatType.HEROCHAT) {
                if (c.getHeroChatChannel() != null) {
                    c.getHeroChatChannel()
                            .announce(
                                    ColorUtils
                                            .formatIRCtoGame(
                                                    Variables.mcformat
                                                            .replace(
                                                                    "{name}",
                                                                    StringUtils
                                                                            .getDisplayName(name))
                                                            .replace(
                                                                    "{displayName}",
                                                                    StringUtils
                                                                            .getDisplayName(name))
                                                            .replace(
                                                                    "{HCchannelColor}",
                                                                    c.getHeroChatChannel()
                                                                            .getColor()
                                                                            .toString())
                                                            .replace(
                                                                    "{channel}",
                                                                    c.getChannel())
                                                            .replace(
                                                                    "{herochatTag}",
                                                                    "")
                                                            .replace(
                                                                    "{herochatName}",
                                                                    "")
                                                            .replace(
                                                                    "{message}",
                                                                    message)
                                                            .replace(
                                                                    "{prefix}",
                                                                    StringUtils
                                                                            .getPrefix(name))
                                                            .replace(
                                                                    "{suffix}",
                                                                    StringUtils
                                                                            .getSuffix(name))
                                                            .replace(
                                                                    "{groupPrefix}",
                                                                    StringUtils
                                                                            .getGroupPrefix(name))
                                                            .replace(
                                                                    "{groupSuffix}",
                                                                    StringUtils
                                                                            .getGroupSuffix(name))
                                                            .replace(
                                                                    "{mvWorld}",
                                                                    StringUtils
                                                                            .getMvWorldAlias(name))
                                                            .replace(
                                                                    "{mvColor}",
                                                                    StringUtils
                                                                            .getMvWorldColor(name))
                                                            .replace(
                                                                    "{world}",
                                                                    StringUtils
                                                                            .getWorld(name)),
                                                    message));
                } else {
                    IRC.log("Invalid herochat channel detected for "
                            + c.getChannel());
                }
            } else if (c.getChatType() == ChatType.GLOBAL) {
                IRC.getIRCPlugin()
                        .getServer()
                        .broadcastMessage(
                                ColorUtils.formatIRCtoGame(
                                        Variables.mcformat
                                                .replace(
                                                        "{name}",
                                                        StringUtils
                                                                .getDisplayName(name))
                                                .replace(
                                                        "{displayName}",
                                                        StringUtils
                                                                .getDisplayName(name))
                                                .replace(
                                                        "{message}",
                                                        ColorUtils.WHITE
                                                                .getMinecraftColor()
                                                                + message)
                                                .replace("{HCchannelColor}", "")
                                                .replace("{herochatTag}", "")
                                                .replace("{herochatName}", "")
                                                .replace("{channel}",
                                                        c.getChannel())
                                                .replace(
                                                        "{prefix}",
                                                        StringUtils
                                                                .getPrefix(name))
                                                .replace(
                                                        "{suffix}",
                                                        StringUtils
                                                                .getSuffix(name))
                                                .replace(
                                                        "{groupPrefix}",
                                                        StringUtils
                                                                .getGroupPrefix(name))
                                                .replace(
                                                        "{groupSuffix}",
                                                        StringUtils
                                                                .getGroupSuffix(name))
                                                .replace(
                                                        "{mvWorld}",
                                                        StringUtils
                                                                .getMvWorldAlias(name))
                                                .replace(
                                                        "{mvColor}",
                                                        StringUtils
                                                                .getMvWorldColor(name))
                                                .replace(
                                                        "{world}",
                                                        StringUtils
                                                                .getWorld(name)),
                                        message));
            } else if (c.getChatType() == ChatType.TOWNYCHAT) {
                TownyChatListener.sendMessage(
                        ColorUtils.formatIRCtoGame(
                                Variables.mcformat
                                        .replace(
                                                "{name}",
                                                StringUtils
                                                        .getDisplayName(name))
                                        .replace(
                                                "{displayName}",
                                                StringUtils
                                                        .getDisplayName(name))
                                        .replace(
                                                "{herochatTag}",
                                                c.getTownyChannel()
                                                        .getChannelTag())
                                        .replace("{herochatName}", "")
                                        .replace(
                                                "{HCchannelColor}",
                                                c.getTownyChannel()
                                                        .getMessageColour())
                                        .replace("{channel}", c.getChannel())
                                        .replace("{message}", message)
                                        .replace("{prefix}",
                                                StringUtils.getPrefix(name))
                                        .replace("{suffix}",
                                                StringUtils.getSuffix(name))
                                        .replace(
                                                "{groupPrefix}",
                                                StringUtils
                                                        .getGroupPrefix(name))
                                        .replace(
                                                "{groupSuffix}",
                                                StringUtils
                                                        .getGroupSuffix(name))
                                        .replace(
                                                "{mvWorld}",
                                                StringUtils
                                                        .getMvWorldAlias(name))
                                        .replace(
                                                "{mvColor}",
                                                StringUtils
                                                        .getMvWorldColor(name))
                                        .replace("{world}",
                                                StringUtils.getWorld(name)),
                                message), c);
            }
        } catch (final Exception e) {
            IRC.debug(e);
        }
    }

    /**
     * Handles a message accoradingly.
     *
     * @param c
     *            The IRCChannel to handle the message for.
     * @param name
     *            The sender's name.
     * @param message
     *            The message to handle.
     */
    public static void sendMessageToGame(final String IRCChannel,
            final String sender, final String message) {
        for (final IRCChannel c : MonsterIRC.getChannels()) {
            if (c.getChannel().equalsIgnoreCase(IRCChannel)) {
                IRC.sendMessageToGame(c, sender, message);
            }
        }
    }

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param to
     *            The person to send the message to.
     * @param message
     *            The message to send.
     */
    public static void sendMessageToUser(final String to, final String message) {
        MonsterIRC.getHandleManager().getIRCHandler().sendMessage(to, message);
    }

    /**
     * Sends a message to a user on the MonsterIRC server.
     *
     * @param to
     *            The user to send it to.
     * @param message
     *            The message to send.
     */
    public static void sendNotice(final String to, final String message) {
        MonsterIRC.getHandleManager().getIRCHandler().sendNotice(to, message);
    }

    /**
     * Sends a raw message to the IRC server.
     *
     * @param RawMessage
     *            The message to send.
     */
    public static void sendRawLine(final String Line) {
        MonsterIRC.getHandleManager().getIRCHandler().sendRaw(Line);
    }

    public static void setServer(final IRCServer server) {
        IRC.server = server;
    }

    private final static Logger logger = Logger.getLogger(IRC.class
            .getSimpleName());

    private static IRCServer server;
}