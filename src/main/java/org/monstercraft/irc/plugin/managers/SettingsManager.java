package org.monstercraft.irc.plugin.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.util.ChatType;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

/**
 * This class contains all of the plugins settings.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class SettingsManager extends MonsterIRC {
	private boolean firstRun = false;
	private MonsterIRC plugin = null;

	/**
	 * Creates an instance of the Settings class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public SettingsManager(final MonsterIRC plugin) {
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

	/**
	 * Saves the config file.
	 * 
	 * @param config
	 *            The config to save.
	 * @param file
	 *            The file to save it to.
	 */
	private void save(final FileConfiguration config, final File file) {
		try {
			config.save(file);
		} catch (IOException e) {
			IRC.debug(e);
		}
	}

	/**
	 * Saves the muted users.
	 */
	public void saveMuted() {
		final FileConfiguration config = plugin.getConfig();
		final File CONFIGURATION_FILE = new File(
				Configuration.Paths.SETTINGS_FILE);
		boolean exists = CONFIGURATION_FILE.exists();
		if (exists) {
			try {
				config.load(CONFIGURATION_FILE);
			} catch (Exception e) {
				IRC.debug(e);
			}
			config.set("IRC.MUTED", Variables.muted);
		} else {
			IRC.debug("No file found, can not save muted users!",
					Variables.debug);
			return;
		}
		try {
			config.save(CONFIGURATION_FILE);
		} catch (IOException e) {
			IRC.debug(e);
		}
	}

	/**
	 * This method loads the plugins configuration file.
	 */
	public void load() {
		final FileConfiguration config = this.plugin.getConfig();
		final File CONFIGURATION_FILE = new File(
				Configuration.Paths.SETTINGS_FILE);
		boolean exists = CONFIGURATION_FILE.exists();
		IRC.log("Loading settings.yml file");
		if (exists) {
			try {
				IRC.log("Loading settings!");
				config.options()
						.header("MonsterIRC's configs - Refer to \"http://dev.bukkit.org/server-mods/monsterirc/pages/settings/\" for help \nDo not remove the ' ' around the strings!");
				config.options().copyDefaults(true);
				config.load(CONFIGURATION_FILE);
			} catch (Exception e) {
				IRC.debug(e);
			}
		} else {
			IRC.log("Loading default settings!");
			config.options()
					.header("MonsterIRC's configs - Refer to \"http://dev.bukkit.org/server-mods/monsterirc/pages/settings/\" for help \nDo not remove the ' ' around the strings!");
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
			Variables.serverPass = config.getString(
					"IRC.SETTINGS.SERVER_PASSWORD", Variables.serverPass);
			Variables.port = config.getInt("IRC.SETTINGS.PORT", Variables.port);
			Variables.limit = config.getInt(
					"IRC.OPTIONS.MESSAGE_LIMIT_PER_SEC", Variables.limit);
			Variables.timeout = config.getInt("IRC.OPTIONS.TIMEOUT",
					Variables.timeout);
			Variables.tries = config.getInt("IRC.OPTIONS.RETRYS",
					Variables.tries);
			Variables.debug = config.getBoolean("IRC.OPTIONS.DEBUG",
					Variables.debug);
			Variables.passOnName = config.getBoolean(
					"IRC.OPTIONS.PASS_ON_NAME", Variables.passOnName);
			Variables.hideMode = config.getBoolean("IRC.OPTIONS.HIDE_MODE",
					Variables.hideMode);
			Variables.colors = config.getBoolean("IRC.OPTIONS.ALLOW_COLORS",
					Variables.colors);
			Variables.ingamecommands = config.getBoolean(
					"IRC.ADMIN.INGAME_COMMANDS", Variables.ingamecommands);
			Variables.commandPrefix = config
					.getString("IRC.ADMIN.INGAME_COMMANDS_PREFIX",
							Variables.commandPrefix);
			Variables.passSay = config.getBoolean("IRC.ADMIN.PASS_SAY_COMMAND",
					Variables.passSay);
			Variables.mcformat = config.getString("IRC.FORMAT.MINECRAFT",
					Variables.mcformat);
			Variables.ircformat = config.getString("IRC.FORMAT.IRC",
					Variables.ircformat);
			Variables.connectCommands = config
					.getStringList("IRC.ON_CONNECT_COMMANDS");
			Variables.muted = config.getStringList("IRC.MUTED");
			save(config, CONFIGURATION_FILE);
		} catch (Exception e) {
			IRC.debug(e);
		}
		String defaultFormat = "<{groupPrefix}{prefix}{name}{suffix}{groupSuffix}>: {message}";
		if (Variables.mcformat.contains("{name}")
				&& Variables.mcformat.contains("{message}")) {
		} else {
			IRC.debug("Invalid Minecraft format detected!", Variables.debug);
			Variables.mcformat = defaultFormat;
		}
		if (Variables.ircformat.contains("{name}")
				&& Variables.ircformat.contains("{message}")) {
		} else {
			IRC.debug("Invalid IRC format detected!", Variables.debug);
			Variables.ircformat = defaultFormat;
		}
	}

	/**
	 * This method loads all of the channels.
	 */
	public void populateChannels() {
		Variables.channels.clear();
		final File CHANNEL_DIR = new File(Configuration.Paths.CHANNELS);
		Set<File> files = new HashSet<File>();
		if (CHANNEL_DIR.listFiles() != null) {
			if (CHANNEL_DIR.listFiles().length != 0) {
				for (File f : CHANNEL_DIR.listFiles()) {
					if (f.getName().toLowerCase()
							.contains("#Sample".toLowerCase())) {
						continue;
					}
					if (f.getName().endsWith(".channel")) {
						files.add(f);
					}
				}
			} else {
				createDefaultChannel();
			}
		} else {
			createDefaultChannel();
		}
		if (files.isEmpty()) {
			IRC.log("***************************************");
			IRC.log("***************************************");
			IRC.log("***************************************");
			IRC.log("No channel files have been found!");
			IRC.log("Refer to the channel page on bukkit dev for help!");
			IRC.log("http://dev.bukkit.org/server-mods/monsterirc/pages/channel-setup/");
			IRC.log("***************************************");
			IRC.log("***************************************");
			IRC.log("***************************************");
			createDefaultChannel();
			return;
		}
		FileConfiguration config = new YamlConfiguration();
		for (File f : files) {
			try {
				FileConfiguration defaultConfig = getSampleChannel();
				config.setDefaults(defaultConfig.getDefaults());
				config.options().copyDefaults(true);
				config.load(f);
				save(config, f);
			} catch (Exception e) {
				IRC.debug(e);
			}
			try {
				Map<String, Boolean> bools = new HashMap<String, Boolean>();
				boolean hero = config.getBoolean(
						"CHANNEL.CHATTYPE.HEROCHAT.ENABLED", false);
				boolean admin = config.getBoolean(
						"CHANNEL.CHATTYPE.MCMMO.ADMINCHAT.ENABLED", false);
				boolean global = config.getBoolean(
						"CHANNEL.CHATTYPE.GLOBAL.ENABLED", false);
				boolean towny = config.getBoolean(
						"CHANNEL.CHATTYPE.TOWNY.ENABLED", false);
				bools.put("Global", global);
				bools.put("Hero", hero);
				bools.put("Admin", admin);
				bools.put("Towny", towny);
				int count = 0;
				for (String b : bools.keySet()) {
					if (bools.get(b)) {
						count++;
					}
				}
				if (count == 1) {
					IRC.log("Channel " + f.getName()
							+ " has been successfully enabled!");
				} else {
					if (count == 0) {
						IRC.debug("Passing channel " + f.getName()
								+ " because no chat types were enabled!",
								Variables.debug);
						continue;
					} else {
						IRC.debug(
								"Invalid channel file detected! You have "
										+ count + " chat types enabled on "
										+ f.getName() + "!", Variables.debug);
						continue;
					}
				}
				if (global) {
					Variables.channels
							.add(new IRCChannel(
									config.getString("CHANNEL.SETTINGS.PASSWORD"),
									config.getBoolean("CHANNEL.SETTINGS.SHOW_JOIN_AND_LEAVE_MESSAGES"),
									config.getBoolean("CHANNEL.SETTINGS.AUTOJOIN"),
									config.getBoolean("CHANNEL.SETTINGS.DEFAULT"),
									f.getName().substring(0,
											f.getName().lastIndexOf(".")),
									ChatType.GLOBAL,
									config.getStringList("CHANNEL.COMMANDS.OP"),
									config.getStringList("CHANNEL.COMMANDS.HOP"),
									config.getStringList("CHANNEL.COMMANDS.ADMINS"),
									config.getStringList("CHANNEL.COMMANDS.VOICE"),
									config.getStringList("CHANNEL.COMMANDS.USERS"),
									config.getBoolean("CHANNEL.SETTINGS.PASS_TO_GAME"),
									config.getBoolean("CHANNEL.SETTINGS.PASS_TO_IRC")));
				} else if (admin) {
					Variables.channels
							.add(new IRCChannel(
									config.getString("CHANNEL.SETTINGS.PASSWORD"),
									config.getBoolean("CHANNEL.SETTINGS.SHOW_JOIN_AND_LEAVE_MESSAGES"),
									config.getBoolean("CHANNEL.SETTINGS.AUTOJOIN"),
									config.getBoolean("CHANNEL.SETTINGS.DEFAULT"),
									f.getName().substring(0,
											f.getName().lastIndexOf(".")),
									ChatType.ADMINCHAT,
									config.getStringList("CHANNEL.COMMANDS.OP"),
									config.getStringList("CHANNEL.COMMANDS.HOP"),
									config.getStringList("CHANNEL.COMMANDS.ADMINS"),
									config.getStringList("CHANNEL.COMMANDS.VOICE"),
									config.getStringList("CHANNEL.COMMANDS.USERS"),
									config.getBoolean("CHANNEL.SETTINGS.PASS_TO_GAME"),
									config.getBoolean("CHANNEL.SETTINGS.PASS_TO_IRC")));
				} else if (hero) {
					Variables.channels
							.add(new IRCChannel(
									config.getString("CHANNEL.SETTINGS.PASSWORD"),
									config.getBoolean("CHANNEL.SETTINGS.SHOW_JOIN_AND_LEAVE_MESSAGES"),
									config.getBoolean("CHANNEL.SETTINGS.AUTOJOIN"),
									config.getBoolean("CHANNEL.SETTINGS.DEFAULT"),
									f.getName().substring(0,
											f.getName().lastIndexOf(".")),
									config.getString("CHANNEL.CHATTYPE.HEROCHAT.CHANNEL"),
									config.getStringList("CHANNEL.CHATTYPE.HEROCHAT.LISTEN"),
									ChatType.HEROCHAT,
									config.getStringList("CHANNEL.COMMANDS.OP"),
									config.getStringList("CHANNEL.COMMANDS.HOP"),
									config.getStringList("CHANNEL.COMMANDS.ADMIN"),
									config.getStringList("CHANNEL.COMMANDS.VOICE"),
									config.getStringList("CHANNEL.COMMANDS.USERS"),
									config.getBoolean("CHANNEL.SETTINGS.PASS_TO_GAME"),
									config.getBoolean("CHANNEL.SETTINGS.PASS_TO_IRC")));
				} else if (towny) {
					Variables.channels
							.add(new IRCChannel(
									config.getString("CHANNEL.SETTINGS.PASSWORD"),
									config.getBoolean("CHANNEL.SETTINGS.SHOW_JOIN_AND_LEAVE_MESSAGES"),
									config.getBoolean("CHANNEL.SETTINGS.AUTOJOIN"),
									config.getBoolean("CHANNEL.SETTINGS.DEFAULT"),
									f.getName().substring(0,
											f.getName().lastIndexOf(".")),
									config.getString("CHANNEL.CHATTYPE.TOWNY.CHANNEL"),
									ChatType.TOWNYCHAT,
									config.getStringList("CHANNEL.COMMANDS.OP"),
									config.getStringList("CHANNEL.COMMANDS.HOP"),
									config.getStringList("CHANNEL.COMMANDS.ADMINS"),
									config.getStringList("CHANNEL.COMMANDS.VOICE"),
									config.getStringList("CHANNEL.COMMANDS.USERS"),
									config.getBoolean("CHANNEL.SETTINGS.PASS_TO_GAME"),
									config.getBoolean("CHANNEL.SETTINGS.PASS_TO_IRC")));
				}
			} catch (Exception e) {
				IRC.debug(e);
			}
		}
	}

	private FileConfiguration getSampleChannel() {
		ArrayList<String> op = new ArrayList<String>();
		ArrayList<String> hop = new ArrayList<String>();
		ArrayList<String> admin = new ArrayList<String>();
		ArrayList<String> voice = new ArrayList<String>();
		ArrayList<String> user = new ArrayList<String>();
		ArrayList<String> channels = new ArrayList<String>();
		channels.add("Global");
		channels.add("Overworld");
		op.add("*");
		hop.add("*");
		admin.add("list");
		voice.add("give");
		user.add("help");
		FileConfiguration config = new YamlConfiguration();
		config.options()
				.header("MonsterIRC's configs - Refer to \"http://dev.bukkit.org/server-mods/monsterirc/pages/channel-setup/\" for help");
		config.addDefault("CHANNEL.SETTINGS.AUTOJOIN", true);
		config.addDefault("CHANNEL.SETTINGS.DEFAULT", true);
		config.addDefault("CHANNEL.SETTINGS.PASSWORD", "");
		config.addDefault("CHANNEL.SETTINGS.PASS_TO_GAME", true);
		config.addDefault("CHANNEL.SETTINGS.PASS_TO_IRC", true);
		config.addDefault("CHANNEL.SETTINGS.PASSWORD", "");
		config.addDefault("CHANNEL.SETTINGS.SHOW_JOIN_AND_LEAVE_MESSAGES", true);
		config.addDefault("CHANNEL.CHATTYPE.GLOBAL.ENABLED", false);
		config.addDefault("CHANNEL.CHATTYPE.MCMMO.ADMINCHAT.ENABLED", false);
		config.addDefault("CHANNEL.CHATTYPE.HEROCHAT.ENABLED", false);
		config.addDefault("CHANNEL.CHATTYPE.HEROCHAT.CHANNEL", "IRC");
		config.addDefault("CHANNEL.CHATTYPE.HEROCHAT.LISTEN", channels);
		config.addDefault("CHANNEL.CHATTYPE.TOWNY.ENABLED", false);
		config.addDefault("CHANNEL.CHATTYPE.TOWNY.CHANNEL", "IRC");
		config.addDefault("CHANNEL.COMMANDS.OP", op);
		config.addDefault("CHANNEL.COMMANDS.HOP", hop);
		config.addDefault("CHANNEL.COMMANDS.ADMIN", admin);
		config.addDefault("CHANNEL.COMMANDS.VOICE", voice);
		config.addDefault("CHANNEL.COMMANDS.USERS", user);
		return config;
	}

	/**
	 * This methods creates the default sample channel files for the plugin.
	 */
	public void createDefaultChannel() {
		File SAMPLE_CHANNEL = new File(Configuration.Paths.CHANNELS
				+ File.separator + "#Sample.channel");
		FileConfiguration config = getSampleChannel();
		config.options().copyDefaults(true);
		save(config, SAMPLE_CHANNEL);
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
