package org.monstercraft.irc.plugin.managers.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.ircplugin.event.events.PluginActionEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginConnectEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginDisconnectEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginJoinEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginKickEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginMessageEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginModeEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginPartEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginPrivateMessageEvent;
import org.monstercraft.irc.ircplugin.event.events.PluginQuitEvent;
import org.monstercraft.irc.plugin.Configuration;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.event.events.IRCActionEvent;
import org.monstercraft.irc.plugin.event.events.IRCConnectEvent;
import org.monstercraft.irc.plugin.event.events.IRCDisconnectEvent;
import org.monstercraft.irc.plugin.event.events.IRCJoinEvent;
import org.monstercraft.irc.plugin.event.events.IRCKickEvent;
import org.monstercraft.irc.plugin.event.events.IRCMessageEvent;
import org.monstercraft.irc.plugin.event.events.IRCModeEvent;
import org.monstercraft.irc.plugin.event.events.IRCPartEvent;
import org.monstercraft.irc.plugin.event.events.IRCPrivateMessageEvent;
import org.monstercraft.irc.plugin.event.events.IRCQuitEvent;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

/**
 * This handles all of the IRC related stuff.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRCHandler extends MonsterIRC {

	private BufferedWriter writer = null;
	private Socket connection = null;
	private BufferedReader reader = null;
	private Thread watch = null;
	private Thread print = null;
	private final MonsterIRC plugin;
	private LinkedList<String> messageQueue = new LinkedList<String>();

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
	 * Connects to an IRC server then a channel.
	 * 
	 * @param server
	 *            The server to connect to.
	 * @return True if connected successfully; otherwise false.
	 */
	public boolean connect(final IRCServer server) {
		if (connection != null) {
			if (connection.isConnected()) {
				IRC.log("Attempting to disconnect before re-connecting!");
				disconnect(server);
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
				connection = new Socket();
				InetAddress addr = InetAddress.getByName(server.getServer());
				SocketAddress sockaddr = new InetSocketAddress(addr,
						server.getPort());
				connection.connect(sockaddr);
				writer = new BufferedWriter(new OutputStreamWriter(
						connection.getOutputStream()));
				reader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				IRC.log("Attempting to connect to chat.");
				if (!server.getPassword().equalsIgnoreCase("")) {
					writer.write("PASS " + server.getPassword() + "\r\n");
					writer.flush();
				}
				writer.write("NICK " + server.getNick() + "\r\n");
				writer.flush();
				writer.write("USER " + server.getNick() + " 8 * :"
						+ plugin.getDescription().getVersion() + "\r\n");
				writer.flush();
				IRC.log("Processing connection....");
				while ((line = reader.readLine()) != null) {
					IRC.debug(line, Variables.debug);
					if (line.contains("004") || line.contains("376")) {
						break;
					} else if (line.contains("433")) {
						if (!server.isIdentifing()) {
							IRC.log("Your nickname is already in use, please switch it");
							IRC.log("using \"nick [NAME]\" and try to connect again.");
							disconnect(server);
							return false;
						} else {
							IRC.log("Sending ghost command....");
							writer.write("NICKSERV GHOST " + server.getNick()
									+ " " + server.getNickservPassword()
									+ "\r\n");
							writer.flush();
							continue;
						}
					} else if (line.toLowerCase().startsWith("ping ")) {
						writer.write("PONG " + line.substring(5) + "\r\n");
						writer.flush();
						continue;
					}
				}
				if (server.isIdentifing()) {
					IRC.log("Identifying with Nickserv....");
					writer.write("NICKSERV IDENTIFY "
							+ server.getNickservPassword() + "\r\n");
					writer.flush();
				}
				for (String s : server.getConnectCommands()) {
					writer.write(s + "\r\n");
					writer.flush();
				}
				IRCConnectEvent cevent = new IRCConnectEvent(server);
				plugin.getServer().getPluginManager().callEvent(cevent);
				PluginConnectEvent pce = new PluginConnectEvent(server);
				MonsterIRC.getEventManager().dispatchEvent(pce);
				for (IRCChannel c : Variables.channels) {
					if (c.isAutoJoin()) {
						MonsterIRC.getHandleManager().getIRCHandler().join(c);
					}
				}
				watch = new Thread(KEEP_ALIVE);
				watch.setDaemon(true);
				watch.setPriority(Thread.MAX_PRIORITY);
				watch.start();
				print = new Thread(DISPATCH);
				print.setDaemon(true);
				print.setPriority(Thread.MAX_PRIORITY);
				print.start();
			} catch (Exception e) {
				IRC.log("Failed to connect to IRC! Try again in about 1 minute!");
				disconnect(server);
			}
		} else {
			IRC.log("The IRC server seems to be down or running slowly!");
			IRC.log("To try conencting again run the command /irc connect");
			IRC.log("Your ping is:" + ping);
			return false;
		}
		return isConnected(server);
	}

	/**
	 * Disconnects a user from the IRC server.
	 * 
	 * @return True if we disconnect successfully; otherwise false.
	 */
	public boolean disconnect(final IRCServer server) {
		if (isConnected(server)) {
			if (Variables.partOnDC) {
				for (IRCChannel c : Variables.channels) {
					getHandleManager().getIRCHandler().part(c);
				}
			}
			try {
				writer.write("QUIT Leaving." + "\r\n");
				writer.flush();
				messageQueue.clear();
				writer.close();
				reader.close();
				connection.close();
				IRC.log("Successfully disconnected from IRC.");
			} catch (Exception e) {
				messageQueue.clear();
				IRC.log("Successfully disconnected from IRC.");
			}
		}
		IRCDisconnectEvent devent = new IRCDisconnectEvent(server);
		plugin.getServer().getPluginManager().callEvent(devent);
		PluginDisconnectEvent pde = new PluginDisconnectEvent(server);
		MonsterIRC.getEventManager().dispatchEvent(pde);
		return !isConnected(server);
	}

	/**
	 * Checks if the user is connected to an IRC server.
	 * 
	 * @return True if conencted to an IRC server; othewise false.
	 */
	public boolean isConnected(final IRCServer server) {
		return connection != null ? connection.isConnected()
				&& connection.isBound() : false;
	}

	/**
	 * Joins an IRC channel on that server.
	 * 
	 * @param channel
	 *            The channel to join.
	 */
	public void join(final IRCChannel channel) {
		if (channel.getPassword() != null && channel.getPassword() != "") {
			String pass = "JOIN " + channel.getChannel() + " "
					+ channel.getPassword();
			messageQueue.add(pass);
		} else {
			String nopass = "JOIN " + channel.getChannel();
			messageQueue.add(nopass);
		}
		IRCJoinEvent jevent = new IRCJoinEvent(channel, MonsterIRC
				.getIRCServer().getNick());
		plugin.getServer().getPluginManager().callEvent(jevent);
		PluginJoinEvent pje = new PluginJoinEvent(channel, MonsterIRC
				.getIRCServer().getNick());
		MonsterIRC.getEventManager().dispatchEvent(pje);
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
			if (isConnected(MonsterIRC.getIRCServer())) {
				writer.write("PART " + channel.getChannel() + "\r\n");
				writer.flush();
			}
		} catch (IOException e) {
			IRC.debug(e);
		}
		IRCPartEvent levent = new IRCPartEvent(channel, MonsterIRC
				.getIRCServer().getNick());
		plugin.getServer().getPluginManager().callEvent(levent);
		PluginPartEvent ppe = new PluginPartEvent(channel, MonsterIRC
				.getIRCServer().getNick());
		MonsterIRC.getEventManager().dispatchEvent(ppe);
	}

	private final Runnable KEEP_ALIVE = new Runnable() {
		public void run() {
			try {
				String line = null;
				while (isConnected(MonsterIRC.getIRCServer())
						&& (line = reader.readLine()) != null) {
					String subline = null;

					if (line.indexOf(" :") != -1) {
						subline = line.substring(0, line.indexOf(" :"));
					}

					IRC.debug(line, Variables.debug);

					if (line.toLowerCase().startsWith("ping")) {
						writer.write("PONG " + line.substring(5) + "\r\n");
						writer.flush();
						IRC.debug("PONG " + line.substring(5), Variables.debug);
						continue;
					} else if (isCTCP(line)) {
						final String _name = line.substring(1,
								line.indexOf("!"));
						final String ctcpMsg = getCTCPMessage(line)
								.toUpperCase();
						if (ctcpMsg.equals("VERSION")) {
							writer.write("NOTICE "
									+ _name
									+ " :"
									+ (char) ctcpControl
									+ "VERSION "
									+ "MonsterIRC for Bukkit written by Fletch_to_99"
									+ (char) ctcpControl + "\r\n");
							writer.flush();
							continue;
						} else if (ctcpMsg.equals("TIME")) {
							final SimpleDateFormat sdf = new SimpleDateFormat(
									"dd MMM yyyy hh:mm:ss zzz");
							writer.write("NOTICE " + _name + " :"
									+ (char) ctcpControl + " TIME "
									+ sdf.format(new Date())
									+ (char) ctcpControl + "\r\n");
							writer.flush();
							continue;
						} else if (ctcpMsg.equals("PING")) {
							writer.write("NOTICE "
									+ _name
									+ " :"
									+ (char) ctcpControl
									+ " PING "
									+ "MonsterIRC by fletch to 99 is to fast to ping."
									+ (char) ctcpControl + "\r\n");
							writer.flush();
							continue;
						} else if (ctcpMsg.equals("FINGER")) {
							writer.write("NOTICE "
									+ _name
									+ " :"
									+ (char) ctcpControl
									+ " FINGER "
									+ "MonsterIRC written by fletch to 99 slaps "
									+ _name + " across the face."
									+ (char) ctcpControl + "\r\n");
							writer.flush();
							continue;
						}
					}
					for (IRCChannel c : Variables.channels) {
						try {
							if (isCTCP(line)) {
								final String ctcpMsg = getCTCPMessage(line);
								if (ctcpMsg.contains("ACTION")) {
									String sender = line.substring(1,
											line.indexOf("!"));
									String action = ctcpMsg.substring(7);
									plugin.getServer()
											.getPluginManager()
											.callEvent(
													new IRCActionEvent(c,
															sender, action));
									MonsterIRC.getEventManager().dispatchEvent(
											new PluginActionEvent(c, sender,
													action));
									break;
								}
							} else if (subline != null) {
								if (subline.toLowerCase().contains(
										("PRIVMSG " + c.getChannel())
												.toLowerCase())) {
									String sender = line.substring(1,
											line.indexOf("!"));
									String message = line.substring(line
											.indexOf(" :") + 2);
									plugin.getServer()
											.getPluginManager()
											.callEvent(
													new IRCMessageEvent(c,
															sender, message));
									MonsterIRC.getEventManager().dispatchEvent(
											new PluginMessageEvent(c, sender,
													message));
									break;
								} else if (subline.toLowerCase().contains(
										"quit")) {
									String sender = line.substring(1,
											line.indexOf("!"));
									plugin.getServer()
											.getPluginManager()
											.callEvent(
													new IRCQuitEvent(c, sender));
									MonsterIRC.getEventManager().dispatchEvent(
											new PluginQuitEvent(c, sender));
									break;

								} else if (subline.toLowerCase().contains(
										("KICK " + c.getChannel())
												.toLowerCase())) {
									String kicker = line.substring(1,
											line.indexOf("!"));
									String user = line
											.substring(
													line.toLowerCase()
															.indexOf(
																	c.getChannel()
																			.toLowerCase())
															+ c.getChannel()
																	.length()
															+ 1, line
															.indexOf(" :") - 1);
									String reason = line.substring(line
											.indexOf(" :") + 2);
									plugin.getServer()
											.getPluginManager()
											.callEvent(
													new IRCKickEvent(c, kicker,
															user, reason));
									MonsterIRC.getEventManager().dispatchEvent(
											new PluginKickEvent(c, kicker,
													user, reason));
									break;
								} else if (subline.toLowerCase()
										.contains("353")
										&& subline.toLowerCase().contains(
												c.getChannel().toLowerCase())) {
									String split = line.substring(line
											.indexOf(" :") + 2);
									StringTokenizer st = new StringTokenizer(
											split);
									ArrayList<String> list = new ArrayList<String>();
									while (st.hasMoreTokens()) {
										list.add(st.nextToken());
									}
									for (String s : list) {
										if (s.contains("@")) {
											c.getOpList()
													.add(s.substring(s
															.indexOf("@") + 1));
											IRC.debug(s.substring(s
													.indexOf("@") + 1)
													+ " is an OP in "
													+ c.getChannel());
										} else if (s.contains("+")) {
											c.getVoiceList()
													.add(s.substring(s
															.indexOf("+") + 1));
											IRC.debug(s.substring(s
													.indexOf("+") + 1)
													+ " is voice in "
													+ c.getChannel());
										} else if (s.contains("~")) {
											c.getOpList()
													.add(s.substring(s
															.indexOf("~") + 1));
											IRC.debug(s.substring(s
													.indexOf("~") + 1)
													+ " is an OP in "
													+ c.getChannel());
										} else if (s.contains("%")) {
											c.getHOpList()
													.add(s.substring(s
															.indexOf("%") + 1));
											IRC.debug(s.substring(s
													.indexOf("%") + 1)
													+ " is half op in "
													+ c.getChannel());
										}
									}
									break;
								}
							} else if (line.toLowerCase().contains(
									("MODE " + c.getChannel()).toLowerCase())) {
								String sender = line.substring(1,
										line.indexOf("!"));
								String mode = line.substring(
										line.toLowerCase().indexOf(
												c.getChannel().toLowerCase())
												+ 1 + c.getChannel().length(),
										line.toLowerCase().indexOf(
												c.getChannel().toLowerCase())
												+ 3 + c.getChannel().length());
								String user = line.substring(line.toLowerCase()
										.indexOf(c.getChannel().toLowerCase())
										+ c.getChannel().length() + 4);
								plugin.getServer()
										.getPluginManager()
										.callEvent(
												new IRCModeEvent(c, sender,
														user, mode));
								MonsterIRC.getEventManager().dispatchEvent(
										new PluginModeEvent(c, sender, user,
												mode));
								break;
							} else if (line.toLowerCase().contains(
									("PART " + c.getChannel()).toLowerCase())) {
								String name = line.substring(1,
										line.indexOf("!"));
								if (name.equalsIgnoreCase(MonsterIRC
										.getIRCServer().getNick())) {
									break;
								}
								IRCPartEvent pevent = new IRCPartEvent(c, name);
								plugin.getServer().getPluginManager()
										.callEvent(pevent);
								PluginPartEvent ppe = new PluginPartEvent(c,
										name);
								MonsterIRC.getEventManager().dispatchEvent(ppe);
								break;
							} else if (line.toLowerCase().contains(
									("JOIN " + c.getChannel()).toLowerCase())) {
								String name = line.substring(1,
										line.indexOf("!"));
								if (name.equalsIgnoreCase(MonsterIRC
										.getIRCServer().getNick())) {
									break;
								}
								plugin.getServer().getPluginManager()
										.callEvent(new IRCJoinEvent(c, name));
								MonsterIRC.getEventManager().dispatchEvent(
										new PluginJoinEvent(c, name));
								break;
							}
						} catch (final Exception e) {
							IRC.debug(e);
						}
					}

					if (line.toLowerCase().contains(
							("PRIVMSG " + MonsterIRC.getIRCServer().getNick())
									.toLowerCase())) {
						String sender = line.substring(1, line.indexOf("!"));
						String message = line.substring(line.indexOf(" :") + 2);
						if (message.contains(":") && message.indexOf(":") > 2) {
							String to = message.substring(0,
									message.indexOf(":"));
							String msg = message
									.substring(message.indexOf(":") + 1);
							if (to != null && message != null && msg != null
									&& sender != null) {
								for (Player p : Bukkit.getServer()
										.getOnlinePlayers()) {
									if (p.getName().equalsIgnoreCase(to)) {
										plugin.getServer()
												.getPluginManager()
												.callEvent(
														new IRCPrivateMessageEvent(
																to, sender, msg));
										MonsterIRC
												.getEventManager()
												.dispatchEvent(
														new PluginPrivateMessageEvent(
																to, sender, msg));
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
			}
		}
	};

	private final Runnable DISPATCH = new Runnable() {

		public void run() {
			while (isConnected(MonsterIRC.getIRCServer())) {
				try {
					int i = 0;
					while (!messageQueue.isEmpty()) {
						String message = messageQueue.remove();
						writer.write(message + "\r\n");
						writer.flush();
						i++;
						if (i >= Variables.limit) {
							break;
						}
						if (messageQueue.isEmpty()) {
							break;
						}
					}
					if (Variables.limit != 0) {
						Thread.sleep(1000 / Variables.limit);
					}
				} catch (Exception e) {
					IRC.debug(e);
				}
			}
		}
	};

	private final byte ctcpControl = 1;

	private boolean isCTCP(final String input) {
		if (input.length() != 0) {
			String message = input.substring(input.indexOf(":", 1) + 1);
			if (message.length() != 0) {
				char[] messageArray = message.toCharArray();
				return ((byte) messageArray[0]) == 1
						&& ((byte) messageArray[messageArray.length - 1]) == 1;
			}
		}
		return false;
	}

	private String getCTCPMessage(final String input) {
		if (input.length() != 0) {
			String message = input.substring(input.indexOf(":", 1) + 1);
			return message.substring(message.indexOf((char) ctcpControl) + 1,
					message.indexOf((char) ctcpControl, 1));
		}
		return null;
	}

	/**
	 * Sends a message to the specified channel.
	 * 
	 * @param Message
	 *            The message to send.
	 * @param channel
	 *            The channel to send the message to.
	 */
	public void sendMessage(final String channel, final String Message) {
		final String prefix = "PRIVMSG " + channel + " :";
		final int length = 500 - prefix.length();
		final String parts[] = Message.toString().split(
				"(?<=\\G.{" + length + "})");
		for (int i = 0; i < parts.length; i++) {
			String msg = prefix + parts[i];
			messageQueue.add(msg.trim());
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
		final String parts[] = RawMessage.toString().split(
				"(?<=\\G.{" + 500 + "})");
		for (int i = 0; i < parts.length; i++) {
			String msg = parts[i];
			messageQueue.add(msg);
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
		final String parts[] = message.toString().split(
				"(?<=\\G.{" + length + "})");
		for (int i = 0; i < parts.length; i++) {
			String msg = prefix + parts[i];
			messageQueue.add(msg);
		}
	}

	/**
	 * Changes the nickname of the IRC bot.
	 * 
	 * @param Nick
	 *            The name to change to.
	 */
	public void changeNick(final IRCServer server, final String Nick) {
		if (isConnected(server)) {
			try {
				writer.write("NICK " + Nick + "\r\n");
				writer.flush();
			} catch (IOException e) {
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
	public void kick(final IRCServer server, final String Nick,
			final String channel, final String reason) {
		if (isConnected(server)) {
			try {
				writer.write("KICK " + channel + " " + Nick + " " + reason
						+ "\r\n");
				writer.flush();
			} catch (IOException e) {
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
	public void mode(final IRCServer server, final String nick,
			final String channel, final String mode) {
		if (isConnected(server)) {
			try {
				writer.write("MODE " + channel + " " + mode + " " + nick
						+ "\r\n");
				writer.flush();
			} catch (IOException e) {
				IRC.debug(e);
			}
		}
	}

	/**
	 * Bans a user from the IRC channel if the bot is OP.
	 * 
	 * @param Nick
	 *            The user to ban.
	 * @param channel
	 *            The channel to ban in.
	 */
	public void ban(final IRCServer server, final String nick,
			final String channel) {
		if (isConnected(server)) {
			try {
				kick(server, nick, channel, "Derp.");
				writer.write("MODE " + channel + " +b " + nick + "\r\n");
				writer.flush();
			} catch (IOException e) {
				IRC.debug(e);
			}
		}
	}
}