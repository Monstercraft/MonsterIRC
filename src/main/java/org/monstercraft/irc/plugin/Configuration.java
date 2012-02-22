package org.monstercraft.irc.plugin;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.ircplugin.IRC;
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
		} catch (SocketTimeoutException e) {
			IRC.log("The socket has timed out when attempting to connect!");
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

}
