package org.monstercraft.irc;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.monstercraft.irc.listeners.IRCListener;
import org.monstercraft.irc.managers.CommandManager;
import org.monstercraft.irc.managers.HandleManager;
import org.monstercraft.irc.managers.HookManager;
import org.monstercraft.irc.util.Settings;
import org.monstercraft.irc.util.Variables;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents the main plugin. All actions related to the plugin are
 * forwarded by this class
 * 
 * @author Fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRC extends JavaPlugin {

	private static HandleManager handles = null;
	private static HookManager hooks = null;
	private static CommandManager command = null;
	private static IRCListener listener = null;

	private static Logger logger = Logger.getLogger("MineCraft");

	private Settings settings = null;
	private Thread watch = null;
	private IRC plugin;
	private Object lock = new Object();

	public void onEnable() {
		plugin = this;
		log("Starting plugin.");
		settings = new Settings(plugin);
		hooks = new HookManager(plugin);
		handles = new HandleManager(plugin);
		command = new CommandManager(plugin);
		listener = new IRCListener(plugin);
		getServer().getPluginManager().registerEvents(listener, plugin);
		synchronized (lock) {
			watch = new Thread(STARTUP);
			watch.setDaemon(true);
			watch.setPriority(Thread.MAX_PRIORITY);
			watch.start();
		}
	}

	private final Runnable STARTUP = new Runnable() {
		public void run() {
			try {
				String currentVersion = getDescription().getVersion();
				String newVersion = updateCheck(currentVersion);
				if (!newVersion.contains(currentVersion)) {
					log(newVersion + " is out! You are running "
							+ currentVersion);
					log("Update MonsterIRC at: http://dev.bukkit.org/server-mods/monsterirc");
				} else {
					log("You are using the latest version of MonsterIRC");
				}
				if (!settings.firstRun()) {
					if (getHandleManager().getIRCHandler().connect(
							Variables.server, Variables.port, Variables.name,
							Variables.password, Variables.ident,
							Variables.timeout)) {
						log("Successfully started up.");
					} else {
						stop();
					}
				} else {
					stop();
				}
			} catch (Exception e) {
				debug(e);
			}
		}
	};

	public void onDisable() {
		if (!settings.firstRun()) {
			if (getHandleManager().getIRCHandler() != null) {
				if (getHandleManager().getIRCHandler().isConnected()) {
					getHandleManager().getIRCHandler().disconnect();
				}
			}
		} else {
			log("Please go edit your config!");
		}
		log("Successfully disabled plugin.");
		synchronized (lock) {
			if (watch != null) {
				watch.interrupt();
			}
			watch = null;
		}
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		return getCommandManager().onGameCommand(sender, command, label, args);
	}

	/**
	 * Checks to see if the plugin is the latest version. Thanks to vault for
	 * letting me use their code.
	 * 
	 * @param currentVersion
	 *            The version that is currently running.
	 * @return The latest version
	 */
	public String updateCheck(String currentVersion) {
		String pluginUrlString = "http://dev.bukkit.org/server-mods/monsterirc/files.rss";
		try {
			URL url = new URL(pluginUrlString);
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
			debug(e);
		}
		return currentVersion;
	}

	/**
	 * The plugins settings.
	 * 
	 * @return The settings.
	 */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * Logs a message to the console.
	 * 
	 * @param msg
	 *            The message to print.
	 */
	protected static void log(String msg) {
		logger.log(Level.INFO, "[IRC] " + msg);
	}

	/**
	 * Logs debugging messages to the console.
	 * 
	 * @param error
	 *            The message to print.
	 */
	protected static void debug(Exception error) {
		if (Variables.debug) {
			logger.log(Level.WARNING, "[IRC - Critical error detected!]");
			error.printStackTrace();
		}
	}

	/**
	 * Stops the server
	 */
	protected void stop() {
		this.getServer()
				.getPluginManager()
				.disablePlugin(
						getServer().getPluginManager().getPlugin("MonsterIRC"));
	}

	/**
	 * Logs debugging messages to the console.
	 * 
	 * @param error
	 *            The message to print.
	 */
	protected static void debug(String error) {
		if (Variables.debug) {
			logger.log(Level.WARNING, "[IRC - Debug] " + error);
		}
	}

	/**
	 * The manager that holds the handlers.
	 * 
	 * @return The handlers.
	 */
	public static HandleManager getHandleManager() {
		return handles;
	}

	/**
	 * The manager that creates the hooks with other plugins.
	 * 
	 * @return The hooks.
	 */
	public static HookManager getHookManager() {
		return hooks;
	}

	/**
	 * The CommandManager that Assigns all the commands.
	 * 
	 * @return The plugins command manager.
	 */
	public static CommandManager getCommandManager() {
		return command;
	}
}
