package org.monstercraft.irc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.monstercraft.irc.command.commands.Ban;
import org.monstercraft.irc.command.commands.Connect;
import org.monstercraft.irc.command.commands.Disconnect;
import org.monstercraft.irc.command.commands.Mute;
import org.monstercraft.irc.command.commands.Nick;
import org.monstercraft.irc.command.commands.ReloadConfig;
import org.monstercraft.irc.command.commands.Say;
import org.monstercraft.irc.command.commands.Unmute;
import org.monstercraft.irc.handlers.IRCHandler;
import org.monstercraft.irc.handlers.PermissionsHandler;
import org.monstercraft.irc.hooks.HeroChatHook;
import org.monstercraft.irc.hooks.PermissionsHook;
import org.monstercraft.irc.hooks.mcMMOHook;
import org.monstercraft.irc.listeners.IRCPlayerListener;
import org.monstercraft.irc.listeners.IRCServerListener;
import org.monstercraft.irc.util.Settings;
import org.monstercraft.irc.util.Variables;

import com.nijiko.permissions.PermissionHandler;

public class IRC extends JavaPlugin {

	public Settings settings = null;
	public List<org.monstercraft.irc.command.Command> commands = null;
	public IRCPlayerListener playerListener = null;
	public PermissionHandler permissionManager = null;
	public IRCServerListener serverListener = null;
	public PermissionsHandler perms = null;
	public HeroChatHook herochat = null;
	public mcMMOHook mcmmo = null;
	public PermissionsHook permissions = null;
	public IRCHandler IRC = null;
	public Logger logger = Logger.getLogger("MineCraft");

	public void onEnable() {
		commands = new ArrayList<org.monstercraft.irc.command.Command>();
		settings = new Settings(this);
		if (Variables.version != 1.0) {
			if (Variables.version == 0.0) {
				log("***************************************************");
				log("We have detected this is your first run!");
				log("Please modify the default configuration!");
				log("***************************************************");
				this.getServer().getPluginManager().disablePlugin(this);
				return;
			}
			log("***************************************************");
			log("Your settings are outdated!" + Variables.version
					+ ", Should be:" + 1.0);
			log("Please delete the config file!");
			log("Allowing the plugin to generate a new Updated file!");
			log("***************************************************");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		registerHooks();
		registerHandles();
		registerEvents();
		registerCommands();
		if (Variables.autoJoin) {
			IRC.connect();
		}
		log("Successfully started up.");

	}

	public void onDisable() {
		if (IRC != null) {
			if (IRC.isConnected()) {
				IRC.disconnect();
			}
		}
		log("Successfully disabled plugin.");
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args.length > 0) {
			String[] split = new String[args.length + 1];
			split[0] = label;
			for (int a = 0; a < args.length; a++) {
				split[a + 1] = args[a];
			}
			for (org.monstercraft.irc.command.Command c : commands) {
				if (c.canExecute(sender, split)) {
					c.execute(sender, split);
					return true;
				}
			}
			return false;
		}
		return false;
	}

	private void registerCommands() {
		commands.add(new Ban(this));
		commands.add(new Mute(this));
		commands.add(new Unmute(this));
		commands.add(new Connect(this));
		commands.add(new Disconnect(this));
		commands.add(new Nick(this));
		commands.add(new Say(this));
		commands.add(new ReloadConfig(this));
	}

	private void registerEvents() {
		playerListener = new IRCPlayerListener(this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_CHAT,
				playerListener, Priority.Highest, this);
		serverListener = new IRCServerListener(this);
		getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE,
				serverListener, Priority.Normal, this);
	}

	private void registerHooks() {
		herochat = new HeroChatHook(this);
		mcmmo = new mcMMOHook(this);
		permissions = new PermissionsHook(this);
	}

	private void registerHandles() {
		IRC = new IRCHandler(this);
	}

	public void log(String msg) {
		logger.log(Level.INFO, "[IRC] " + msg);
	}
}
