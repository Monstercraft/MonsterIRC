package org.monstercraft.irc;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.ircplugin.event.EventManager;
import org.monstercraft.irc.plugin.Configuration;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.managers.CommandManager;
import org.monstercraft.irc.plugin.managers.HandleManager;
import org.monstercraft.irc.plugin.managers.HookManager;
import org.monstercraft.irc.plugin.managers.SettingsManager;
import org.monstercraft.irc.plugin.managers.listeners.MonsterIRCListener;
import org.monstercraft.irc.plugin.util.Metrics;
import org.monstercraft.irc.plugin.util.Metrics.Graph;
import org.monstercraft.irc.plugin.util.log.TailLogHandler;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

/**
 * This class represents the main plugin. All actions related to the plugin are
 * forwarded by this class
 * 
 * @author Fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class MonsterIRC extends JavaPlugin implements Runnable {

	private static HandleManager handles = null;
	private static HookManager hooks = null;
	private static CommandManager command = null;
	private static MonsterIRCListener listener = null;

	private static IRCServer IRCserver = null;

	private static SettingsManager settings = null;
	private Object lock = new Object();
	private static EventManager em = null;
	private static TailLogHandler tail = null;

	/**
	 * Enables the plugin.
	 */

	public void onEnable() {
		tail = new TailLogHandler();
		Bukkit.getLogger().addHandler(tail);
		IRC.log("Starting plugin.");
		new Configuration();
		settings = new SettingsManager(this);
		em = new EventManager();
		em.start();
		hooks = new HookManager(this);
		command = new CommandManager();
		listener = new MonsterIRCListener(this);
		IRCserver = new IRCServer(Variables.server, Variables.serverPass,
				Variables.port, Variables.name, Variables.password,
				Variables.ident, Variables.timeout, Variables.tries,
				Variables.connectCommands);
		handles = new HandleManager(this);
		getServer().getPluginManager().registerEvents(listener, this);
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
	}

	/**
	 * Disables the plugin.
	 */

	public void onDisable() {
		if (!settings.firstRun()) {
			if (getHandleManager().getIRCHandler() != null) {
				if (getHandleManager().getIRCHandler().isConnected(
						getIRCServer())) {
					getHandleManager().getIRCHandler().disconnect(
							getIRCServer());
				}
			}
		} else {
			IRC.log("");
			IRC.log("A sample channel file was created for you to refrence.");
			IRC.log("Please go create a .channel file before trying to run MonsterIRC again.");
			IRC.log("");
		}
		settings.saveMuted();
		getHandleManager().getPluginHandler().stopPlugins();
		IRC.log("Successfully disabled plugin!");
	}

	/**
	 * Handles commands.
	 */

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
	public static HookManager getHookManager() {
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

	/**
	 * Fetches the handler handling messages.
	 * 
	 * @return The tailloghandler.
	 */
	public static TailLogHandler getLogHandler() {
		return tail;
	}

	/**
	 * Stops the plugin.
	 * 
	 * @param plugin
	 *            The plugin to stop.
	 */
	protected static void stop(final Plugin plugin) {
		Bukkit.getServer().getPluginManager().disablePlugin(plugin);
	}

	public void run() {
		synchronized (lock) {
			try {
				IRC.log("Setting up metrics!");
				Metrics metrics = new Metrics(this);
				Graph graph = metrics.createGraph("Lines Relayed");
				graph.addPlotter(new Metrics.Plotter("From IRC") {
					@Override
					public int getValue() {
						return Variables.linesToGame;
					}
				});
				graph.addPlotter(new Metrics.Plotter("From Game") {
					@Override
					public int getValue() {
						return Variables.linesToIrc;
					}
				});
				Graph graph2 = metrics.createGraph("Commands Executed");
				graph2.addPlotter(new Metrics.Plotter("Ingame") {
					@Override
					public int getValue() {
						return Variables.commandsGame;
					}
				});
				graph2.addPlotter(new Metrics.Plotter("In IRC") {
					@Override
					public int getValue() {
						return Variables.commandsIRC;
					}
				});
				metrics.start();
				String newVersion = Configuration.checkForUpdates(this,
						Configuration.URLS.UPDATE_URL);
				if (!newVersion.contains(Configuration.getCurrentVerison(this))) {
					IRC.log(newVersion + " is out! You are running "
							+ Configuration.getCurrentVerison(this));
					IRC.log("Update MonsterIRC at: http://dev.bukkit.org/server-mods/monsterirc");
				} else {
					IRC.log("You are using the latest version of MonsterIRC");
				}
				if (!settings.firstRun()) {
					getHandleManager().getIRCHandler().connect(getIRCServer());
					IRC.log("Successfully started up.");
				} else {
					stop(this);
				}
			} catch (Exception e) {
				IRC.debug(e);
			}
		}
	}
}
