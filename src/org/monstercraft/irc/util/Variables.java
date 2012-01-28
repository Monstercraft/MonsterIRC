package org.monstercraft.irc.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.monstercraft.irc.wrappers.IRCChannel;

/**
 * This class holds all of the variables used within the plugin.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class Variables {

	/**
	 * The option to debug.
	 */
	public static boolean debug = false;

	/**
	 * The option to send the IDENTIFY command to nickserv.
	 */
	public static boolean ident = false;

	/**
	 * The port of the IRC server.
	 */
	public static int port = 6667;

	/**
	 * The IRC server to connect to.
	 */
	public static String server = "irc.esper.net";

	/**
	 * The Users login name.
	 */
	public static String login = "Default";

	/**
	 * The users nickname.
	 */
	public static String name = "default";

	/**
	 * The password to identify with.
	 */
	public static String password = "default";

	/**
	 * A list containing all of the muted users.
	 */
	public static ArrayList<String> muted = null;

	/**
	 * A Set of all the IRC channels.
	 */
	public static Set<IRCChannel> channels = new HashSet<IRCChannel>();

}
