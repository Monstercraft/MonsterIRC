package org.monstercraft.irc.plugin.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Methods {

	private final static Logger logger = Logger.getLogger("MineCraft");

	/**
	 * Logs a message to the console.
	 * 
	 * @param msg
	 *            The message to print.
	 */
	public static void log(final String msg) {
		logger.log(Level.INFO, "[IRC] " + msg);
	}

	/**
	 * Logs debugging messages to the console.
	 * 
	 * @param error
	 *            The message to print.
	 */
	public static void debug(final String error, final boolean console) {
		if (console) {
			logger.log(Level.WARNING, "[IRC - Debug] " + error);
		}
	}

	/**
	 * Logs debugging messages to the console.
	 * 
	 * @param error
	 *            The message to print.
	 */
	public static void debug(final Exception error) {
		logger.log(Level.SEVERE, "[IRC - Critical error detected!]");
		error.printStackTrace();
	}

	/**
	 * Stops the server
	 */
	public static void stop(final Plugin plugin) {
		Bukkit.getServer()
				.getPluginManager()
				.disablePlugin(
						Bukkit.getServer().getPluginManager()
								.getPlugin("MonsterIRC"));
	}

	/**
	 * Pings the host.
	 * 
	 * @param host
	 *            The host to ping
	 * @param port
	 *            The port the host is on.
	 * @param timeoutMs
	 *            The time in ms for the maximum ping response.
	 * @return The time in ms the ping took.
	 */
	public static int ping(final String host, final int port,
			final int timeoutMs) {
		int start = -1;
		int end = -1;
		int total = -1;
		Socket s = new Socket();
		try {
			InetAddress addr = InetAddress.getByName(host);
			SocketAddress sockaddr = new InetSocketAddress(addr, port);
			start = (int) System.currentTimeMillis();
			s.connect(sockaddr, timeoutMs);
			end = (int) System.currentTimeMillis();
		} catch (Exception e) {
			Methods.debug(e);
			start = -1;
			end = -1;
			total = -1;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (Exception e) {
					Methods.debug(e);
				}
			}
			if ((start != -1) && (end != -1)) {
				total = end - start;
			}
		}
		return total;
	}

}
