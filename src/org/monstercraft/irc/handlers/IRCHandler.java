package org.monstercraft.irc.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.util.ChatType;
import org.monstercraft.irc.util.IRCColor;
import org.monstercraft.irc.util.Pinger;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;
import org.monstercraft.irc.wrappers.IRCServer;

import com.gmail.nossr50.mcPermissions;

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
	private ArrayList<String> messageQue = new ArrayList<String>();
	private OutputStreamWriter osw = null;
	private InputStreamReader isr = null;

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
		if (!isConnected(server)) {
			String line = null;
			long ping = -1;
			int tries = 0;
			for (int i = 0; i < server.getRetrys(); i++) {
				ping = Pinger.ping(server.getServer(), server.getPort(),
						server.getTimeout());
				if (ping > 0) {
					tries = i;
					break;
				}
			}
			if (ping < server.getTimeout() + 1 && ping != -1) {
				log("The IRC server took " + ping + " MS to respond with "
						+ tries + " retrys.");
				try {
					connection = new Socket(server.getServer(),
							server.getPort());
					osw = new OutputStreamWriter(connection.getOutputStream());
					isr = new InputStreamReader(connection.getInputStream());
					writer = new BufferedWriter(osw);
					reader = new BufferedReader(isr);
					log("Attempting to connect to chat.");
					if (server.isIdentifing()) {
						writer.write("PASS " + server.getPassword() + "\r\n");
						writer.flush();
					}
					writer.write("NICK " + server.getNick() + "\r\n");
					writer.flush();
					writer.write("USER " + server.getNick() + " 8 * :"
							+ plugin.getDescription().getVersion() + "\r\n");
					writer.flush();
					log("Processing connection....");
					while ((line = reader.readLine()) != null) {
						debug(line);
						if (line.contains("004")) {
							break;
						} else if (line.contains("433")) {
							if (!server.isIdentifing()) {
								log("Your nickname is already in use, please switch it");
								log("using \"nick [NAME]\" and try to connect again.");
								disconnect(server);
								return false;
							} else {
								log("Sending ghost command....");
								writer.write("NICKSERV GHOST "
										+ server.getNick() + " "
										+ server.getPassword() + "\r\n");
								writer.flush();
							}
						} else if (line.toLowerCase().startsWith("ping ")) {
							writer.write("PONG " + line.substring(5) + "\r\n");
							writer.flush();
							continue;
						}
					}
					if (server.isIdentifing()) {
						log("Identifying with Nickserv....");
						writer.write("NICKSERV IDENTIFY "
								+ server.getPassword() + "\r\n");
						writer.flush();
					}
					for (IRCChannel c : Variables.channels) {
						if (c.isAutoJoin()) {
							c.join();
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
					log("Failed to connect to IRC! Try again in about 1 minute!");
					disconnect(server);
				}
			} else {
				log("The IRC server seems to be down or running slowly!");
				log("To try conencting again run the command /irc connect");
				return false;
			}
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
					log("Closing connection.");
					connection.shutdownInput();
					connection.shutdownOutput();
					if (reader != null) {
						isr.close();
						reader.close();
						reader = null;
					}
					if (writer != null) {
						osw.flush();
						writer.flush();
						osw.close();
						writer.close();
						writer = null;
					}
					connection.close();
					connection = null;
				}
				log("Successfully disconnected from IRC.");
			} catch (Exception e) {
				debug(e);
			}
		}
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
		messageQue.add("JOIN " + channel.getChannel());
	}

	/**
	 * Quits a channel in the IRC
	 * 
	 * @param channel
	 *            The channel to leave.
	 * @throws IOException
	 */
	public void leave(final IRCChannel channel) {
		try {
			if (isConnected(channel.getServer())) {
				writer.write("PART " + channel.getChannel() + "\r\n");
				writer.flush();
			}
		} catch (IOException e) {
			debug(e);
		}
	}

	private final Runnable KEEP_ALIVE = new Runnable() {
		public void run() {
			try {
				if (isConnected(IRC.getIRCServer()) && reader != null
						&& reader.ready()) {
					String line;
					try {
						while ((line = reader.readLine()) != null) {
							if (!isConnected(IRC.getIRCServer())) {
								break;
							}
							debug(line);
							String name = null;
							String msg = null;
							for (IRCChannel c : Variables.channels) {
								try {
									if (line.toLowerCase().contains(
											"PRIVMSG ".toLowerCase()
													+ c.getChannel()
															.toLowerCase())) {
										name = line.substring(1,
												line.indexOf("!"));
										msg = line
												.substring(line.indexOf(" :") + 2);
										if (line.contains("ACTION")) {
											msg = "* "
													+ line.substring(
															line.indexOf(" :") + 2)
															.replaceFirst(
																	"ACTION",
																	name);
										}
									} else if (line.toLowerCase().contains(
											"JOIN :".toLowerCase()
													+ c.getChannel()
															.toLowerCase()
															.toLowerCase())) {
										if (Variables.joinAndQuit) {
											name = line.substring(1,
													line.indexOf("!"));
											msg = name + " joined "
													+ c.getChannel() + ".";
										}
									} else if (line.toLowerCase().contains(
											"PART ".toLowerCase()
													+ c.getChannel()
															.toLowerCase())) {
										if (Variables.joinAndQuit) {
											name = line.substring(1,
													line.indexOf("!"));
											msg = name + " left "
													+ c.getChannel() + ".";
										}
									} else if (line.toLowerCase().contains(
											"QUIT :".toLowerCase())) {
										if (Variables.joinAndQuit) {
											name = line.substring(1,
													line.indexOf("!"));
											if (name.equalsIgnoreCase(Variables.name)) {
												disconnect(IRC.getIRCServer());
												msg = null;
												break;
											}
											msg = name + " has quit"
													+ c.getChannel() + ".";
										}
									} else if (line.toLowerCase().contains(
											"MODE ".toLowerCase()
													+ c.getChannel()
															.toLowerCase())) {
										name = line.substring(1,
												line.indexOf("!"));
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
										msg = name + " gave mode " + mode
												+ " to " + _name + ".";
										log("MODE LINE :D " + line);
										if (mode.contains("+v")) {
											c.getVoiceList().add(_name);
										} else if (mode.contains("-v")) {
											c.getVoiceList().remove(_name);
										} else if (mode.contains("+o")) {
											c.getOpList().add(_name);
										} else if (mode.contains("-o")) {
											c.getOpList().remove(_name);
										}
									} else if (line.toLowerCase().contains(
											"KICK ".toLowerCase()
													+ c.getChannel()
															.toLowerCase())) {
										name = line.substring(1,
												line.indexOf("!"));
										if (name.equalsIgnoreCase(Variables.name)) {
											join(c);
										}
										msg = name + " has been kicked from"
												+ c.getChannel() + ".";
									}

									if (msg != null && name != null
											&& c.getChannel() != null) {
										if (!line.toLowerCase().contains(
												c.getChannel().toLowerCase())) {
											continue;
										} else if (msg.toLowerCase().contains(
												c.getChannel().toLowerCase())
												&& !line.toLowerCase()
														.contains(
																c.getChannel()
																		.toLowerCase())) {
											continue;
										}

										if (msg.startsWith(Variables.commandPrefix)
												&& !Variables.muted
														.contains(name
																.toLowerCase())) {
											IRC.getCommandManager()
													.onIRCCommand(name, msg, c);
											break;
										} else if (!Variables.passOnName
												&& !Variables.muted
														.contains(name
																.toLowerCase())) {
											handleMessage(c, name, msg);
											break;
										} else if (Variables.passOnName
												&& msg.startsWith(Variables.name)
												&& !Variables.muted
														.contains(name
																.toLowerCase())) {
											handleMessage(
													c,
													name,
													msg.substring(Variables.name
															.length()));
											break;
										}
									}
								} catch (final Exception e) {
									debug(e);
								}
							}

							if (line.toLowerCase().contains(
									"PRIVMSG ".toLowerCase()
											+ IRC.getIRCServer().getNick()
													.toLowerCase())) {
								for (Player p : Bukkit.getServer()
										.getOnlinePlayers()) {
									name = line.substring(1, line.indexOf("!"));
									msg = line
											.substring(line.indexOf(" :") + 2);
									if (msg.contains(":")
											&& msg.indexOf(":") > 2) {
										String to = msg.substring(0,
												msg.indexOf(":"));
										String _msg = msg.substring(msg
												.indexOf(":") + 1);
										if (to == null || _msg == null
												|| msg == null || name == null) {
											break;
										}
										if (p.getName().equalsIgnoreCase(to)) {
											p.sendMessage(IRCColor.LIGHT_GRAY
													.getMinecraftColor()
													+ "([IRC] from "
													+ name
													+ "):" + _msg);
										}
									}
								}
							}

							if (line.toLowerCase().contains(
									"QUIT :".toLowerCase())) {
								if (name.equalsIgnoreCase(Variables.name)
										&& msg == null) {
									Thread run = new Thread(RECONNECT);
									run.setDaemon(true);
									run.setPriority(Thread.MAX_PRIORITY);
									run.start();
									break;
								}
							}
							if (line.toLowerCase().startsWith("ping ")) {
								if (msg == null) {
									writer.write("PONG " + line.substring(5)
											+ "\r\n");
									writer.flush();
								}
							} else if (line.toLowerCase().contains("353")) {
								if (msg == null) {
									IRCChannel chan = null;
									String split = line.substring(line
											.indexOf(" :") + 2);
									String channel = line.substring(
											line.indexOf("#"),
											line.indexOf(" :"));
									StringTokenizer st = new StringTokenizer(
											split);
									ArrayList<String> list = new ArrayList<String>();
									while (st.hasMoreTokens()) {
										list.add(st.nextToken());
									}
									for (IRCChannel c : Variables.channels) {
										if (c.getChannel()
												.toLowerCase()
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
											} else if (s.contains("+")) {
												chan.getVoiceList()
														.add(s.substring(s
																.indexOf("+") + 1));
											}
										}
									}
								}
							}

						}
					} catch (final Exception e) {
						debug(e);
					}
				}
			} catch (IOException e) {
				debug(e);
			}
		}
	};

	private final Runnable DISPATCH = new Runnable() {
		public void run() {
			try {
				while (true) {
					if (messageQue.size() > 0) {
						if (isConnected(IRC.getIRCServer())) {
							for (final String str : messageQue) {
								writer.write(str + "\r\n");
								writer.flush();
								messageQue.remove(str);
								break;
							}
						}
					}
					try {
						Thread.sleep(1000 / Variables.limit);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			} catch (Exception e) {
				debug(e);
			}
		}

	};

	private final Runnable RECONNECT = new Runnable() {
		public void run() {
			try {
				if (IRC.getHandleManager().getIRCHandler()
						.isConnected(IRC.getIRCServer())) {
					IRC.getHandleManager().getIRCHandler()
							.disconnect(IRC.getIRCServer());
				}
				plugin.getSettingsManager().reload();
				IRC.getHandleManager().getIRCHandler()
						.connect(IRC.getIRCServer());
			} catch (final Exception e) {
				debug(e);
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
	public void sendMessage(final String Message, final String channel) {
		messageQue.add("PRIVMSG " + channel + " :" + Message);
	}

	/**
	 * Sends a message to the specified channel.
	 * 
	 * @param Message
	 *            The message to send.
	 * @param channel
	 *            The channel to send the message to.
	 */
	public void sendNotice(final String Message, final String reciever) {
		messageQue.add("NOTICE " + reciever + " :" + Message);
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
				debug(e);
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
				debug(e);
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

	private String getGroupSuffix(String name) {
		StringBuilder sb = new StringBuilder();
		String s = name;
		if (IRC.getHookManager().getChatHook() != null) {
			String prefix = IRC
					.getHookManager()
					.getChatHook()
					.getGroupSuffix(
							"",
							IRC.getHookManager().getChatHook()
									.getPrimaryGroup("", name));
			sb.append(prefix);
			String temp = sb.toString();
			s = temp.replace("&", "§");
		}
		return s;
	}

	private String getGroupPrefix(String name) {
		StringBuilder sb = new StringBuilder();
		String s = name;
		if (IRC.getHookManager().getChatHook() != null) {
			String prefix = IRC
					.getHookManager()
					.getChatHook()
					.getGroupPrefix(
							"",
							IRC.getHookManager().getChatHook()
									.getPrimaryGroup("", name));
			sb.append(prefix);
			String temp = sb.toString();
			s = temp.replace("&", "§");
		}
		return s;
	}

	private String getPrefix(String name) {
		StringBuilder sb = new StringBuilder();
		String s = name;
		if (IRC.getHookManager().getChatHook() != null) {
			String prefix = IRC.getHookManager().getChatHook()
					.getPlayerPrefix("", name);
			sb.append(prefix);
			String temp = sb.toString();
			s = temp.replace("&", "§");
		}
		return s;
	}

	private String getSuffix(String name) {
		StringBuilder sb = new StringBuilder();
		String s = name;
		if (IRC.getHookManager().getChatHook() != null) {
			String suffix = IRC.getHookManager().getChatHook()
					.getPlayerSuffix("", name);
			sb.append(suffix);
			String temp = sb.toString();
			s = temp.replace("&", "§");
		}
		return s;
	}

	private String getName(String name) {
		StringBuilder sb = new StringBuilder();
		String s = name;
		if (IRC.getHookManager().getChatHook() != null) {
			String color = name;
			sb.append(color);
			String temp = sb.toString();
			s = temp.replace("&", "§");
		}
		return s;
	}

	private void handleMessage(final IRCChannel c, final String name,
			final String message) {
		try {
			if (c.getChatType() == ChatType.ADMINCHAT) {
				if (IRC.getHookManager().getmcMMOHook() != null) {
					String format = "§b" + "{" + "§f" + "[IRC] "
							+ getPrefix(name) + getName(name) + getSuffix(name)
							+ "§b" + "} " + message;
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						if (p.isOp()
								|| mcPermissions.getInstance().adminChat(p))
							p.sendMessage(IRCColor.formatIRCMessage(format));
					}
				}
			} else if (c.getChatType() == ChatType.HEROCHAT && !Variables.hc4) {
				c.getHeroChatChannel().announce(
						Variables.mcformat
								.replace("{name}", getName(name))
								.replace("{message}",
										IRCColor.formatIRCMessage(message))
								.replace("{colon}", ":")
								.replace("{prefix}", getPrefix(name))
								.replace("{suffix}", getSuffix(name))
								.replace("{groupPrefix}", getGroupPrefix(name))
								.replace("{groupSuffix}", getGroupSuffix(name))
								+ c.getHeroChatChannel().getColor());
			} else if (c.getChatType() == ChatType.HEROCHAT
					&& IRC.getHookManager().getHeroChatHook() != null
					&& Variables.hc4) {
				c.getHeroChatFourChannel()
						.sendMessage(
								Variables.mcformat
										.replace("{name}", getName(name))
										.replace("{message}", "")
										.replace("{colon}", "")
										.replace("{prefix}", getPrefix(name))
										.replace("{suffix}", getSuffix(name))
										.replace("{groupPrefix}",
												getGroupPrefix(name))
										.replace("{groupSuffix}",
												getGroupSuffix(name)),
								IRCColor.formatIRCMessage(IRCColor
										.formatIRCMessage(message)),
								c.getHeroChatFourChannel().getMsgFormat(),
								false);
			} else if (c.getChatType() == ChatType.GLOBAL) {
				plugin.getServer().broadcastMessage(
						Variables.mcformat
								.replace("{name}", getName(name))
								.replace("{message}",
										IRCColor.formatIRCMessage(message))
								.replace("{colon}", ":")
								.replace("{prefix}", getPrefix(name))
								.replace("{suffix}", getSuffix(name))
								.replace("{groupPrefix}", getGroupPrefix(name))
								.replace("{groupSuffix}", getGroupSuffix(name))
								+ IRCColor.WHITE.getMinecraftColor());
			}
		} catch (Exception e) {
			debug(e);
		}
	}
}