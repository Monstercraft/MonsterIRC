package org.monstercraft.irc.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.util.ChatType;
import org.monstercraft.irc.util.Constants;
import org.monstercraft.irc.util.CreateReadme;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;

/**
 * This class contains all of the plugins settings.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class SettingsManager extends IRC {
	private boolean firstRun = false;
	private IRC plugin = null;

	/**
	 * Creates an instance of the Settings class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public SettingsManager(IRC plugin) {
		this.plugin = plugin;
		load();
		populateChannels();
	}

	/**
	 * Reload all of the configuration files.
	 */
	public void reload() {
		load();
		populateChannels();
	}

	private void save(final FileConfiguration config, final File file) {
		try {
			config.save(file);
		} catch (IOException e) {
			debug(e);
		}
	}

	public void saveMuted() {
		final FileConfiguration config = plugin.getConfig();
		final File CONFIGURATION_FILE = new File(plugin.getDataFolder()
				+ File.separator + Constants.SETTINGS_FILE);
		boolean exists = CONFIGURATION_FILE.exists();
		if (exists) {
			try {
				config.load(CONFIGURATION_FILE);
			} catch (Exception e) {
				debug(e);
			}
			config.set("IRC.MUTED", Variables.muted);
		} else {
			debug("No file found, can not save muted users!");
			return;
		}
		try {
			config.save(CONFIGURATION_FILE);
		} catch (IOException e) {
			debug(e);
		}
	}

	/**
	 * This method loads the plugins configuration file.
	 */
	public void load() {
		Variables.format = "<{name}>{colon} {message}";
		final FileConfiguration config = plugin.getConfig();
		final File CONFIGURATION_FILE = new File(plugin.getDataFolder()
				+ File.separator + Constants.SETTINGS_FILE);
		boolean exists = CONFIGURATION_FILE.exists();
		// List<String> sample = new ArrayList<String>();
		// sample.add("user sample 1");
		// sample.add("user sample 2");
		// config.addDefault("IRC.SETTINGS.IDENTIFY", Variables.ident);
		// config.addDefault("IRC.SETTINGS.NICKNAME", Variables.name);
		// config.addDefault("IRC.SETTINGS.PASSWORD", Variables.password);
		// config.addDefault("IRC.SETTINGS.SERVER", Variables.server);
		// config.addDefault("IRC.SETTINGS.PORT", Variables.port);
		// config.addDefault("IRC.OPTIONS.TIMEOUT", Variables.timeout);
		// config.addDefault("IRC.OPTIONS.DEBUG", Variables.debug);
		// config.addDefault("IRC.OPTIONS.PASS_ON_NAME", Variables.passOnName);
		// config.addDefault("IRC.OPTIONS.ALLOW_COLORS", Variables.colors);
		// config.addDefault("IRC.OPTIONS.SHOW_JOIN_AND_LEAVE_MESSAGES",
		// Variables.joinAndQuit);
		// config.addDefault("IRC.ADMIN.INGAME_COMMANDS",
		// Variables.ingamecommands);
		// config.addDefault("IRC.ADMIN.INGAME_COMMANDS_PREFIX",
		// Variables.commandPrefix);
		// config.addDefault("IRC.MINECRAFT.FORMAT", Variables.format);
		// config.addDefault("IRC.MUTED", sample);
		if (exists) {
			try {
				config.options().copyDefaults(true);
				config.load(CONFIGURATION_FILE);
			} catch (Exception e) {
				debug(e);
			}
		} else {
			config.options().copyDefaults(true);
		}
		try {
			Variables.ident = config.getBoolean("IRC.SETTINGS.IDENTIFY",
					Variables.ident);
			Variables.name = config.getString("IRC.SETTINGS.NICKNAME",
					Variables.name);
			Variables.password = config.getString("IRC.SETTINGS.PASSWORD",
					Variables.password);
			Variables.server = config.getString("IRC.SETTINGS.SERVER",
					Variables.server);
			Variables.port = config.getInt("IRC.SETTINGS.PORT", Variables.port);
			Variables.timeout = config.getInt("IRC.OPTIONS.TIMEOUT",
					Variables.timeout);
			Variables.tries = config.getInt("IRC.OPTIONS.RETRYS",
					Variables.tries);
			Variables.debug = config.getBoolean("IRC.OPTIONS.DEBUG",
					Variables.debug);
			Variables.passOnName = config.getBoolean(
					"IRC.OPTIONS.PASS_ON_NAME", Variables.passOnName);
			Variables.colors = config.getBoolean("IRC.OPTIONS.ALLOW_COLORS",
					Variables.colors);
			Variables.joinAndQuit = config.getBoolean(
					"IRC.OPTIONS.SHOW_JOIN_AND_LEAVE_MESSAGES",
					Variables.joinAndQuit);
			Variables.ingamecommands = config.getBoolean(
					"IRC.ADMIN.INGAME_COMMANDS", Variables.ingamecommands);
			Variables.commandPrefix = config
					.getString("IRC.ADMIN.INGAME_COMMANDS_PREFIX",
							Variables.commandPrefix);
			Variables.format = config.getString("IRC.MINECRAFT.FORMAT",
					Variables.format);
			Variables.muted = config.getStringList("IRC.MUTED");
			save(config, CONFIGURATION_FILE);
		} catch (Exception e) {
			debug(e);
		}
		if (Variables.name.contains("default")) {
			firstRun = true;
		}
	}

	/**
	 * This method loads all of the channels.
	 */
	public void populateChannels() {
		Variables.channels.clear();
		final File CHANNEL_DIR = new File(plugin.getDataFolder()
				+ File.separator + Constants.CHANNELS_PATH);
		Set<File> files = new HashSet<File>();
		if (CHANNEL_DIR.listFiles() != null) {
			for (File f : CHANNEL_DIR.listFiles()) {
				if (f.getName().endsWith(".channel")) {
					files.add(f);
				}
			}
		} else {
			createDefaultChannel();
		}
		FileConfiguration config = new YamlConfiguration();
		for (File f : files) {
			try {
				config.load(f);
			} catch (Exception e1) {
				debug(e1);
			}
			try {
				Map<String, Boolean> bools = new HashMap<String, Boolean>();
				boolean hero = config.getBoolean(
						"CHANNEL.CHATTYPE.HEROCHAT.ENABLED", false);
				boolean admin = config.getBoolean(
						"CHANNEL.CHATTYPE.MCMMO.ADMINCHAT.ENABLED", false);
				boolean global = config.getBoolean(
						"CHANNEL.CHATTYPE.GLOBAL.ENABLED", false);
				bools.put("Global", global);
				bools.put("Hero", hero);
				bools.put("Admin", admin);
				int count = 0;
				for (String b : bools.keySet()) {
					if (bools.get(b)) {
						count++;
					}
				}
				if (count == 1) {
					log("Channel " + f.getName()
							+ " has been successfully enabled!");
				} else {
					if (count == 0) {
						debug("Passing " + f.getName()
								+ " because no chat types were enabled!");
					} else {
						debug("Invalid channel file detected! You have "
								+ count + " chat types enabled on "
								+ f.getName() + "!");
					}
					continue;
				}
				if (global) {
					Variables.channels.add(new IRCChannel(config
							.getBoolean("CHANNEL.SETTINGS.AUTOJOIN"), config
							.getBoolean("CHANNEL.SETTINGS.DEFAULT"), "#"
							+ f.getName().substring(0,
									f.getName().lastIndexOf(".")),
							ChatType.GLOBAL, config
									.getStringList("CHANNEL.COMMANDS.OP"),
							config.getStringList("CHANNEL.COMMANDS.VOICE"),
							config.getStringList("CHANNEL.COMMANDS.USERS")));
				} else if (admin) {
					Variables.channels.add(new IRCChannel(config
							.getBoolean("CHANNEL.SETTINGS.AUTOJOIN"), config
							.getBoolean("CHANNEL.SETTINGS.DEFAULT"), "#"
							+ f.getName().substring(0,
									f.getName().lastIndexOf(".")),
							ChatType.ADMINCHAT, config
									.getStringList("CHANNEL.COMMANDS.OP"),
							config.getStringList("CHANNEL.COMMANDS.VOICE"),
							config.getStringList("CHANNEL.COMMANDS.USERS")));
				} else if (hero) {
					Variables.channels.add(new IRCChannel(config
							.getBoolean("CHANNEL.SETTINGS.AUTOJOIN"), config
							.getBoolean("CHANNEL.SETTINGS.DEFAULT"), "#"
							+ f.getName().substring(0,
									f.getName().lastIndexOf(".")), config
							.getString("CHANNEL.CHATTYPE.HEROCHAT.CHANNEL"),
							ChatType.HEROCHAT, config
									.getStringList("CHANNEL.COMMANDS.OP"),
							config.getStringList("CHANNEL.COMMANDS.VOICE"),
							config.getStringList("CHANNEL.COMMANDS.USERS")));
				}
			} catch (Exception e) {
				debug(e);
			}
		}
	}

	/**
	 * This methods creates the default sample channel files for the plugin.
	 */
	public void createDefaultChannel() {
		File SAMPLE_CHANNEL = new File(plugin.getDataFolder() + File.separator
				+ Constants.CHANNELS_PATH + File.separator + "Sample.channel");
		FileConfiguration config = new YamlConfiguration();
		config.set("CHANNEL.SETTINGS.AUTOJOIN", false);
		config.set("CHANNEL.SETTINGS.DEFAULT", false);
		config.set("CHANNEL.CHATTYPE.GLOBAL.ENABLED", false);
		config.set("CHANNEL.CHATTYPE.MCMMO.ADMINCHAT.ENABLED", false);
		config.set("CHANNEL.CHATTYPE.HEROCHAT.ENABLED", false);
		config.set("CHANNEL.CHATTYPE.HEROCHAT.CHANNEL", "IRC");
		config.set("CHANNEL.COMMANDS.OP", new ArrayList<String>());
		config.set("CHANNEL.COMMANDS.VOICE", new ArrayList<String>());
		config.set("CHANNEL.COMMANDS.USERS", new ArrayList<String>());
		save(config, SAMPLE_CHANNEL);
		new CreateReadme();
		log("*************************************************");
		log("*************************************************");
		log("*************************************************");
		log("*************************************************");
		log("*************************************************");
		log("*************************************************");
		log("We have set up the default channels for you!");
		log("Check the Readme.txt file in plugins/MonsterIRC");
		log("for a detailed guide to set up this plugin!");
		log("*************************************************");
		log("*************************************************");
		log("*************************************************");
		log("*************************************************");
		log("*************************************************");
		log("*************************************************");
		firstRun = true;
	}

	/**
	 * Check if this is the first time the plugin ran.
	 * 
	 * @return True if this is the first run of the plugin; otherwise false.
	 */
	public boolean firstRun() {
		return firstRun;
	}
}
