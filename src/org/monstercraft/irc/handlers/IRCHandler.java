package org.monstercraft.irc.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.util.ChatType;
import org.monstercraft.irc.util.IRCColor;
import org.monstercraft.irc.util.Pinger;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;

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
	private IRC plugin;
	private boolean connected = false;

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
	 *            The server to connec to.
	 * @param port
	 *            The port to use.
	 * @param nick
	 *            The nick name.
	 * @param password
	 *            The password when identifing.
	 * @param identify
	 *            Weither the user wants to identify with nickserv.
	 * @param timeoutMs
	 *            The time to wait for a reply.
	 * @return True if connected successfully; otherwise false.
	 */
	public boolean connect(final String server, final int port,
			final String nick, final String password, final boolean identify,
			final int timeoutMs) {
		if (!isConnected()) {
			String line = null;
			long ping = 0;
			int tries = 0;
			for (int i = 0; i < Variables.tries; i++) {
				ping = Pinger.ping(Variables.server, Variables.port, timeoutMs);
				if (ping > 0) {
					tries = i;
					break;
				}
			}
			if (ping > 0) {
				log("The IRC server took " + ping + " MS to respond with "
						+ tries + " retrys.");
				try {
					connection = new Socket(server, port);
					writer = new BufferedWriter(new OutputStreamWriter(
							connection.getOutputStream()));
					reader = new BufferedReader(new InputStreamReader(
							connection.getInputStream()));
					log("Attempting to connect to chat.");
					if (identify) {
						writer.write("PASS " + password + "\r\n");
						writer.flush();
					}
					writer.write("NICK " + nick + "\r\n");
					writer.flush();
					writer.write("USER " + nick + " 8 * :"
							+ plugin.getDescription().getVersion() + "\r\n");
					writer.flush();
					log("Processing connection....");
					while ((line = reader.readLine()) != null) {
						debug(line);
						if (line.contains("004")) {
							break;
						} else if (line.contains("433")) {
							if (!identify) {
								log("Your nickname is already in use, please switch it");
								log("using \"nick [NAME]\" and try to connect again.");
								disconnect();
								return false;
							} else {
								log("Sending ghost command....");
								writer.write("NICKSERV GHOST " + nick + " "
										+ password + "\r\n");
								writer.flush();
							}
						} else if (line.toLowerCase().startsWith("ping ")) {
							writer.write("PONG " + line.substring(5) + "\r\n");
							writer.flush();
							continue;
						}
					}
					if (identify) {
						log("Identifying with Nickserv....");
						writer.write("NICKSERV IDENTIFY " + password + "\r\n");
						writer.flush();
					}
					for (IRCChannel c : Variables.channels) {
						if (c.isAutoJoin()) {
							join(c.getChannel());

						}
					}
					watch = new Thread(KEEP_ALIVE);
					watch.setDaemon(true);
					watch.setPriority(Thread.MAX_PRIORITY);
					watch.start();
					connected = true;
				} catch (Exception e) {
					log("Failed to connect to IRC!");
					debug(e);
					disconnect();
				}
			} else {
				log("The IRC server seems to be down or running slowly!");
				log("The plugin will now stop.");
				connected = false;
				return false;
			}
		}
		return isConnected();
	}

	/**
	 * Disconnects a user from the IRC server.
	 * 
	 * @return True if we disconnect successfully; otherwise false.
	 */
	public boolean disconnect() {
		if (isConnected()) {
			try {
				for (IRCChannel c : Variables.channels) {
					leave(c.getChannel());
				}
				connected = false;
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
				if (!connection.isClosed()) {
					connection.shutdownInput();
					connection.shutdownOutput();
					connection.close();
				}
				if (watch != null) {
					watch.interrupt();
				}
				watch = null;
				writer = null;
				reader = null;
				connection = null;
				log("Successfully disconnected from IRC.");
			} catch (Exception e) {
				debug(e);
			}
		}
		return !isConnected();
	}

	/**
	 * Checks if the user is connected to an IRC server.
	 * 
	 * @return True if conencted to an IRC server; othewise false.
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Joins an IRC channel on that server.
	 * 
	 * @param channel
	 *            The channel to join.
	 */
	public void join(final String channel) {
		try {
			writer.write("JOIN " + channel + "\r\n");
			writer.flush();
			log("Successfully joined " + channel);
		} catch (IOException e) {
			debug(e);
		}
	}

	/**
	 * Quits a channel in the IRC
	 * 
	 * @param channel
	 *            The channel to leave.
	 * @throws IOException
	 */
	public void leave(final String channel) throws IOException {
		if (isConnected()) {
			writer.write("QUIT " + channel + "\r\n");
			writer.flush();
		}
	}

	private final Runnable KEEP_ALIVE = new Runnable() {
		public void run() {
			try {
				if (isConnected() && reader != null && reader.ready()) {
					String line;
					try {
						while ((line = reader.readLine()) != null) {
							if (!isConnected()) {
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

	/**
	 * Sends a message to the specified channel.
	 * 
	 * @param Message
	 *            The message to send.
	 * @param channel
	 *            The channel to send the message to.
	 */
	public void sendMessage(final String Message, final String channel) {
		if (isConnected() && writer != null) {
			try {
				writer.write("PRIVMSG " + channel + " :" + Message + "\r\n");
				writer.flush();
			} catch (IOException e) {
				debug(e);
			}
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
	public void sendNotice(final String Message, final String reciever) {
		if (isConnected() && writer != null) {
			try {
				writer.write("NOTICE " + reciever + " :" + Message + "\r\n");
				writer.flush();
			} catch (IOException e) {
				debug(e);
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
		if (isConnected()) {
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
	public void ban(final String Nick, final String channel) {
		if (isConnected()) {
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

	private String getSpecialName(String name) {
		StringBuilder sb = new StringBuilder();
		String s = name;
		if (IRC.getHookManager().getChatHook() != null) {
			String prefix = IRC.getHookManager().getChatHook()
					.getPlayerPrefix("", name);
			String suffix = IRC.getHookManager().getChatHook()
					.getPlayerSuffix("", name);
			String color = name;
			if (!color.contains("&")) {
				color = "&f" + color;
			}
			sb.append(prefix);
			sb.append(color);
			sb.append(suffix);
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
							+ getSpecialName(name) + "§b" + "} " + message;
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						if (p.isOp()
								|| mcPermissions.getInstance().adminChat(p))
							p.sendMessage(format);
					}
				}
			} else if (c.getChatType() == ChatType.HEROCHAT && !Variables.hc4) {
				c.getHeroChatChannel().announce(
						Variables.format
								.replace(
										"{name}",
										getSpecialName(name)
												+ c.getHeroChatChannel()
														.getColor())
								.replace("{message}", message)
								.replace("{colon}", ":"));
			} else if (c.getChatType() == ChatType.HEROCHAT
					&& IRC.getHookManager().getHeroChatHook() != null
					&& Variables.hc4) {
				c.getHeroChatFourChannel().sendMessage(
						"<" + getSpecialName(name) + ">",
						IRCColor.formatIRCMessage(message),
						c.getHeroChatFourChannel().getMsgFormat(), false);
			} else if (c.getChatType() == ChatType.GLOBAL) {
				plugin.getServer().broadcastMessage(
						"[IRC]"
								+ Variables.format
										.replace("{name}",
												getSpecialName(name) + "§f")
										.replace("{message}", message)
										.replace("{colon}", ":"));
			}
		} catch (Exception e) {
			debug(e);
		}
	}
}