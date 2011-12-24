package org.monstercraft.irc.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.util.Variables;

public class IRCHandler extends IRC {

	private BufferedWriter writer = null;
	private Socket connection = null;
	private BufferedReader reader = null;
	private Thread watch = null;
	private boolean avalible = true;
	private IRC plugin;

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
				System.out.println("[IRC] Attempting to connect to chat.");
				writer.write("USER " + Variables.login + " 8 * :"
						+ Variables.name + "\r\n");
				writer.write("NICK " + Variables.name + "\r\n");
				writer.flush();
				System.out.println("[IRC] Processing connection....");
				while ((line = reader.readLine()) != null) {
					if (line.contains("004")) {
						break;
					} else if (line.contains("433")) {
						System.out
								.println("[IRC] Your nickname is already in use, please switch it");
						System.out
								.println("[IRC] using \"nick [NAME]\" and try to connect again.");
						disconnect();
						avalible = false;
						break;
					} else if (line.toLowerCase().startsWith("ping ")) {
						// We must respond to PINGs to avoid being disconnected.
						writer.write("PONG " + line.substring(5) + "\r\n");
						writer.flush();
						continue;
					}
				}
				if (avalible) {
					if (Variables.ident) {
						System.out.println("[IRC] Identifying....");
						writer.write("PRIVMSG NICKSERV :IDENTIFY "
								+ Variables.password + "\r\n");
						writer.flush();
					}
					writer.write("JOIN " + Variables.channel + "\r\n");
					writer.flush();
					watch = new Thread(KEEP_ALIVE);
					watch.setDaemon(true);
					watch.setPriority(Thread.MAX_PRIORITY);
					watch.start();
					System.out.println("[IRC] Connected to chat as "
							+ Variables.name + ".");
				}
			} catch (Exception e) {
				System.out.println("[IRC] Failed to connect to IRC!");
				System.out
						.println("[IRC] Please tell Fletch_to_99 the following!");
				e.printStackTrace();
				disconnect();
			}
		}
		return isConnected();
	}

	public boolean disconnect() {
		if (isConnected()) {
			avalible = true;
			try {
				writer.write("QUIT " + Variables.channel + "\n");
				writer.flush();
			} catch (IOException e) {
			}
		}
		if (watch != null) {
			try {
				watch.interrupt();
				watch = null;
			} catch (final IllegalThreadStateException ignored) {
			}
		}
		writer = null;
		reader = null;
		try {
			if (connection != null) {
				connection.shutdownInput();
				connection.shutdownOutput();
				connection.close();
				connection = null;
			}
		} catch (final IOException ignored) {
		}
		System.out.println("[IRC] Successfully disconnected from IRC.");
		return !isConnected();
	}

	public boolean isConnected() {
		return connection != null && connection.isConnected();
	}

	private final Runnable KEEP_ALIVE = new Runnable() {
		public void run() {
			if (isConnected()) {
				String line;
				try {
					while ((line = reader.readLine()) != null) {
						if (line.toLowerCase().startsWith("ping ")) {
							// We must respond to PINGs to avoid being
							// disconnected.
							writer.write("PONG " + line.substring(5) + "\r\n");
							writer.flush();
							continue;
						} else if (isCTCP(line)) {
							final String _name = line.substring(1,
									line.indexOf("!"));
							final String ctcpMsg = getCTCPMessage(line)
									.toUpperCase();
							if (ctcpMsg.equals("VERSION")) {
								writer.write("NOTICE " + _name + " :"
										+ (char) ctcpControl + "Monstercraft"
										+ " : " + "1" + (char) ctcpControl
										+ "\r\n");
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
							if (line.contains("PRIVMSG " + Variables.channel)) {
								name = line.substring(1, line.indexOf("!"));
								msg = line.substring(line.indexOf(":", 1) + 1);
							} else if (line.contains("NICK :")) {
								final String _name = line.substring(1,
										line.indexOf("!"));
								msg = _name
										+ " is now known as "
										+ line.substring(line.indexOf("NICK :") + 6);
							} else if (line.contains("JOIN :"
									+ Variables.channel)) {
								final String _name = line.substring(1,
										line.indexOf("!"));
								msg = _name + " has joined "
										+ Variables.channel + ".";
							} else if (line.contains("PART "
									+ Variables.channel)) {
								final String _name = line.substring(1,
										line.indexOf("!"));
								msg = _name + " has left " + Variables.channel
										+ ".";
							} else if (line.contains("QUIT :")) {
								final String _name = line.substring(1,
										line.indexOf("!"));
								msg = _name
										+ " has quit "
										+ Variables.channel
										+ " ("
										+ line.substring(line.indexOf(":", 1) + 1)
										+ ").";
							} else if (line.contains("MODE "
									+ Variables.channel)) {
								final String _name = line.substring(1,
										line.indexOf("!"));
								final String comm = line.substring(line
										.indexOf("MODE " + Variables.channel
												+ " ") + 14);
								msg = _name + " sets mode " + comm + ".";
							} else if (line.contains("KICK "
									+ Variables.channel)) {
								final String _name = line.substring(1,
										line.indexOf("!"));
								msg = _name + " has been kicked from"
										+ Variables.channel + ".";
							}

							if (msg != null && name != null) {
								if (!Variables.muted.contains(name
										.toLowerCase())) {
									plugin.herochat.HeroChatHook
											.getChannelManager()
											.getChannel(Variables.hc)
											.sendMessage(
													"<" + name + ">",
													msg,
													plugin.herochat.HeroChatHook
															.getChannelManager()
															.getChannel(
																	Variables.hc)
															.getMsgFormat(),
													false);
								}
							}
						} catch (final Exception e) {
						}
					}
				} catch (final Exception e) {
				}
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
				writer.write("KICK " + Variables.channel + " " +  Nick
						+ "\r\n");
				writer.flush();
				writer.write("MODE " + Variables.channel + " +b" + Nick
						+ "\r\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}