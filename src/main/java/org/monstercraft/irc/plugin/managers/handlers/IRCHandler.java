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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
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
import org.monstercraft.irc.ircplugin.util.Methods;
import org.monstercraft.irc.plugin.Configuration;
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
import org.monstercraft.irc.plugin.util.ChatType;
import org.monstercraft.irc.plugin.util.IRCColor;
import org.monstercraft.irc.plugin.util.StringUtils;
import org.monstercraft.irc.plugin.util.Variables;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

import com.gmail.nossr50.mcPermissions;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.TownyUniverse;

/**
 * This handles all of the IRC related stuff.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRCHandler extends IRC {

	private BufferedWriter writer = null;
	private Socket connection = null;
	private BufferedReader reader = null;
	private Thread watch = null;
	private Thread print = null;
	private IRC plugin;
	private Hashtable<Integer, String> MessageQueue = new Hashtable<Integer, String>();
	int counter = -1;

	/**
	 * Creates an instance of the IRCHandler class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public IRCHandler(final IRC plugin) {
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
				disconnect(server);
			}
		}
		String line = null;
		int ping = -1;
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
			Methods.log("The IRC server took " + ping + " MS to respond with "
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
				Methods.log("Attempting to connect to chat.");
				if (server.isIdentifing()) {
					writer.write("PASS " + server.getPassword() + "\r\n");
					writer.flush();
				}
				writer.write("NICK " + server.getNick() + "\r\n");
				writer.flush();
				writer.write("USER " + server.getNick() + " 8 * :"
						+ plugin.getDescription().getVersion() + "\r\n");
				writer.flush();
				Methods.log("Processing connection....");
				while ((line = reader.readLine()) != null) {
					Methods.debug(line, Variables.debug);
					if (line.contains("004") || line.contains("376")) {
						for (String s : server.getConnectCommands()) {
							writer.write(s + "\r\n");
							writer.flush();
						}
						break;
					} else if (line.contains("433")) {
						if (!server.isIdentifing()) {
							Methods.log("Your nickname is already in use, please switch it");
							Methods.log("using \"nick [NAME]\" and try to connect again.");
							disconnect(server);
							return false;
						} else {
							Methods.log("Sending ghost command....");
							writer.write("NICKSERV GHOST " + server.getNick()
									+ " " + server.getPassword() + "\r\n");
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
					Methods.log("Identifying with Nickserv....");
					writer.write("NICKSERV IDENTIFY " + server.getPassword()
							+ "\r\n");
					writer.flush();
				}
				IRCConnectEvent cevent = new IRCConnectEvent(server);
				plugin.getServer().getPluginManager().callEvent(cevent);
				PluginConnectEvent pce = new PluginConnectEvent(server);
				IRC.getEventManager().dispatchEvent(pce);
				for (IRCChannel c : Variables.channels) {
					if (c.isAutoJoin()) {
						IRC.getHandleManager().getIRCHandler().join(c);
					}
				}
				watch = new Thread(KEEP_ALIVE);
				watch.setDaemon(true);
				watch.setPriority(Thread.MAX_PRIORITY);
				watch.start();
				print = new Thread(DISPATCH);
				print.setDaemon(true);
				print.setPriority(Thread.NORM_PRIORITY);
				print.start();
			} catch (Exception e) {
				Methods.log("Failed to connect to IRC! Try again in about 1 minute!");
				disconnect(server);
			}
		} else {
			Methods.log("The IRC server seems to be down or running slowly!");
			Methods.log("To try conencting again run the command /irc connect");
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
			try {
				if (watch != null) {
					watch.interrupt();
					watch = null;
				}
				if (print != null) {
					print.interrupt();
					print = null;
				}
				if (!connection.isClosed()) {
					Methods.log("Closing connection.");
					connection.shutdownInput();
					connection.shutdownOutput();
					if (writer != null) {
						writer.flush();
						writer.close();
						writer = null;
					}
					connection.close();
					connection = null;
				}
				Methods.log("Successfully disconnected from IRC.");
			} catch (Exception e) {
				Methods.debug(e);
			}
		}
		IRCDisconnectEvent devent = new IRCDisconnectEvent(server);
		plugin.getServer().getPluginManager().callEvent(devent);
		PluginDisconnectEvent pde = new PluginDisconnectEvent(server);
		IRC.getEventManager().dispatchEvent(pde);
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
			MessageQueue.put(counter, pass);
			counter--;
		} else {
			String nopass = "JOIN " + channel.getChannel();
			MessageQueue.put(counter, nopass);
			counter--;
		}
		IRCJoinEvent jevent = new IRCJoinEvent(channel, IRC.getIRCServer()
				.getNick());
		plugin.getServer().getPluginManager().callEvent(jevent);
		PluginJoinEvent pje = new PluginJoinEvent(channel, IRC.getIRCServer()
				.getNick());
		IRC.getEventManager().dispatchEvent(pje);
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
			if (isConnected(IRC.getIRCServer())) {
				writer.write("PART " + channel.getChannel() + "\r\n");
				writer.flush();
			}
		} catch (IOException e) {
			Methods.debug(e);
		}
		IRCPartEvent levent = new IRCPartEvent(channel, IRC.getIRCServer()
				.getNick());
		plugin.getServer().getPluginManager().callEvent(levent);
		PluginPartEvent ppe = new PluginPartEvent(channel, IRC.getIRCServer()
				.getNick());
		IRC.getEventManager().dispatchEvent(ppe);
	}

	private final Runnable KEEP_ALIVE = new Runnable() {
		@Override
		public void run() {
			try {
				if (isConnected(IRC.getIRCServer()) && reader != null) {
					String line;
					while ((line = reader.readLine()) != null) {
						Methods.debug(line, Variables.debug);
						if (line.toLowerCase().startsWith("ping")) {
							writer.write("PONG " + line.substring(5) + "\r\n");
							writer.flush();
							Methods.debug("PONG " + line.substring(5),
									Variables.debug);
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
											IRC.getEventManager()
													.dispatchEvent(pae);
										} else {
											IRCMessageEvent msgEvent = new IRCMessageEvent(
													c, msg, name);
											plugin.getServer()
													.getPluginManager()
													.callEvent(msgEvent);
											PluginMessageEvent pme = new PluginMessageEvent(
													c, name, msg);
											IRC.getEventManager()
													.dispatchEvent(pme);
										}
									} else if (subline.toLowerCase().contains(
											"QUIT".toLowerCase())) {
										if (c.showJoinLeave()) {
											name = line.substring(1,
													line.indexOf("!"));
											msg = name + " has left ("
													+ c.getChannel() + ")";
										}
										IRCQuitEvent qevent = new IRCQuitEvent(
												c, name);
										plugin.getServer().getPluginManager()
												.callEvent(qevent);
										PluginQuitEvent pqe = new PluginQuitEvent(
												c, name);
										IRC.getEventManager()
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
										}
										IRCModeEvent mevent = new IRCModeEvent(
												c, name, mode, msg);
										plugin.getServer().getPluginManager()
												.callEvent(mevent);
										PluginModeEvent pme = new PluginModeEvent(
												c, _name, _name, mode);
										IRC.getEventManager()
												.dispatchEvent(pme);
									} else if (line.toLowerCase().contains(
											"PART ".toLowerCase()
													+ c.getChannel()
															.toLowerCase())) {
										if (c.showJoinLeave()) {
											name = line.substring(1,
													line.indexOf("!"));
											msg = name + " has left ("
													+ c.getChannel() + ")";
										}
										IRCPartEvent pevent = new IRCPartEvent(
												c, name);
										plugin.getServer().getPluginManager()
												.callEvent(pevent);
										PluginPartEvent ppe = new PluginPartEvent(
												c, name);
										IRC.getEventManager()
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
										msg = _name + " has been kicked from "
												+ c.getChannel() + ".";
										IRCKickEvent kevent = new IRCKickEvent(
												c, name);
										plugin.getServer().getPluginManager()
												.callEvent(kevent);
										PluginKickEvent pke = new PluginKickEvent(
												c, _name, line.substring(line
														.indexOf(" :") + 1));
										IRC.getEventManager()
												.dispatchEvent(pke);
									} else if (line.toLowerCase().contains(
											"JOIN ".toLowerCase()
													+ c.getChannel()
															.toLowerCase()
															.toLowerCase())) {
										if (c.showJoinLeave()) {
											name = line.substring(1,
													line.indexOf("!"));
											msg = name + " has joined ("
													+ c.getChannel() + ")";
										}
										IRCJoinEvent jevent = new IRCJoinEvent(
												c, name);
										plugin.getServer().getPluginManager()
												.callEvent(jevent);
										PluginJoinEvent pje = new PluginJoinEvent(
												c, name);
										IRC.getEventManager()
												.dispatchEvent(pje);
									}
								}

								if (msg != null && name != null
										&& c.getChannel() != null) {
									if (msg.startsWith(Variables.commandPrefix)
											&& !Variables.muted.contains(name
													.toLowerCase())) {
										IRC.getCommandManager().onIRCCommand(
												name, msg, c);
										break;
									} else if (!Variables.passOnName
											&& !Variables.muted.contains(name
													.toLowerCase())) {
										handleMessage(c, name, msg);
										break;
									} else if (Variables.passOnName
											&& msg.startsWith(Variables.name)
											&& !Variables.muted.contains(name
													.toLowerCase())) {
										handleMessage(c, name,
												msg.substring(Variables.name
														.length()));
										break;
									}
								}
							} catch (final Exception e) {
								Methods.debug(e);
							}
						}

						if (line.toLowerCase().contains(
								"PRIVMSG ".toLowerCase()
										+ IRC.getIRCServer().getNick()
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
										IRC.getEventManager().dispatchEvent(
												ppme);
										p.sendMessage(IRCColor.LIGHT_GRAY
												.getMinecraftColor()
												+ "([IRC] from "
												+ name
												+ "):"
												+ _msg);
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
											Methods.log(s.substring(s
													.indexOf("@") + 1)
													+ " is an OP in "
													+ chan.getChannel());
										} else if (s.contains("+")) {
											chan.getVoiceList()
													.add(s.substring(s
															.indexOf("+") + 1));
											Methods.log(s.substring(s
													.indexOf("+") + 1)
													+ " is voice in "
													+ chan.getChannel());
										} else if (s.contains("~")) {
											chan.getOpList()
													.add(s.substring(s
															.indexOf("~") + 1));
											Methods.log(s.substring(s
													.indexOf("~") + 1)
													+ " is an OP in "
													+ chan.getChannel());
										} else if (s.contains("%")) {
											chan.getVoiceList()
													.add(s.substring(s
															.indexOf("%") + 1));
											Methods.log(s.substring(s
													.indexOf("%") + 1)
													+ " is voice in "
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
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	};

	private final Runnable DISPATCH = new Runnable() {
		@Override
		public void run() {
			while (true) {
				try {
					if (isConnected(IRC.getIRCServer())) {
						int i = 0;
						for (Enumeration<Integer> e = MessageQueue.keys(); e
								.hasMoreElements();) {
							int key = e.nextElement();
							String Message = MessageQueue.get(key);
							writer.write(Message + "\r\n");
							writer.flush();
							MessageQueue.remove(key);
							i++;
							if (i >= Variables.limit) {
								break;
							}
							if (MessageQueue.isEmpty()) {
								counter = -1;
								break;
							}
						}
						if (Variables.limit != 0) {
							Thread.sleep(1000 / Variables.limit);
						}
					}
				} catch (Exception ex) {
					Thread.currentThread().interrupt();
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
			MessageQueue.put(counter, msg);
			counter--;
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
			MessageQueue.put(counter, msg);
			counter--;
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
				Methods.debug(e);
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
	public void ban(final IRCServer server, final String Nick,
			final String channel) {
		if (isConnected(server)) {
			try {
				writer.write("KICK " + channel + " " + Nick + "\r\n");
				writer.flush();
				writer.write("MODE " + channel + " +b" + Nick + "\r\n");
				writer.flush();
			} catch (IOException e) {
				Methods.debug(e);
			}
		}
	}

	/**
	 * Fetches the list of Operaters in the current IRC channel.
	 * 
	 * @return The list of Operators.
	 */
	public boolean isOp(final IRCChannel channel, final String sender) {
		return channel.getOpList().contains(sender);
	}

	/**
	 * Fetches the list of Voices in the current IRC channel.
	 * 
	 * @return The list of Voices.
	 */
	public boolean isVoice(final IRCChannel channel, final String sender) {
		return channel.getVoiceList().contains(sender);
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
	private void handleMessage(final IRCChannel c, final String name,
			final String message) {
		try {
			if (c.getChatType() == ChatType.ADMINCHAT) {
				if (IRC.getHookManager().getmcMMOHook() != null) {
					String format = "§b" + "{" + "§f" + "[IRC] "
							+ StringUtils.getPrefix(name)
							+ StringUtils.getName(name)
							+ StringUtils.getSuffix(name) + "§b" + "} "
							+ message;
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						if (p.isOp()
								|| mcPermissions.getInstance().adminChat(p))
							p.sendMessage(IRCColor.formatIRCMessage(format));
					}
				}
			} else if (c.getChatType() == ChatType.HEROCHAT && !Variables.hc4) {
				c.getHeroChatChannel().announce(
						IRCColor.formatIRCMessage(Variables.mcformat
								.replace("{name}", StringUtils.getName(name))
								.replace("{message}", message)
								.replace("{prefix}",
										StringUtils.getPrefix(name))
								.replace("{suffix}",
										StringUtils.getSuffix(name))
								.replace("{groupPrefix}",
										StringUtils.getGroupPrefix(name))
								.replace("{groupSuffix}",
										StringUtils.getGroupSuffix(name))
								.replace("{world}", StringUtils.getWorld(name))
								.replace("&", "§")));
			} else if (c.getChatType() == ChatType.HEROCHAT
					&& IRC.getHookManager().getHeroChatHook() != null
					&& Variables.hc4) {
				c.getHeroChatFourChannel().sendMessage(
						Variables.mcformat
								.replace("{name}", StringUtils.getName(name))
								.replace("{message}", "")
								.replace(":", "")
								.replace("{prefix}",
										StringUtils.getPrefix(name))
								.replace("{suffix}",
										StringUtils.getSuffix(name))
								.replace("{groupPrefix}",
										StringUtils.getGroupPrefix(name))
								.replace("{groupSuffix}",
										StringUtils.getGroupSuffix(name))
								.replace("{world}", StringUtils.getWorld(name))
								.replace("&", "§"),
						IRCColor.formatIRCMessage(IRCColor
								.formatIRCMessage(message)),
						c.getHeroChatFourChannel().getMsgFormat(), false);
			} else if (c.getChatType() == ChatType.GLOBAL) {
				plugin.getServer().broadcastMessage(
						IRCColor.formatIRCMessage(Variables.mcformat
								.replace("{name}", StringUtils.getName(name))
								.replace(
										"{message}",
										IRCColor.WHITE.getMinecraftColor()
												+ message)
								.replace("{prefix}",
										StringUtils.getPrefix(name))
								.replace("{suffix}",
										StringUtils.getSuffix(name))
								.replace("{groupPrefix}",
										StringUtils.getGroupPrefix(name))
								.replace("{groupSuffix}",
										StringUtils.getGroupSuffix(name))
								.replace("{world}", StringUtils.getWorld(name))
								.replace("&", "§")));
			} else if (c.getChatType() == ChatType.TOWNYCHAT) {
				for (Player p : TownyUniverse.getOnlinePlayers()) {
					TownyMessaging.sendMessage(p, IRCColor.formatIRCMessage(c
							.getTownyChannel().getChannelTag()
							.replace("&", "§")
							+ Variables.mcformat
									.replace("{name}",
											StringUtils.getName(name))
									.replace(
											"{message}",
											c.getTownyChannel()
													.getMessageColour()
													+ message)
									.replace("{prefix}",
											StringUtils.getPrefix(name))
									.replace("{suffix}",
											StringUtils.getSuffix(name))
									.replace("{groupPrefix}",
											StringUtils.getGroupPrefix(name))
									.replace("{groupSuffix}",
											StringUtils.getGroupSuffix(name))
									.replace("{world}",
											StringUtils.getWorld(name))
									.replace("&", "§")));
				}
			} else if (c.getChatType() == ChatType.NONE) {
				return;
			}
		} catch (Exception e) {
			Methods.debug(e);
		}
	}
}