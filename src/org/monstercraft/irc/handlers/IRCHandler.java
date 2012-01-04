package org.monstercraft.irc.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.util.Variables;

public class IRCHandler extends IRC {

	private BufferedWriter writer = null;
	private Socket connection = null;
	private BufferedReader reader = null;
	private Thread watch = null;
	private boolean avalible = true;
	private IRC plugin;
	private ArrayList<String> users = new ArrayList<String>();

	public IRCHandler(IRC plugin) {
		this.plugin = plugin;
	}

	public boolean connect() {
		if (!isConnected()) {
			String line = null;
			try {
				connection = new Socket(Variables.server, Variables.port);
				writer = new BufferedWriter(new OutputStreamWriter(
						connection.getOutputStream()));
				reader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				log("Attempting to connect to chat.");
				writer.write("USER " + Variables.login + " 8 * :"
						+ Variables.name + "\r\n");
				writer.write("NICK " + Variables.name + "\r\n");
				writer.flush();
				log("Processing connection....");
				while ((line = reader.readLine()) != null) {
					if (line.contains("004")) {
						break;
					} else if (line.contains("433")) {
						log("Your nickname is already in use, please switch it");
						log("using \"nick [NAME]\" and try to connect again.");
						disconnect();
						avalible = false;
						break;
					} else if (line.toLowerCase().startsWith("ping ")) {
						writer.write("PONG " + line.substring(5) + "\r\n");
						writer.flush();
						continue;
					}
				}
				if (avalible) {
					if (Variables.ident) {
						log("Identifying with Nickserv....");
						writer.write("NICKSERV IDENTIFY " + Variables.password
								+ "\r\n");
						writer.flush();
					}
					writer.write("JOIN " + Variables.channel + "\r\n");
					writer.flush();
					watch = new Thread(KEEP_ALIVE);
					watch.setDaemon(true);
					watch.setPriority(Thread.MAX_PRIORITY);
					watch.start();
					log("Connected to chat as " + Variables.name + ".");
				}
			} catch (Exception e) {
				log("Failed to connect to IRC!");
				log("Please tell Fletch_to_99 the following!");
				e.printStackTrace();
				disconnect();
			}
		}
		return isConnected();
	}

