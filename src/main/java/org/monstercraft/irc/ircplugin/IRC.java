package org.monstercraft.irc.ircplugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class IRC {

	private final static Logger logger = Logger.getLogger(IRC.class.getSimpleName());

	/**
	 * Fetches the logger.
	 * 
	 * @return The logger.
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * Logs a message to the console.
	 * 
	 * @param msg
	 *            The message to print.
	 */
	public static void log(final String msg) {
		logger.log(Level.INFO, "[MonsterIRC] " + msg);
	}

	/**
	 * Logs debugging messages to the console.
	 * 
	 * @param error
	 *            The message to print.
	 */
	public static void debug(final String error, final boolean console) {
		if (console) {
			logger.log(Level.WARNING, "[MonsterIRC - Debug] " + error);
		}
	}

	/**
	 * Sends a message to the MonsterIRC channel.
	 * 
	 * @param channel
	 *            The channel to send it to.
	 * @param message
	 *            The message to send.
	 */
	public static void sendMessage(final IRCChannel channel,
			final String message) {
		MonsterIRC.getHandleManager().getIRCHandler()
				.sendMessage(channel.getChannel(), message);
	}

	/**
	 * Sends a message to the MonsterIRC channel.
	 * 
	 * @param to
	 *            The person to send the message to.
	 * @param message
	 *            The message to send.
	 */
	public static void sendMessage(final String to, final String message) {
		MonsterIRC.getHandleManager().getIRCHandler().sendMessage(to, message);
	}

	/**
	 * Sends a message to a user on the MonsterIRC server.
	 * 
	 * @param to
	 *            The user to send it to.
	 * @param message
	 *            The message to send.
	 */
	public static void sendNotice(final String to, final String message) {
		MonsterIRC.getHandleManager().getIRCHandler().sendNotice(to, message);
	}

	/**
	 * Logs debugging messages to the console.
	 * 
	 * @param error
	 *            The message to print.
	 */
	public static void debug(final Exception error) {
		logger.log(Level.SEVERE, "[MonsterIRC - Critical error detected!]");
		error.printStackTrace();
	}

}
