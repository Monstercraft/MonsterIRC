package org.monstercraft.irc;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.monstercraft.irc.listeners.IRCListener;
import org.monstercraft.irc.managers.CommandManager;
import org.monstercraft.irc.managers.HandleManager;
import org.monstercraft.irc.managers.HookManager;
import org.monstercraft.irc.util.Settings;
import org.monstercraft.irc.util.Variables;

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

	public void onEnable() {
		try {
			log("Starting plugin.");
			settings = new Settings(this);
			if (!settings.firstRun()) {
				hooks = new HookManager(this);
				handles = new HandleManager(this);
				command = new CommandManager(this);
				listener = new IRCListener(this);
				getServer().getPluginManager().registerEvents(listener, this);
				getHandleManager().getIRCHandler().connect(Variables.server,
						Variables.port, Variables.login, Variables.name,
						Variables.password, Variables.ident);
				log("Successfully started up.");
			} else {
				getServer().getPluginManager().disablePlugin(
						getServer().getPluginManager().getPlugin("MonsterIRC"));
			}
		} catch (Exception e) {
			debug(e.toString());
		}

	}

	public void onDisable() {
		if (!settings.firstRun()) {
			if (getHandleManager().getIRCHandler() != null) {
				if (getHandleManager().getIRCHandler().isConnected()) {
					getHandleManager().getIRCHandler().disconnect();
				}
			}
		} else {
			log("Please go check the config or look for any errors that may have occured!");
		}
		log("Successfully disabled plugin.");
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		return getCommandManager().onGameCommand(sender, command, label, args);
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
