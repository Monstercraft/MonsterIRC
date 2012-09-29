package org.monstercraft.irc.plugin.handles.thread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.ircplugin.event.events.PluginActionEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginJoinEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginKickEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginMessageEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginModeEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginNickChangeEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginPartEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginPrivateMessageEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginQuitEvent;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.event.events.IRCActionEvent;
import org.monstercraft.irc.plugin.event.events.IRCJoinEvent;
import org.monstercraft.irc.plugin.event.events.IRCKickEvent;
import org.monstercraft.irc.plugin.event.events.IRCMessageEvent;
import org.monstercraft.irc.plugin.event.events.IRCModeEvent;
import org.monstercraft.irc.plugin.event.events.IRCNickEvent;
import org.monstercraft.irc.plugin.event.events.IRCPartEvent;
import org.monstercraft.irc.plugin.event.events.IRCPrivateMessageEvent;
import org.monstercraft.irc.plugin.event.events.IRCQuitEvent;
import org.monstercraft.irc.plugin.handles.IRCHandler;
import org.monstercraft.irc.plugin.util.IRCRank;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class InputThread extends Thread implements Runnable {

    private final IRCHandler parent;

    private final MonsterIRC plugin;

    public InputThread(final IRCHandler parent, final MonsterIRC plugin) {
        this.parent = parent;
        this.plugin = plugin;
    }

    private final byte ctcpControl = 1;

    private boolean isCTCP(final String input) {
        if (input.length() != 0) {
            final String message = input.substring(input.indexOf(":", 1) + 1);
            if (message.length() != 0) {
                final char[] messageArray = message.toCharArray();
                return (byte) messageArray[0] == 1
                        && (byte) messageArray[messageArray.length - 1] == 1;
            }
        }
        return false;
    }

    private String getCTCPMessage(final String input) {
        if (input.length() != 0) {
            final String message = input.substring(input.indexOf(":", 1) + 1);
            return message.substring(message.indexOf((char) ctcpControl) + 1,
                    message.indexOf((char) ctcpControl, 1));
        }
        return null;
    }

    @Override
    public void run() {
        try {
            String line = null;
            while (parent.isConnected()
                    && (line = parent.getReader().readLine()) != null) {
                String subline = null;

                if (line.indexOf(" :") != -1) {
                    subline = line.substring(0, line.indexOf(" :"));
                }

                IRC.debug(line, Variables.debug);

                if (line.toLowerCase().startsWith("ping")) {
                    parent.write("PONG " + line.substring(5) + "\r\n");
                    IRC.debug("PONG " + line.substring(5), Variables.debug);
                    continue;
                } else if (isCTCP(line)) {
                    final String _name = line.substring(1, line.indexOf("!"));
                    final String ctcpMsg = getCTCPMessage(line).toUpperCase();
                    if (ctcpMsg.equals("VERSION")) {
                        parent.write("NOTICE "
                                + _name
                                + " :"
                                + (char) ctcpControl
                                + "VERSION "
                                + "MonsterIRC for Bukkit written by Fletch_to_99"
                                + (char) ctcpControl + "\r\n");
                        continue;
                    } else if (ctcpMsg.equals("TIME")) {
                        final SimpleDateFormat sdf = new SimpleDateFormat(
                                "dd MMM yyyy hh:mm:ss zzz");
                        parent.write("NOTICE " + _name + " :"
                                + (char) ctcpControl + " TIME "
                                + sdf.format(new Date()) + (char) ctcpControl
                                + "\r\n");
                        continue;
                    } else if (ctcpMsg.equals("PING")) {
                        parent.write("NOTICE "
                                + _name
                                + " :"
                                + (char) ctcpControl
                                + " PING "
                                + "MonsterIRC by fletch to 99 is to fast to ping."
                                + (char) ctcpControl + "\r\n");
                        continue;
                    } else if (ctcpMsg.equals("FINGER")) {
                        parent.write("NOTICE " + _name + " :"
                                + (char) ctcpControl + " FINGER "
                                + "MonsterIRC written by fletch to 99 slaps "
                                + _name + " across the face."
                                + (char) ctcpControl + "\r\n");
                        continue;
                    }
                }
                for (final IRCChannel c : Variables.channels) {
                    try {
                        if (isCTCP(line)) {
                            final String ctcpMsg = getCTCPMessage(line);
                            if (ctcpMsg.contains("ACTION")) {
                                final String sender = line.substring(1,
                                        line.indexOf("!"));
                                final String action = ctcpMsg.substring(7);
                                plugin.getServer()
                                        .getPluginManager()
                                        .callEvent(
                                                new IRCActionEvent(c, sender,
                                                        action));
                                MonsterIRC.getEventManager()
                                        .dispatchEvent(
                                                new PluginActionEvent(c,
                                                        sender, action));
                                break;
                            }
                        } else if (subline != null) {
                            if (subline.toLowerCase()
                                    .contains(
                                            ("PRIVMSG " + c.getChannel())
                                                    .toLowerCase())) {
                                final String sender = line.substring(1,
                                        line.indexOf("!"));
                                final String message = line.substring(line
                                        .indexOf(" :") + 2);
                                plugin.getServer()
                                        .getPluginManager()
                                        .callEvent(
                                                new IRCMessageEvent(c, sender,
                                                        message));
                                MonsterIRC.getEventManager().dispatchEvent(
                                        new PluginMessageEvent(c, sender,
                                                message));
                                break;
                            } else if (subline.toLowerCase().contains("quit")) {
                                final String sender = line.substring(1,
                                        line.indexOf("!"));
                                plugin.getServer().getPluginManager()
                                        .callEvent(new IRCQuitEvent(c, sender));
                                MonsterIRC.getEventManager().dispatchEvent(
                                        new PluginQuitEvent(c, sender));
                                break;
                            } else if (subline.toLowerCase().contains(" nick ")) {
                                final String sender = line.substring(1,
                                        line.indexOf("!"));
                                final String newNick = line.substring(line
                                        .indexOf(" :") + 2);
                                plugin.getServer()
                                        .getPluginManager()
                                        .callEvent(
                                                new IRCNickEvent(c, sender,
                                                        newNick));
                                MonsterIRC.getEventManager().dispatchEvent(
                                        new PluginNickChangeEvent(c, sender,
                                                newNick));
                                break;
                            } else if (subline.toLowerCase().contains(
                                    ("KICK " + c.getChannel()).toLowerCase())) {
                                final String kicker = line.substring(1,
                                        line.indexOf("!"));
                                final String user = line.substring(
                                        line.toLowerCase().indexOf(
                                                c.getChannel().toLowerCase())
                                                + c.getChannel().length() + 1,
                                        line.indexOf(" :") - 1);
                                final String reason = line.substring(line
                                        .indexOf(" :") + 2);
                                plugin.getServer()
                                        .getPluginManager()
                                        .callEvent(
                                                new IRCKickEvent(c, kicker,
                                                        user, reason));
                                MonsterIRC.getEventManager().dispatchEvent(
                                        new PluginKickEvent(c, kicker, user,
                                                reason));
                                break;
                            } else if (subline.toLowerCase().contains("353")
                                    && subline.toLowerCase().contains(
                                            c.getChannel().toLowerCase())) {
                                final String split = line.substring(line
                                        .indexOf(" :") + 2);
                                final StringTokenizer st = new StringTokenizer(
                                        split);
                                final ArrayList<String> list = new ArrayList<String>();
                                while (st.hasMoreTokens()) {
                                    list.add(st.nextToken());
                                }
                                for (final String s : list) {
                                    IRCRank rank = IRCRank.USER;
                                    if (s.startsWith("~")) {
                                        rank = IRCRank.OWNER;
                                    } else if (s.contains("&")) {
                                        rank = IRCRank.ADMIN;
                                    } else if (s.contains("@")) {
                                        rank = IRCRank.OP;
                                    } else if (s.contains("%")) {
                                        rank = IRCRank.HALFOP;
                                    } else if (s.contains("+")) {
                                        rank = IRCRank.VOICE;
                                    }
                                    if (rank.equals(IRCRank.USER)) {
                                        c.addUser(s, rank, "");
                                    } else {
                                        c.addUser(s.substring(1), rank, null);
                                    }
                                }
                                break;
                            }
                        } else if (line.toLowerCase().contains(
                                ("MODE " + c.getChannel()).toLowerCase())) {
                            final String sender = line.substring(1,
                                    line.indexOf("!"));
                            final String mode = line.substring(
                                    line.toLowerCase().indexOf(
                                            c.getChannel().toLowerCase())
                                            + 1 + c.getChannel().length(),
                                    line.toLowerCase().indexOf(
                                            c.getChannel().toLowerCase())
                                            + 3 + c.getChannel().length());
                            final String user = line.substring(line
                                    .toLowerCase().indexOf(
                                            c.getChannel().toLowerCase())
                                    + c.getChannel().length() + 4);
                            plugin.getServer()
                                    .getPluginManager()
                                    .callEvent(
                                            new IRCModeEvent(c, sender, user,
                                                    mode));
                            MonsterIRC.getEventManager().dispatchEvent(
                                    new PluginModeEvent(c, sender, user, mode));
                            break;
                        } else if (line.toLowerCase().contains(
                                ("PART " + c.getChannel()).toLowerCase())) {
                            final String name = line.substring(1,
                                    line.indexOf("!"));
                            if (name.equalsIgnoreCase(parent.getServer()
                                    .getNick())) {
                                break;
                            }
                            final IRCPartEvent pevent = new IRCPartEvent(c,
                                    name);
                            plugin.getServer().getPluginManager()
                                    .callEvent(pevent);
                            final PluginPartEvent ppe = new PluginPartEvent(c,
                                    name);
                            MonsterIRC.getEventManager().dispatchEvent(ppe);
                            break;
                        } else if (line.toLowerCase().contains(
                                ("JOIN " + c.getChannel()).toLowerCase())) {
                            final String name = line.substring(1,
                                    line.indexOf("!"));
                            if (name.equalsIgnoreCase(parent.getServer()
                                    .getNick())) {
                                break;
                            }
                            final String longName = line.substring(
                                    line.indexOf('!'), line.indexOf(" J"));
                            final String hostmask = longName.substring(longName
                                    .indexOf('@') + 1);
                            plugin.getServer().getPluginManager()
                                    .callEvent(new IRCJoinEvent(c, name));
                            MonsterIRC.getEventManager().dispatchEvent(
                                    new PluginJoinEvent(c, name, hostmask));
                            break;
                        }
                    } catch (final Exception e) {
                        IRC.debug(e);
                    }
                }

                if (line.toLowerCase().contains(
                        ("PRIVMSG " + parent.getServer().getNick())
                                .toLowerCase())) {
                    final String sender = line.substring(1, line.indexOf("!"));
                    final String message = line
                            .substring(line.indexOf(" :") + 2);
                    if (message.contains(":") && message.indexOf(":") > 2) {
                        final String to = message.substring(0,
                                message.indexOf(":"));
                        final String msg = message.substring(message
                                .indexOf(":") + 1);
                        if (to != null && message != null && msg != null
                                && sender != null) {
                            for (final Player p : Bukkit.getServer()
                                    .getOnlinePlayers()) {
                                if (p.getName().equalsIgnoreCase(to)) {
                                    plugin.getServer()
                                            .getPluginManager()
                                            .callEvent(
                                                    new IRCPrivateMessageEvent(
                                                            to, sender, msg));
                                    MonsterIRC.getEventManager().dispatchEvent(
                                            new PluginPrivateMessageEvent(to,
                                                    sender, msg));
                                }
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
        }
    }
}