	public boolean disconnect() {
		try {
			if (isConnected()) {
				avalible = true;
				writer.write("QUIT " + Variables.channel + "\n");
				writer.flush();
			}
			if (watch != null) {
				watch.interrupt();
				watch = null;
			}
			if (connection != null) {
				connection.shutdownInput();
				connection.shutdownOutput();
				connection.close();
				connection = null;
			}
			reader.close();
			writer.close();
			writer = null;
			reader = null;
			log("Successfully disconnected from IRC.");
			connection = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return !isConnected();
	}

	public boolean isConnected() {
		if (connection != null) {
			connection.isConnected();
		}
		return false;
	}

	private final Runnable KEEP_ALIVE = new Runnable() {
		public void run() {
			try {
				if (isConnected() && reader != null && reader.ready()) {
					String line;
					try {
						while ((line = reader.readLine()) != null) {
							if (line.toLowerCase().startsWith("ping ")) {
								writer.write("PONG " + line.substring(5)
										+ "\r\n");
								writer.flush();
								continue;
							} else if (line.contains("353")) {
								users.clear();
								listImportance(line, users);
							} else if (isCTCP(line)) {
								final String _name = line.substring(1,
										line.indexOf("!"));
								final String ctcpMsg = getCTCPMessage(line)
										.toUpperCase();
								if (ctcpMsg.equals("VERSION")) {
									writer.write("NOTICE " + _name + " :"
											+ (char) ctcpControl
											+ "Monstercraft" + " : " + "1"
											+ (char) ctcpControl + "\r\n");
									writer.flush();
								} else if (ctcpMsg.equals("TIME")) {
									final SimpleDateFormat sdf = new SimpleDateFormat(
											"dd MMM yyyy hh:mm:ss zzz");
									writer.write("NOTICE " + _name + " :"
											+ (char) ctcpControl
											+ sdf.format(new Date())
											+ (char) ctcpControl + "\r\n");
									writer.flush();
								}
								continue;
							}
							try {
								String name = null;
								String msg = null;
								if (line.contains("PRIVMSG "
										+ Variables.channel)) {
									name = line.substring(1, line.indexOf("!"));
									msg = line
											.substring(line.indexOf(":", 1) + 1);
								} else if (line.contains("NICK :")) {
									final String _name = line.substring(1,
											line.indexOf("!"));
									msg = _name
											+ " is now known as "
											+ line.substring(line
													.indexOf("NICK :") + 6);
									if (users.contains(_name)) {
										users.remove(_name);
										users.add(line.substring(line
												.indexOf("NICK :") + 6));
									}
								} else if (line.contains("JOIN :"
										+ Variables.channel)) {
									final String _name = line.substring(1,
											line.indexOf("!"));
									if (!users.contains(_name)) {
										users.add(_name);
									}
									msg = _name + " has joined "
											+ Variables.channel + ".";
								} else if (line.contains("PART "
										+ Variables.channel)) {
									final String _name = line.substring(1,
											line.indexOf("!"));
									msg = _name + " has left "
											+ Variables.channel + ".";
									if (users.contains(_name)) {
										users.remove(_name);
									}
								} else if (line.contains("QUIT :")) {
									final String _name = line.substring(1,
											line.indexOf("!"));
									msg = _name
											+ " has quit "
											+ Variables.channel
											+ " ("
											+ line.substring(line.indexOf(":",
													1) + 1) + ").";
									if (users.contains(_name)) {
										users.remove(_name);
									}
								} else if (line.contains("MODE "
										+ Variables.channel + " +v")
										|| line.contains("MODE "
												+ Variables.channel + " -v")
										|| line.contains("MODE "
												+ Variables.channel + " +o")
										|| line.contains("MODE "
												+ Variables.channel + " -o")) {
									name = line.substring(line.indexOf("MODE "
											+ Variables.channel + " ")
											+ Variables.channel.length() + 9);
									final String mode = line.substring(
											line.indexOf("MODE "
													+ Variables.channel + " ")
													+ Variables.channel
															.length() + 6,
											line.indexOf("MODE "
													+ Variables.channel + " ")
													+ Variables.channel
															.length() + 8);
									if (mode.contains("+v")) {
										if (users.contains(name)) {
											users.remove(name);
										}
										users.add("+" + name);
									} else if (mode.contains("+o")) {
										if (users.contains(name)) {
											users.remove(name);
										}
										users.add("@" + name);
									} else if (mode.contains("-o")) {
										if (users.contains(name)) {
											users.remove(name);
										}
										users.remove("@" + name);
									} else if (mode.contains("-v")) {
										if (users.contains(name)) {
											users.remove(name);
										}
										users.remove("+" + name);
									}
									name = line.substring(1, line.indexOf("!"));
									final String _name = line.substring(line
											.indexOf("MODE "
													+ Variables.channel + " ")
											+ Variables.channel.length() + 9);
									msg = _name + " has mode " + mode + ".";
								} else if (line.contains("KICK "
										+ Variables.channel)) {
									final String _name = line.substring(1,
											line.indexOf("!"));
									msg = _name + " has been kicked from"
											+ Variables.channel + ".";
									if (users.contains(_name)) {
										users.add(_name);
									}
								}

								if (msg != null && name != null) {
									if (msg.contains(".say")) {
										if (isOp(name) || isVoice(name)) {
											if (Variables.all) {
												plugin.getServer()
														.broadcastMessage(
																"[IRC]<"
																		+ name
																		+ ">: "
																		+ removeColors(msg
																				.substring(5)));
											} else if (Variables.hc
													&& plugin.herochat.HeroChatHook != null) {
												plugin.herochat.HeroChatHook
														.getChannelManager()
														.getChannel(
																Variables.announce)
														.sendMessage(
																"<" + name
																		+ ">",
																removeColors(msg
																		.substring(5)),
																plugin.herochat.HeroChatHook
																		.getChannelManager()
																		.getChannel(
																				Variables.announce)
																		.getMsgFormat(),
																false);
											}
										}
									} else if (!Variables.muted.contains(name
											.toLowerCase())) {
										if (Variables.all) {
											plugin.getServer()
													.broadcastMessage(
															"[IRC]<"
																	+ name
																	+ ">: "
																	+ removeColors(msg));
										} else if (Variables.hc
												&& plugin.herochat.HeroChatHook != null) {
											plugin.herochat.HeroChatHook
													.getChannelManager()
													.getChannel(Variables.hcc)
													.sendMessage(
															"<" + name + ">",
															removeColors(msg),
															plugin.herochat.HeroChatHook
																	.getChannelManager()
																	.getChannel(
																			Variables.hcc)
																	.getMsgFormat(),
															false);
										}
									}
								}
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
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

	public void sendMessage(final String Message) {
		if (isConnected()) {
			try {
				writer.write("PRIVMSG " + Variables.channel + " :" + Message
						+ "\r\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void changeNick(final String Nick) {
		if (isConnected()) {
			try {
				writer.write("NICK " + Nick + "\r\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void ban(final String Nick) {
		if (isConnected()) {
			try {
				writer.write("KICK " + Variables.channel + " " + Nick + "\r\n");
				writer.flush();
				writer.write("MODE " + Variables.channel + " +b" + Nick
						+ "\r\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isOp(final String sender) {
		for (Object o : users.toArray()) {
			if (((String) o).contains(sender) && ((String) o).contains("@")) {
				return true;
			}
		}
		return false;
	}

	private boolean isVoice(final String sender) {
		for (Object o : users.toArray()) {
			if (((String) o).contains(sender) && ((String) o).contains("+")) {
				return true;
			}
		}
		return false;
	}

	private String removeColors(final String msg) {
		String ns = msg;
		if (msg.toLowerCase().contains("&")) {
			ns = msg.replace("&", "");
		}
		return ns;
	}

	private void listImportance(final String message,
			final ArrayList<String> users) {
		StringTokenizer st = new StringTokenizer(message);
		while (st.hasMoreTokens()) {
			users.add(st.nextToken());
		}
	}
}