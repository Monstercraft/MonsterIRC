package org.monstercraft.irc.plugin.handles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.ircplugin.event.events.PluginConnectEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginDisconnectEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginJoinEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginPartEvent;
import org.monstercraft.irc.plugin.Configuration;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.event.events.IRCConnectEvent;
import org.monstercraft.irc.plugin.event.events.IRCDisconnectEvent;
import org.monstercraft.irc.plugin.event.events.IRCJoinEvent;
import org.monstercraft.irc.plugin.event.events.IRCPartEvent;
import org.monstercraft.irc.plugin.handles.thread.InputThread;
import org.monstercraft.irc.plugin.handles.thread.OutputThread;
import org.monstercraft.irc.plugin.util.StringUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCClient;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

/**
 * This handles all of the IRC related stuff.
 *
 * @author fletch_to_99 <fletchto99@hotmail.com>
 *
 */
public class IRCHandler {

    private BufferedWriter output = null;
    private BufferedReader input = null;
    private Socket connection = null;
    private Thread watch = null;
    private Thread print = null;
    private IRCServer server = null;
    private final MonsterIRC plugin;
    private final LinkedList<String> outputQueue = new LinkedList<String>();

    /**
     * Creates an instance of the IRCHandler class.
     *
     * @param plugin
     *            The parent plugin.
     */
    public IRCHandler(final MonsterIRC plugin) {
        this.plugin = plugin;
    }

    /**
     * Bans a user from the IRC channel if the bot is OP.
     *
     * @param Nick
     *            The user to ban.
     * @param channel
     *            The channel to ban in.
     */
    public void ban(final String nick, final String channel) {
        if (this.isConnected()) {
            try {
                IRCClient client;
                if ((client = IRC.getChannel(channel).getUser(nick)) != null) {
                    if (client.getHostmask() != null) {
                        if (!client.getHostmask().equalsIgnoreCase("")) {
                            this.kick(nick, channel, "Bye.");
                            this.write("MODE " + channel + " +b "
                                    + client.getHostmask());
                            return;
                        }
                    }
                }
                this.kick(nick, channel, "Bye.");
                this.write("MODE " + channel + " +b " + nick);
            } catch (final IOException e) {
                IRC.debug(e);
            }
        }
    }

    /**
     * Changes the nickname of the IRC bot.
     *
     * @param Nick
     *            The name to change to.
     */
    public void changeNick(final String Nick) {
        if (this.isConnected()) {
            try {
                this.write("NICK " + Nick);
            } catch (final IOException e) {
                IRC.debug(e);
            }
        }
    }

    /**
     * Connects to an IRC server then a channel.
     *
     * @param server
     *            The server to connect to.
     * @return True if connected successfully; otherwise false.
     */
    public boolean connect(final IRCServer server) {
        this.server = server;
        if (connection != null) {
            if (connection.isConnected()) {
                IRC.log("Attempting to disconnect before re-connecting!");
                this.disconnect();
            }
        }
        String line = null;
        long ping = -1;
        int tries = 0;
        for (int i = 0; i < server.getRetrys(); i++) {
            ping = Configuration.ping(server.getServer(), server.getPort(),
                    server.getTimeout());
            if (ping < server.getTimeout() + 1 && ping != -1) {
                tries = i;
                break;
            }
        }
        if (ping < server.getTimeout() + 1 && ping != -1) {
            IRC.log("The IRC server took " + ping + " MS to respond with "
                    + tries + " retrys.");
            try {
                connection = null;
                connection = new Socket(InetAddress.getByName(server
                        .getServer()), server.getPort());
                output = new BufferedWriter(new OutputStreamWriter(
                        connection.getOutputStream()));
                input = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                IRC.log("Attempting to connect to chat.");
                if (!server.getPassword().equalsIgnoreCase("")) {
                    this.write("PASS " + server.getPassword());
                }
                this.write("NICK " + server.getNick());
                this.write("USER " + server.getNick() + " 8 * :"
                        + plugin.getDescription().getVersion());
                IRC.log("Processing connection....");
                while ((line = this.getReader().readLine()) != null) {
                    IRC.debug(line, Variables.debug);
                    if (line.contains("004") || line.contains("376")) {
                        break;
                    } else if (line.contains("433")) {
                        if (!server.isIdentifing()) {
                            IRC.log("Your nickname is already in use, please switch it");
                            IRC.log("using \"nick [NAME]\" and try to connect again.");
                            this.disconnect();
                            return false;
                        } else {
                            IRC.log("Sending ghost command....");
                            this.write("NICKSERV GHOST " + server.getNick()
                                    + " " + server.getNickservPassword());

                            continue;
                        }
                    } else if (line.toLowerCase().startsWith("ping ")) {
                        this.write("PONG " + line.substring(5));

                        continue;
                    }
                }
                if (server.isIdentifing()) {
                    IRC.log("Identifying with Nickserv....");
                    this.write("NICKSERV IDENTIFY "
                            + server.getNickservPassword());

                }
                for (final String s : server.getConnectCommands()) {
                    this.write(s);

                }
                final IRCConnectEvent cevent = new IRCConnectEvent(server);
                plugin.getServer().getPluginManager().callEvent(cevent);
                final PluginConnectEvent pce = new PluginConnectEvent(server);
                MonsterIRC.getEventManager().dispatchEvent(pce);
                for (final IRCChannel c : Variables.channels) {
                    if (c.isAutoJoin()) {
                        MonsterIRC.getHandleManager().getIRCHandler().join(c);
                    }
                }
                watch = new InputThread(this, plugin);
                watch.setDaemon(true);
                watch.setPriority(Thread.MAX_PRIORITY);
                watch.start();
                print = new OutputThread(this);
                print.setDaemon(true);
                print.setPriority(Thread.MAX_PRIORITY);
                print.start();
            } catch (final Exception e) {
                IRC.log("Failed to connect to IRC! Try again in about 1 minute!");
                this.disconnect();
            }
        } else {
            IRC.log("The IRC server seems to be down or running slowly!");
            IRC.log("To try conencting again run the command /irc connect");
            IRC.log("Your ping is:" + ping);
            return false;
        }
        return this.isConnected();
    }

