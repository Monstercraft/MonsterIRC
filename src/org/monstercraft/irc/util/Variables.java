package org.monstercraft.irc.util;

import java.util.List;

/**
 * This class holds all of the variables used within the plugin.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class Variables {

	/**
	 * The Version of the Settings 0.0 on the first run.
	 */
	public static double version = 0.0;

	/**
	 * The option the pass all chat from and to IRC.
	 */
	public static boolean all = true;

	/**
	 * The option to send the IDENTIFY command to nickserv.
	 */
	public static boolean ident = false;

	/**
	 * The option to automatically join upon starting the plugin.
	 */
	public static boolean autoJoin = false;

	/**
	 * The option to use HeroChat as a chat plugin to pass chat.
	 */
	public static boolean hc = false;

	/**
	 * The port of the IRC server.
	 */
	public static int port = 6667;

	/**
	 * The IRC server to connect to.
	 */
	public static String server = "irc.esper.net";

	/**
	 * The channel to connect to.
	 */
	public static String channel = "#default";

	/**
	 * The Users login name.
	 */
	public static String login = "Default";

	/**
	 * The users nickname.
	 */
	public static String name = "default";

	/**
	 * The Hero Chat Channel that chat will be passed through.
	 */
	public static String hcc = "IRC";

	/**
	 * The password to identify with.
	 */
	public static String password = "default";

	/**
	 * The Hero Chat Channel that the .announce command will be sent to.
	 */
	public static String announce = "Announce";

	/**
	 * A list containing all of the muted users.
	 */
	public static List<String> muted = null;

}
