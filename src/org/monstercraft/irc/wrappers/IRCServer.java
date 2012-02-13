package org.monstercraft.irc.wrappers;

import java.util.List;

import org.monstercraft.irc.IRC;

public class IRCServer {

	private String server;
	private int port;
	private String nick;
	private String password;
	private boolean identify;
	private int timeoutMs;
	private int retrys;
	private List<String> connectCommands;

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

	public String getServer() {
		return server;
	}

	public int getPort() {
		return port;
	}

	public String getNick() {
		return nick;
	}

	public String getPassword() {
		return password;
	}

	public boolean isIdentifing() {
		return identify;
	}

	public int getTimeout() {
		return timeoutMs;
	}

	public int getRetrys() {
		return retrys;
	}

	public List<String> getConnectCommands() {
		return connectCommands;
	}

	public boolean isConnected() {
		return IRC.getHandleManager().getIRCHandler().isConnected(this);
	}

	public boolean connect() {
		return IRC.getHandleManager().getIRCHandler().connect(this);
	}

	public boolean disconnect() {
		return IRC.getHandleManager().getIRCHandler().disconnect(this);
	}

}