    /**
     * Disconnects a user from the IRC server.
     *
     * @return True if we disconnect successfully; otherwise false.
     */
    public boolean disconnect() {
        if (this.isConnected()) {
            if (Variables.partOnDC) {
                for (final IRCChannel c : Variables.channels) {
                    this.part(c);
                }
            }
            try {
                this.write("QUIT Leaving.");
                input.close();
                outputQueue.clear();
                output.close();
                this.getReader().close();
                connection.close();
                IRC.log("Successfully disconnected from IRC.");
            } catch (final Exception e) {
                outputQueue.clear();
                IRC.log("Successfully disconnected from IRC.");
            }
        }
        final IRCDisconnectEvent devent = new IRCDisconnectEvent(server);
        plugin.getServer().getPluginManager().callEvent(devent);
        final PluginDisconnectEvent pde = new PluginDisconnectEvent(server);
        MonsterIRC.getEventManager().dispatchEvent(pde);
        return !this.isConnected();
    }

    public Queue<String> getQueue() {
        return outputQueue;
    }

    public BufferedReader getReader() {
        return input;
    }

    public IRCServer getServer() {
        return server;
    }

    /**
     * Checks if the user is connected to an IRC server.
     *
     * @return True if conencted to an IRC server; othewise false.
     */
    public boolean isConnected() {
        return connection != null ? connection.isConnected()
                && connection.isBound() : false;
    }

    /**
     * Joins an IRC channel on that server.
     *
     * @param channel
     *            The channel to join.
     * @throws IOException
     */
    public void join(final IRCChannel channel) throws IOException {
        if (channel.getPassword() != null && channel.getPassword() != "") {
            final String pass = "JOIN " + channel.getChannel() + " "
                    + channel.getPassword();
            this.write(pass);
        } else {
            final String nopass = "JOIN " + channel.getChannel();
            this.write(nopass);
        }
        final IRCJoinEvent jevent = new IRCJoinEvent(channel, this.getServer()
                .getNick());
        plugin.getServer().getPluginManager().callEvent(jevent);
        final PluginJoinEvent pje = new PluginJoinEvent(channel, this
                .getServer().getNick(), "localhost");
        MonsterIRC.getEventManager().dispatchEvent(pje);
    }

    /**
     * Bans a user from the IRC channel if the bot is OP.
     *
     * @param Nick
     *            The user to kick.
     * @param channel
     *            The channel to ban in.
     */
    public void kick(final String Nick, final String channel,
            final String reason) {
        if (this.isConnected()) {
            try {
                this.write("KICK " + channel + " " + Nick + " " + reason);
            } catch (final IOException e) {
                IRC.debug(e);
            }
        }
    }

    /**
     * Bans a user from the IRC channel if the bot is OP.
     *
     * @param Nick
     *            The user to kick.
     * @param channel
     *            The channel to ban in.
     */
    public void mode(final String nick, final String channel, final String mode) {
        if (this.isConnected()) {
            try {
                this.write("MODE " + channel + " " + mode + " " + nick);
            } catch (final IOException e) {
                IRC.debug(e);
            }
        }
    }

    /**
     * Quits a channel in the IRC
     *
     * @param channel
     *            The channel to leave.
     * @throws IOException
     */
    public void part(final IRCChannel channel) {
        try {
            if (this.isConnected()) {
                this.write("PART " + channel.getChannel());

            }
        } catch (final IOException e) {
        }
        final IRCPartEvent levent = new IRCPartEvent(channel, this.getServer()
                .getNick());
        plugin.getServer().getPluginManager().callEvent(levent);
        final PluginPartEvent ppe = new PluginPartEvent(channel, this
                .getServer().getNick());
        MonsterIRC.getEventManager().dispatchEvent(ppe);
    }

    /**
     * Sends a message to the specified channel.
     *
     * @param Message
     *            The message to send.
     * @param channel
     *            The channel to send the message to.
     */
    public void sendMessage(final String channel, final String message) {
        final String prefix = "PRIVMSG " + channel + " :";
        final int length = 500 - prefix.length();
        final ArrayList<String> parts = StringUtils.split(length, message);
        for (final String part : parts) {
            final String msg = prefix + part;
            outputQueue.add(msg.trim());
        }
    }

    /**
     * Sends a message to the specified channel.
     *
     * @param Message
     *            The message to send.
     * @param channel
     *            The channel to send the message to.
     */
    public void sendNotice(final String to, final String message) {
        final String prefix = "NOTICE " + to + " :";
        final int length = 500 - prefix.length();
        final ArrayList<String> parts = StringUtils.split(length, message);
        for (final String part : parts) {
            final String msg = prefix + part;
            outputQueue.add(msg);
        }
    }

    /**
     * Sends a message to the specified channel.
     *
     * @param Message
     *            The message to send.
     * @param channel
     *            The channel to send the message to.
     */
    public void sendRaw(final String RawMessage) {
        outputQueue.add(RawMessage);
    }

    public void write(final String string) throws IOException {
        output.write(string + "\r\n");
        output.flush();
    }
}