package org.monstercraft.irc;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.monstercraft.irc.listeners.IRCPlayerListener;
import org.monstercraft.irc.listeners.IRCServerListener;
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

	private IRCPlayerListener playerListener = null;
	private IRCServerListener serverListener = null;

	private HandleManager handles = null;
	private HookManager hooks = null;
	private CommandManager command = null;

	private Logger logger = Logger.getLogger("MineCraft");

	private Settings settings = null;

	public void onEnable() {
		log("Starting plugin.");
		settings = new Settings(this);
		if (Variables.version != 1.0) {
			if (Variables.version == 0.0) {
				log("***************************************************");
				log("We have detected this is your first run!");
				log("Please modify the default configuration!");
				log("***************************************************");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
			log("***************************************************");
			log("Your settings are outdated!" + Variables.version
					+ ", Should be:" + 1.0);
			log("Please delete the config file!");
			log("Allowing the plugin to generate a new Updated  file!");
			log("***************************************************");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		hooks = new HookManager(this);
		handles = new HandleManager(this);
		command = new CommandManager(this);
		playerListener = new IRCPlayerListener(this);
		serverListener = new IRCServerListener(this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_CHAT,
				playerListener, Priority.Highest, this);
		getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE,
				serverListener, Priority.Monitor, this);
		if (Variables.autoJoin) {
			getHandleManager().getIRCHandler().connect(Variables.channel,
					Variables.server, Variables.port, Variables.login,
					Variables.name, Variables.password, Variables.ident);
		}
		log("Successfully started up.");

	}

	public void onDisable() {
		if (getHandleManager().getIRCHandler() != null) {
			if (getHandleManager().getIRCHandler().isConnected()) {
				getHandleManager().getIRCHandler()
						.disconnect(Variables.channel);
			}
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
	protected void log(String msg) {
		logger.log(Level.INFO, "[IRC] " + msg);
	}

	/**
	 * The manager that holds the handlers.
	 * 
	 * @return The handlers.
	 */
	public HandleManager getHandleManager() {
		return handles;
	}

	/**
	 * The manager that creates the hooks with other plugins.
	 * 
	 * @return The hooks.
	 */
	public HookManager getHookManager() {
		return hooks;
	}

	/**
	 * The CommandManager that Assigns all the commands.
	 * 
	 * @return The plugins command manager.
	 */
	public CommandManager getCommandManager() {
		return command;
	}
}
