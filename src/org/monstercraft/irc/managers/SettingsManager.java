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
		final File CONFIGURATION_FILE = new File(Constants.SETTINGS_PATH
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
		final FileConfiguration config = this.plugin.getConfig();
		final File CONFIGURATION_FILE = new File(Constants.SETTINGS_PATH
				+ File.separator + Constants.SETTINGS_FILE);
		boolean exists = CONFIGURATION_FILE.exists();
		log("Loading settings.yml file");
		if (exists) {
			try {
				log("Loading settings!");
				config.options()
						.header("MonsterIRC's configs - Refer to \"http://dev.bukkit.org/server-mods/monsterirc/pages/settings/\" for help \n#Do not remove the ' ' around the strings!");
				config.options().copyDefaults(true);
				config.load(CONFIGURATION_FILE);
			} catch (Exception e) {
				debug(e);
			}
		} else {
			log("Loading default settings!");
			config.options()
					.header("MonsterIRC's configs - Refer to \"http://dev.bukkit.org/server-mods/monsterirc/pages/settings/\" for help \n#Do not remove the ' ' around the strings!");
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
			Variables.colors = config.getBoolean("IRC.OPTIONS.ALLOW_COLORS",
					Variables.colors);
			Variables.ingamecommands = config.getBoolean(
					"IRC.ADMIN.INGAME_COMMANDS", Variables.ingamecommands);
			Variables.commandPrefix = config
					.getString("IRC.ADMIN.INGAME_COMMANDS_PREFIX",
							Variables.commandPrefix);
			Variables.mcformat = config.getString("IRC.FORMAT.MINECRAFT",
					Variables.mcformat);
			Variables.ircformat = config.getString("IRC.FORMAT.IRC",
					Variables.ircformat);
			Variables.connectCommands = config.getStringList("IRC.ON_CONNECT_COMMANDS");
			Variables.muted = config.getStringList("IRC.MUTED");
			save(config, CONFIGURATION_FILE);
		} catch (Exception e) {
			debug(e);
		}
		String defaultFormat = "<{groupPrefix}{prefix}{name}{suffix}{groupSuffix}>{colon} {message}";
		if (Variables.mcformat.contains("{name}")
				&& Variables.mcformat.contains("{message}")) {
		} else {
			debug("Invalid Minecraft format detected!");
			Variables.mcformat = defaultFormat;
		}
		if (Variables.ircformat.contains("{name}")
				&& Variables.ircformat.contains("{message}")) {
		} else {
			debug("Invalid IRC format detected!");
			Variables.ircformat = defaultFormat;
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
		final File CHANNEL_DIR = new File(Constants.SETTINGS_PATH
				+ File.separator + Constants.CHANNELS_PATH);
		Set<File> files = new HashSet<File>();
		if (CHANNEL_DIR.listFiles() != null) {
			if (CHANNEL_DIR.listFiles().length != 0) {
				for (File f : CHANNEL_DIR.listFiles()) {
					if (f.getName().toLowerCase()
							.contains("SSample".toLowerCase())) {
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
					Variables.channels
							.add(new IRCChannel(
									IRC.getIRCServer(),
									config.getString("CHANNEL.SETTINGS.PASSWORD"),
									config.getBoolean("CHANNEL.SETTINGS.SHOW_JOIN_AND_LEAVE_MESSAGES"),
									config.getBoolean("CHANNEL.SETTINGS.AUTOJOIN"),
									config.getBoolean("CHANNEL.SETTINGS.DEFAULT"),
									f.getName().substring(0,
											f.getName().lastIndexOf(".")),
									ChatType.GLOBAL,
									config.getStringList("CHANNEL.COMMANDS.OP"),
									config.getStringList("CHANNEL.COMMANDS.VOICE"),
									config.getStringList("CHANNEL.COMMANDS.USERS")));
				} else if (admin) {
					Variables.channels
							.add(new IRCChannel(
									IRC.getIRCServer(),
									config.getString("CHANNEL.SETTINGS.PASSWORD"),
									config.getBoolean("CHANNEL.SETTINGS.SHOW_JOIN_AND_LEAVE_MESSAGES"),
									config.getBoolean("CHANNEL.SETTINGS.AUTOJOIN"),
									config.getBoolean("CHANNEL.SETTINGS.DEFAULT"),
									f.getName().substring(0,
											f.getName().lastIndexOf(".")),
									ChatType.ADMINCHAT,
									config.getStringList("CHANNEL.COMMANDS.OP"),
									config.getStringList("CHANNEL.COMMANDS.VOICE"),
									config.getStringList("CHANNEL.COMMANDS.USERS")));
				} else if (hero) {
					Variables.channels
							.add(new IRCChannel(
									IRC.getIRCServer(),
									config.getString("CHANNEL.SETTINGS.PASSWORD"),
									config.getBoolean("CHANNEL.SETTINGS.SHOW_JOIN_AND_LEAVE_MESSAGES"),
									config.getBoolean("CHANNEL.SETTINGS.AUTOJOIN"),
									config.getBoolean("CHANNEL.SETTINGS.DEFAULT"),
									f.getName().substring(0,
											f.getName().lastIndexOf(".")),
									config.getString("CHANNEL.CHATTYPE.HEROCHAT.CHANNEL"),
									ChatType.HEROCHAT,
									config.getStringList("CHANNEL.COMMANDS.OP"),
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
		File SAMPLE_CHANNEL = new File(Constants.SETTINGS_PATH + File.separator
				+ Constants.CHANNELS_PATH + File.separator + "#Sample.channel");
		ArrayList<String> op = new ArrayList<String>();
		ArrayList<String> voice = new ArrayList<String>();
		ArrayList<String> user = new ArrayList<String>();
		op.add("*");
		voice.add("give");
		user.add("help");
		FileConfiguration config = new YamlConfiguration();
		config.options()
				.header("MonsterIRC's configs - Refer to \"http://dev.bukkit.org/server-mods/monsterirc/pages/channel-setup/\" for help");
		config.set("CHANNEL.SETTINGS.AUTOJOIN", false);
		config.set("CHANNEL.SETTINGS.DEFAULT", false);
		config.set("CHANNEL.SETTINGS.PASSWORD", "");
		config.set("CHANNEL.SETTINGS.SHOW_JOIN_AND_LEAVE_MESSAGES", true);
		config.set("CHANNEL.CHATTYPE.GLOBAL.ENABLED", false);
		config.set("CHANNEL.CHATTYPE.MCMMO.ADMINCHAT.ENABLED", false);
		config.set("CHANNEL.CHATTYPE.HEROCHAT.ENABLED", false);
		config.set("CHANNEL.CHATTYPE.HEROCHAT.CHANNEL", "IRC");
		config.set("CHANNEL.COMMANDS.OP", op);
		config.set("CHANNEL.COMMANDS.VOICE", voice);
		config.set("CHANNEL.COMMANDS.USERS", user);
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
