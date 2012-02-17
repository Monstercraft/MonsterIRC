package org.monstercraft.irc.plugin.wrappers;

import java.util.List;

/**
 * This class creates an IRCServer to connect to.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRCServer {

	private String server;
	private int port;
	private String nick;
	private String password;
	private boolean identify;
	private int timeoutMs;
	private int retrys;
	private List<String> connectCommands;

	/**
	 * Creates an IRCServer to connect to.
	 * 
	 * @param server
	 *            The IRC server to connect to.
	 * @param port
	 *            The port to bind to.
	 * @param nick
	 *            The users nick name.
	 * @param password
	 *            The password to identify with.
	 * @param identify
	 *            The option to identify.
	 * @param timeoutMs
	 *            The time to wait when connecting.
	 * @param retrys
	 *            The amount of times to attempt conencting.
	 * @param connectCommands
	 *            The commands to connect with.
	 */
	public IRCServer(final String server, final int port, final String nick,
			final String password, final boolean identify, final int timeoutMs,
			final int retrys, final List<String> connectCommands) {
		this.server = server;
		this.port = port;
		this.nick = nick;
		this.password = password;
		this.identify = identify;
		this.timeoutMs = timeoutMs;
		this.retrys = retrys;
		this.connectCommands = connectCommands;
	}

	/**
	 * Creates an IRCServer to connect to.
	 * 
	 * @param server
	 *            The IRC server to connect to.
	 * @param port
	 *            The port to bind to.
	 * @param nick
	 *            The users nick name.
	 * @param timeoutMs
	 *            The time to wait when connecting.
	 * @param retrys
	 *            The amount of times to attempt conencting.
	 * @param connectCommands
	 *            The commands to connect with.
	 */
	public IRCServer(final String server, final int port, final String nick,
			final int timeoutMs, final int retrys,
			final List<String> connectCommands) {
		this.server = server;
		this.port = port;
		this.nick = nick;
		this.password = "";
		this.identify = false;
		this.timeoutMs = timeoutMs;
		this.retrys = retrys;
		this.connectCommands = connectCommands;
	}

	/**
	 * Fetches the servers address.
	 * 
	 * @return The servers address.
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Fetches the servers port.
	 * 
	 * @return The servers port.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Fetches the users nick name.
	 * 
	 * @return The users nick name.
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * Fetches the users password.
	 * 
	 * @return The users password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Checks if the user is identifying with nickserv.
	 * 
	 * @return True if the user is identifying with nickserv; otherwise false.
	 */
	public boolean isIdentifing() {
		return identify;
	}

	/**
	 * Fetches the time to wait before retring the conenction.
	 * 
	 * @return The time in ms.
	 */
	public int getTimeout() {
		return timeoutMs;
	}

	/**
	 * The amount of times to test the connection.
	 * 
	 * @return The amount of times to retry the connection.
	 */
	public int getRetrys() {
		return retrys;
	}

	/**
	 * Fetches the commands to send on connection.
	 * 
	 * @return The commands to send upon connecting to the server.
	 */
	public List<String> getConnectCommands() {
		return connectCommands;
	}

}
