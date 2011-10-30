package org.monstercraft.irc.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.monstercraft.irc.Constants;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.Variables;

public class IRCHandler extends IRC {

	private static BufferedWriter writer = null;
	private static Socket connection = null;
	private static BufferedReader reader = null;
	private static Thread watch = null;

	public static void connect() {
		if (!isConnected()) {
			String line = null;
			try {
				connection = new Socket(Constants.SERVER, Constants.PORT);
				writer = new BufferedWriter(new OutputStreamWriter(
						connection.getOutputStream()));
				reader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				System.out.println("[IRC] Attempting to connect to chat.");
				writer.write("USER " + Constants.LOGIN + " 8 * :"
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
						return;
					} else if (line.toLowerCase().startsWith("ping ")) {
						// We must respond to PINGs to avoid being disconnected.
						writer.write("PONG " + line.substring(5) + "\r\n");
						writer.flush();
						continue;
					}
				}
				writer.write("JOIN " + Constants.CHANNEL + "\r\n");
				writer.flush();
				watch = new Thread(KEEP_ALIVE);
				watch.setDaemon(true);
				watch.setPriority(Thread.MIN_PRIORITY);
				watch.start();
				System.out.println("[IRC] Connected to chat as "
						+ Variables.name + ".");
			} catch (Exception e) {
				System.out.println("[IRC] Failed to connect to IRC!");
				System.out
						.println("[IRC] Please tell Fletch_to_99 the following!");
				e.printStackTrace();
				disconnect();
			}
		}
	}

	public static void disconnect() {
		if (isConnected()) {
			try {
				writer.write("QUIT " + Constants.CHANNEL + "\n");
				writer.flush();
			} catch (IOException e) {
			}
		}
		if (watch != null) {
			try {
				watch.interrupt();
			} catch (final IllegalThreadStateException ignored) {
			}
		}
		writer = null;
		reader = null;
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (final IOException ignored) {
		}
		connection = null;
		System.out.println("[IRC] Successfully disconnected from IRC.");
	}

	public static boolean isConnected() {
		return connection != null && connection.isConnected();
	}

	private final static Runnable KEEP_ALIVE = new Runnable() {
		public void run() {
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					if (line.toLowerCase().startsWith("ping ")) {
						// We must respond to PINGs to avoid being disconnected.
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
									+ " : " + "1" + (char) ctcpControl + "\r\n");
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
						if (line.contains(": ACTION")) {
							final String _name = line.substring(1,
									line.indexOf("!"));
							msg = _name
									+ " "
									+ line.substring(line.indexOf(": ACTION ") + 1);
						} else if (line
								.contains("PRIVMSG " + Constants.CHANNEL)) {
							name = line.substring(1, line.indexOf("!"));
							msg = line.substring(line.indexOf(":", 1) + 1);
						} else if (line.contains("NICK :")) {
							final String _name = line.substring(1,
									line.indexOf("!"));
							msg = _name
									+ " is now known as "
									+ line.substring(line.indexOf("NICK :") + 6);
						} else if (line.contains("JOIN :" + Constants.CHANNEL)) {
							final String _name = line.substring(1,
									line.indexOf("!"));
							msg = _name + " has joined " + Constants.CHANNEL
									+ ".";
						} else if (line.contains("PART " + Constants.CHANNEL)) {
							final String _name = line.substring(1,
									line.indexOf("!"));
							msg = _name + " has left " + Constants.CHANNEL
									+ ".";
						} else if (line.contains("QUIT :")) {
							final String _name = line.substring(1,
									line.indexOf("!"));
							msg = _name + " has quit " + Constants.CHANNEL
									+ " ("
									+ line.substring(line.indexOf(":", 1) + 1)
									+ ").";
						} else if (line.contains("MODE " + Constants.CHANNEL)) {
							final String _name = line.substring(1,
									line.indexOf("!"));
							final String comm = line
									.substring(line.indexOf("MODE "
											+ Constants.CHANNEL + " ") + 14);
							msg = _name + " sets mode " + comm + ".";
						} else if (line.contains("KICK " + Constants.CHANNEL)) {
							final String _name = line.substring(1,
									line.indexOf("!"));
							msg = _name + " has been kicked from"
									+ Constants.CHANNEL + ".";
						}
						if (msg != null) {
							if (msg.startsWith("IRC")) {
								server.broadcastMessage("[IRC] "
										+ (name != null ? name + ": " : "")
										+ msg.substring(4));
							}
						}
					} catch (final Exception ignored) {
					}
				}
			} catch (final IOException ignored) {
			} catch (final NullPointerException ignored) {
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

	public static void sendMessage(final String Message) {
		if (isConnected()) {
			try {
				writer.write("PRIVMSG " + Constants.CHANNEL + " :" + Message
						+ "\r\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void changeNick(final String Nick) {
		if (isConnected()) {
			try {
				writer.write("NICK " + Nick + "\r\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
