package org.monstercraft.irc;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.monstercraft.irc.ircplugin.event.EventManager;
import org.monstercraft.irc.plugin.Configuration;
import org.monstercraft.irc.plugin.listeners.IRCListener;
import org.monstercraft.irc.plugin.managers.CommandManager;
import org.monstercraft.irc.plugin.managers.HandleManager;
import org.monstercraft.irc.plugin.managers.HookManager;
import org.monstercraft.irc.plugin.managers.SettingsManager;
import org.monstercraft.irc.plugin.util.Methods;
import org.monstercraft.irc.plugin.util.Variables;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

/**
 * This class represents the main plugin. All actions related to the plugin are
 * forwarded by this class
 * 
 * @author Fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRC extends JavaPlugin implements Runnable {

	private static HandleManager handles = null;
	private static HookManager hooks = null;
	private static CommandManager command = null;
	private static IRCListener listener = null;

	private static IRCServer IRCserver = null;

	private static SettingsManager settings = null;
	private Object lock = new Object();
	private static EventManager em = null;

	/**
	 * Enables the plugin.
	 */
	@Override
	public void onEnable() {
		Methods.log("Starting plugin.");
		settings = new SettingsManager(this);
		em = new EventManager();
		em.start();
		hooks = new HookManager(this);
		command = new CommandManager(this);
		listener = new IRCListener(this);
		IRCserver = new IRCServer(Variables.server, Variables.port,
				Variables.name, Variables.password, Variables.ident,
				Variables.timeout, Variables.limit, Variables.connectCommands);
		handles = new HandleManager(this);
		getServer().getPluginManager().registerEvents(listener, this);
		String newVersion = Configuration.checkForUpdates(this,
				Configuration.URLS.UPDATE_URL);
		if (!newVersion.contains(Configuration.getCurrentVerison(this))) {
			Methods.log(newVersion + " is out! You are running "
					+ Configuration.getCurrentVerison(this));
			Methods.log("Update MonsterIRC at: http://dev.bukkit.org/server-mods/monsterirc");
		} else {
			Methods.log("You are using the latest version of MonsterIRC");
		}
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
	}

	/**
	 * Disables the plugin.
	 */
	@Override
	public void onDisable() {
		if (!settings.firstRun()) {
			if (getHandleManager().getIRCHandler() != null) {
				if (getHandleManager().getIRCHandler().isConnected(
						getIRCServer())) {
					for (IRCChannel c : Variables.channels) {
						IRC.getHandleManager().getIRCHandler().part(c);
					}
					getHandleManager().getIRCHandler().disconnect(
							getIRCServer());
				}
			}
		} else {
			Methods.log("Please go edit your config!");
		}
		settings.saveMuted();
		getHandleManager().getPluginHandler().stopPlugin();
		Methods.log("Successfully disabled plugin.");
	}

	/**
	 * Handles commands.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		return getCommandManager().onGameCommand(sender, command, label, args);
	}

	/**
	 * The plugins settings.
	 * 
	 * @return The settings.
	 */
	protected static SettingsManager getSettingsManager() {
		return settings;
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
	protected static HookManager getHookManager() {
		return hooks;
	}

	/**
	 * The CommandManager that Assigns all the commands.
	 * 
	 * @return The plugins command manager.
	 */
	protected static CommandManager getCommandManager() {
		return command;
	}

	/**
	 * The CommandManager that Assigns all the commands.
	 * 
	 * @return The plugins command manager.
	 */
	public static IRCServer getIRCServer() {
		return IRCserver;
	}

	/**
	 * Fetches the irc channels.
	 * 
	 * @return All of the IRCChannels
	 */
	public static Set<IRCChannel> getChannels() {
		return Variables.channels;
	}

	/**
	 * Fetches the event manager for IRC plugins.
	 * 
	 * @return The event manager for IRC plugins.
	 */
	public static EventManager getEventManager() {
		return em;
	}

	@Override
	public void run() {
		synchronized (lock) {
			try {
				if (!settings.firstRun()) {
					getHandleManager().getIRCHandler().connect(getIRCServer());
					Methods.log("Successfully started up.");
				} else {
					Methods.stop(this);
				}
			} catch (Exception e) {
				Methods.debug(e);
			}
		}
	}
}
