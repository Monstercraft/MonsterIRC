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
import org.monstercraft.irc.plugin.util.ColorUtils;
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
				if (watch != null) {
					watch.interrupt();
					watch = null;
				}
				if (print != null) {
					print.interrupt();
					print = null;
				}
				if (!connection.isClosed()) {
					IRC.log("Closing connection.");
					connection.shutdownInput();
					connection.shutdownOutput();
					if (writer != null) {
						writer.flush();
						writer.close();
						writer = null;
					}
					reader.close();
					connection.close();
					connection = null;
				}
				messageQueue.clear();
				IRC.log("Successfully disconnected from IRC.");
			} catch (Exception e) {
				connection = null;
				messageQueue.clear();
				IRC.log("Successfully disconnected from IRC.");
			} finally {

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
		if (connection != null) {
			return connection.isConnected();
		}
		return false;
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
				if (isConnected(MonsterIRC.getIRCServer()) && reader != null) {
					String line;
					while ((line = reader.readLine()) != null) {
						IRC.debug(line, Variables.debug);
						if (line.toLowerCase().startsWith("ping")) {
							writer.write("PONG " + line.substring(5) + "\r\n");
							writer.flush();
							IRC.debug("PONG " + line.substring(5),
									Variables.debug);
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
							} else if (ctcpMsg.equals("TIME")) {
								final SimpleDateFormat sdf = new SimpleDateFormat(
										"dd MMM yyyy hh:mm:ss zzz");
								writer.write("NOTICE " + _name + " :"
										+ (char) ctcpControl + " TIME "
										+ sdf.format(new Date())
										+ (char) ctcpControl + "\r\n");
								writer.flush();
							} else if (ctcpMsg.equals("PING")) {
								writer.write("NOTICE "
										+ _name
										+ " :"
										+ (char) ctcpControl
										+ " PING "
										+ "MonsterIRC by fletch to 99 is to fast to ping."
										+ (char) ctcpControl + "\r\n");
								writer.flush();
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
							}
							continue;
						}
						String name = null;
						String msg = null;
						String subline = null;
						if (line.indexOf(" :") != -1) {
							subline = line.substring(0, line.indexOf(" :"));
						}
						for (IRCChannel c : Variables.channels) {
							try {
								if (subline != null) {
									if (subline.toLowerCase().contains(
											"PRIVMSG ".toLowerCase()
													+ c.getChannel()
															.toLowerCase())) {
										name = line.substring(1,
												line.indexOf("!"));
										msg = line
												.substring(line.indexOf(" :") + 2);
										if (line.contains("ACTION")) {
											msg = "[Action] * "
													+ line.substring(
															line.indexOf(" :") + 2)
															.replaceFirst(
																	"ACTION",
																	name);
											IRCActionEvent actionEvent = new IRCActionEvent(
													c, name, msg);
											plugin.getServer()
													.getPluginManager()
													.callEvent(actionEvent);
											PluginActionEvent pae = new PluginActionEvent(
													c,
													name,
													line.substring(
															line.indexOf(" :") + 2)
															.replaceFirst(
																	"ACTION",
																	name));
											MonsterIRC.getEventManager()
													.dispatchEvent(pae);
										} else {
											IRCMessageEvent msgEvent = new IRCMessageEvent(
													c, msg, name);
											plugin.getServer()
													.getPluginManager()
													.callEvent(msgEvent);
											PluginMessageEvent pme = new PluginMessageEvent(
													c, name, msg);
											MonsterIRC.getEventManager()
													.dispatchEvent(pme);
										}
									} else if (subline.toLowerCase().contains(
											"QUIT".toLowerCase())) {
										if (c.showJoinLeave()) {
											name = line.substring(1,
													line.indexOf("!"));
											msg = name + " has left "
													+ c.getChannel();
										}
										IRCQuitEvent qevent = new IRCQuitEvent(
												c, name);
										plugin.getServer().getPluginManager()
												.callEvent(qevent);
										PluginQuitEvent pqe = new PluginQuitEvent(
												c, name);
										MonsterIRC.getEventManager()
												.dispatchEvent(pqe);
									}
								} else {
									if (line.toLowerCase().contains(
											"MODE ".toLowerCase()
													+ c.getChannel()
															.toLowerCase())) {
										if (line.indexOf("!") != -1) {
											if (line.substring(1,
													line.indexOf("!")) != null) {
												name = line.substring(1,
														line.indexOf("!"));
											}
										}
										String mode = line.substring(
												line.toLowerCase().indexOf(
														c.getChannel()
																.toLowerCase())
														+ 1
														+ c.getChannel()
																.length(),
												line.toLowerCase().indexOf(
														c.getChannel()
																.toLowerCase())
														+ 3
														+ c.getChannel()
																.length());
										String _name = line.substring(line
												.toLowerCase().indexOf(
														c.getChannel()
																.toLowerCase())
												+ c.getChannel().length() + 4);
										if (!Variables.hideMode) {
											msg = "[Mode] " + name
													+ " gave mode " + mode
													+ " to " + _name + ".";
										}
										if (mode.contains("+v")) {
											c.getVoiceList().add(_name);
										} else if (mode.contains("-v")) {
											c.getVoiceList().remove(_name);
										} else if (mode.contains("+o")) {
											c.getOpList().add(_name);
										} else if (mode.contains("-o")) {
											c.getOpList().remove(_name);
										} else if (mode.contains("+h")) {
											c.getHOpList().add(_name);
										} else if (mode.contains("-h")) {
											c.getHOpList().remove(_name);
										} else if (mode.contains("+a")) {
											c.getAdminList().add(_name);
										} else if (mode.contains("-a")) {
											c.getAdminList().remove(_name);
										} else if (mode.contains("+q")) {
											c.getOpList().add(_name);
										} else if (mode.contains("-q")) {
											c.getOpList().remove(_name);
										}
										IRCModeEvent mevent = new IRCModeEvent(
												c, name, mode, msg);
										plugin.getServer().getPluginManager()
												.callEvent(mevent);
										PluginModeEvent pme = new PluginModeEvent(
												c, _name, _name, mode);
										MonsterIRC.getEventManager()
												.dispatchEvent(pme);
									} else if (line.toLowerCase().contains(
											"PART ".toLowerCase()
													+ c.getChannel()
															.toLowerCase())) {
										if (c.showJoinLeave()) {
											name = line.substring(1,
													line.indexOf("!"));
											msg = " has left " + c.getChannel();
										}
										IRCPartEvent pevent = new IRCPartEvent(
												c, name);
										plugin.getServer().getPluginManager()
												.callEvent(pevent);
										PluginPartEvent ppe = new PluginPartEvent(
												c, name);
										MonsterIRC.getEventManager()
												.dispatchEvent(ppe);
									} else if (line.toLowerCase().contains(
											"KICK ".toLowerCase()
													+ c.getChannel()
															.toLowerCase())) {
										name = line.substring(1,
												line.indexOf("!"));
										String _name = line.substring(
												line.toLowerCase().indexOf(
														c.getChannel()
																.toLowerCase())
														+ c.getChannel()
																.length() + 1,
												line.indexOf(" :") - 1);
										msg = _name
												+ " has been kicked from "
												+ c.getChannel()
												+ ". ("
												+ line.substring(line
														.indexOf(" :") + 1)
												+ ")";
										IRCKickEvent kevent = new IRCKickEvent(
												c, name);
										plugin.getServer().getPluginManager()
												.callEvent(kevent);
										PluginKickEvent pke = new PluginKickEvent(
												c, _name, line.substring(line
														.indexOf(" :") + 1));
										MonsterIRC.getEventManager()
												.dispatchEvent(pke);
									} else if (line.toLowerCase().contains(
											"JOIN ".toLowerCase()
													+ c.getChannel()
															.toLowerCase()
															.toLowerCase())) {
										if (c.showJoinLeave()) {
											name = line.substring(1,
													line.indexOf("!"));
											msg = " has joined "
													+ c.getChannel();
										}
										IRCJoinEvent jevent = new IRCJoinEvent(
												c, name);
										plugin.getServer().getPluginManager()
												.callEvent(jevent);
										PluginJoinEvent pje = new PluginJoinEvent(
												c, name);
										MonsterIRC.getEventManager()
												.dispatchEvent(pje);
									}
								}

								if (msg != null && name != null
										&& c.getChannel() != null) {
									if (msg.startsWith(Variables.commandPrefix)) {
										MonsterIRC.getCommandManager()
												.onIRCCommand(name, msg, c);
										break;
									} else if (!Variables.passOnName
											&& !Variables.muted.contains(name
													.toLowerCase())) {
										IRC.sendMessageToGame(c, name, msg);
										break;
									} else if (Variables.passOnName
											&& msg.startsWith(Variables.name)
											&& !Variables.muted.contains(name
													.toLowerCase())) {
										IRC.sendMessageToGame(c, name, msg
												.substring(Variables.name
														.length()));
										break;
									}
								}
							} catch (final Exception e) {
								IRC.debug(e);
							}
						}

						if (line.toLowerCase().contains(
								"PRIVMSG ".toLowerCase()
										+ MonsterIRC.getIRCServer().getNick()
												.toLowerCase())) {
							for (Player p : Bukkit.getServer()
									.getOnlinePlayers()) {
								name = line.substring(1, line.indexOf("!"));
								msg = line.substring(line.indexOf(" :") + 2);
								if (msg.contains(":") && msg.indexOf(":") > 2) {
									String to = msg.substring(0,
											msg.indexOf(":"));
									String _msg = msg.substring(msg
											.indexOf(":") + 1);
									if (to == null || _msg == null
											|| msg == null || name == null) {
										break;
									}
									if (p.getName().equalsIgnoreCase(to)) {
										IRCPrivateMessageEvent pmevent = new IRCPrivateMessageEvent(
												to, name, _msg);
										plugin.getServer().getPluginManager()
												.callEvent(pmevent);
										PluginPrivateMessageEvent ppme = new PluginPrivateMessageEvent(
												to, name, _msg);
										MonsterIRC.getEventManager()
												.dispatchEvent(ppme);
										p.sendMessage(ColorUtils.LIGHT_GRAY
												.getMinecraftColor()
												+ "([IRC] from "
												+ name
												+ "):"
												+ _msg);
										Variables.reply.put(p, name);
									}
								}
							}
						} else if (line.toLowerCase().contains("353")) {
							if (msg == null) {
								IRCChannel chan = null;
								String split = line.substring(line
										.indexOf(" :") + 2);
								String channel = line.substring(
										line.indexOf("#"), line.indexOf(" :"));
								StringTokenizer st = new StringTokenizer(split);
								ArrayList<String> list = new ArrayList<String>();
								while (st.hasMoreTokens()) {
									list.add(st.nextToken());
								}
								for (IRCChannel c : Variables.channels) {
									if (c.getChannel().toLowerCase()
											.contains(channel.toLowerCase())) {
										chan = c;
										break;
									}
								}
								if (chan != null) {
									for (String s : list) {
										if (s.contains("@")) {
											chan.getOpList()
													.add(s.substring(s
															.indexOf("@") + 1));
											IRC.log(s.substring(s.indexOf("@") + 1)
													+ " is an OP in "
													+ chan.getChannel());
										} else if (s.contains("+")) {
											chan.getVoiceList()
													.add(s.substring(s
															.indexOf("+") + 1));
											IRC.log(s.substring(s.indexOf("+") + 1)
													+ " is voice in "
													+ chan.getChannel());
										} else if (s.contains("~")) {
											chan.getOpList()
													.add(s.substring(s
															.indexOf("~") + 1));
											IRC.log(s.substring(s.indexOf("~") + 1)
													+ " is an OP in "
													+ chan.getChannel());
										} else if (s.contains("%")) {
											chan.getHOpList()
													.add(s.substring(s
															.indexOf("%") + 1));
											IRC.log(s.substring(s.indexOf("%") + 1)
													+ " is half op in "
													+ chan.getChannel());
										}
									}
								}
							}
						}
					}
				}
			} catch (final Exception e) {
				Thread.currentThread().interrupt();
				disconnect(MonsterIRC.getIRCServer());
			}
		}

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
				return message.substring(
						message.indexOf((char) ctcpControl) + 1,
						message.indexOf((char) ctcpControl, 1));
			}
			return null;
		}
	};

	private final Runnable DISPATCH = new Runnable() {

		public void run() {
			while (true) {
				try {
					int i = 0;
					if (isConnected(MonsterIRC.getIRCServer())) {
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
					}
				} catch (Exception ex) {
					Thread.currentThread().interrupt();
					disconnect(MonsterIRC.getIRCServer());
					break;
				}
			}
		}
	};

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