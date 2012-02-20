package org.monstercraft.irc.plugin;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.plugin.util.Methods;
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
			Methods.debug(e);
		}
		return currentVersion;
	}

}
