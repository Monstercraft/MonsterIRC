package org.monstercraft.irc.ircplugin.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

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
	 * Sends a message to the IRC channel.
	 * 
	 * @param channel
	 *            The channel to send it to.
	 * @param message
	 *            The message to send.
	 */
	public static void sendMessage(final IRCChannel channel,
			final String message) {
		IRC.getHandleManager().getIRCHandler()
				.sendMessage(channel.getChannel(), message);
	}

	/**
	 * Sends a message to the IRC channel.
	 * 
	 * @param to
	 *            The person to send the message to.
	 * @param message
	 *            The message to send.
	 */
	public static void sendMessage(final String to, final String message) {
		IRC.getHandleManager().getIRCHandler().sendMessage(to, message);
	}

	/**
	 * Sends a message to a user on the IRC server.
	 * 
	 * @param to
	 *            The user to send it to.
	 * @param message
	 *            The message to send.
	 */
	public static void sendNotice(final String to, final String message) {
		IRC.getHandleManager().getIRCHandler().sendNotice(to, message);
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

}
