package org.monstercraft.irc.plugin;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class holds all of the configuration data used within the plugin.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class Configuration {

	static {
		IRC.getLogger().setLevel(Level.ALL);
		final ArrayList<String> dirs = new ArrayList<String>();
		dirs.add(Paths.PLUGINS);
		dirs.add(Paths.CHANNELS);
		for (final String name : dirs) {
			final File dir = new File(name);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}
	}

	public static class Paths {

		/**
		 * The location in which to save the files.
		 */
		public static final String ROOT = "plugins" + File.separator
				+ "MonsterIRC" + File.separator;

		/**
		 * The configuration file.
		 */
		public static final String SETTINGS_FILE = ROOT + File.separatorChar
				+ "Settings.yml";

		/**
		 * The locaton where the plugins for MonsterIRC are stored.
		 */
		public static String PLUGINS = ROOT + File.separator + "Plugins";

		/**
		 * The Channel Directory.
		 */
		public static final String CHANNELS = ROOT + File.separator
				+ "Channels" + File.separator;

	}

	public static class URLS {
		public static String UPDATE_URL = "http://dev.bukkit.org/server-mods/monsterirc/files.rss";
	}

	public static String getCurrentVerison(final Plugin plugin) {
		return plugin.getDescription().getVersion();
	}

	/**
	 * Checks to see if the plugin is the latest version. Thanks to vault for
	 * letting me use their code.
	 * 
	 * @param currentVersion
	 *            The version that is currently running.
	 * @return The latest version
	 */
	public static String checkForUpdates(final Plugin plugin, final String site) {
		String currentVersion = getCurrentVerison(plugin);
		try {
			URL url = new URL(site);
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("item");
			Node firstNode = nodes.item(0);
			if (firstNode.getNodeType() == 1) {
				Element firstElement = (Element) firstNode;
				NodeList firstElementTagName = firstElement
						.getElementsByTagName("title");
				Element firstNameElement = (Element) firstElementTagName
						.item(0);
				NodeList firstNodes = firstNameElement.getChildNodes();
				return firstNodes.item(0).getNodeValue();
			}
		} catch (Exception e) {
			IRC.debug(e);
		}
		return currentVersion;
	}

	/**
	 * Pings the host.
	 * 
	 * @param host
	 *            The host to ping.
	 * @param port
	 *            The port the host is on.
	 * @param timeoutMs
	 *            The time in ms for the maximum ping response.
	 * @return The time in ms the ping took.
	 */
	public static long ping(final String host, final int port,
			final int timeoutMs) {
		long start = -1;
		long end = -1;
		long total = -1;
		Socket s = new Socket();
		try {
			InetAddress addr = InetAddress.getByName(host);
			SocketAddress sockaddr = new InetSocketAddress(addr, port);
			start = System.currentTimeMillis();
			s.connect(sockaddr, timeoutMs);
			end = System.currentTimeMillis();
		} catch (SocketTimeoutException e) {
			IRC.log("The socket has timed out when attempting to connect!");
			IRC.log("Try running /irc reload in a few mins!");
			start = -1;
			end = -1;
			total = -1;
		} catch (ConnectException e) {
			IRC.log("Your connection was refused by the IRC server!");
			IRC.log("Try running /irc reload in a few mins!");
			start = -1;
			end = -1;
			total = -1;
		} catch (IOException e) {
			IRC.debug(e);
			start = -1;
			end = -1;
			total = -1;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (Exception e) {
					IRC.debug(e);
				}
			}
			if ((start != -1) && (end != -1)) {
				total = end - start;
			}
		}
		return total;
	}

	public static String getClassPath() {
		String path = new File(Configuration.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath()).getAbsolutePath();
		try {
			path = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException ignored) {
		}
		return path;
	}

	public static class Variables {

		/**
		 * The amount of times to attempt to connect to the server.
		 */
		public static int tries = 5;

		/**
		 * The option to enable colors.
		 */
		public static boolean colors = true;

		/**
		 * Pass chat only when the bots name is said.
		 */
		public static boolean passOnName = false;

		/**
		 * The option to debug.
		 */
		public static boolean debug = true;

		/**
		 * Hero chat 4 compatiblilty.
		 */
		public static boolean hc4 = false;

		/**
		 * The option to execute ingame commands as and IRC OP.
		 */
		public static boolean ingamecommands = false;

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
		 * The users nickname.
		 */
		public static String name = "default";

		/**
		 * The password to identify with.
		 */
		public static String password = "default";

		/**
		 * The format for the game messages to be recieved in.
		 */
		public static String mcformat = "<{groupPrefix}{prefix}{name}{suffix}{groupSuffix}>: {message}";

		/**
		 * The format for the irc messages to be recieved in.
		 */
		public static String ircformat = "<{groupPrefix}{prefix}{name}{suffix}{groupSuffix}>: {message}";

		/**
		 * The prefix to be used when detecting an IRC command.
		 */
		public static String commandPrefix = ".";

		/**
		 * A list containing all of the muted users.
		 */
		public static List<String> muted = new ArrayList<String>();

		/**
		 * A Set of all the IRC channels.
		 */
		public static Set<IRCChannel> channels = new HashSet<IRCChannel>();

		/**
		 * The time to wait before disconnecting.
		 */
		public static int timeout = 2000;

		/**
		 * The time to wait before disconnecting.
		 */
		public static int limit = 2;

		/**
		 * The commands to send on.
		 */
		public static List<String> connectCommands = new ArrayList<String>();

		/**
		 * Option to hide MODE messages in irc.
		 */
		public static boolean hideMode = false;

		/**
		 * The option to pass the console commands "say".
		 */
		public static boolean passSay = false;

		/**
		 * The password for the server.
		 */
		public static String serverPass = "";

		/**
		 * The last person to reply to.
		 */
		public static Map<Player, String> reply = new HashMap<Player, String>();
	}
}
