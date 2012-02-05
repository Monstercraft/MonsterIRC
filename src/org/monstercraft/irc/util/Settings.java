package org.monstercraft.irc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.wrappers.IRCChannel;

/**
 * This class contains all of the plugins settings.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class Settings extends IRC {
	private boolean firstRun = false;

	/**
	 * Creates an instance of the Settings class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public Settings(IRC plugin) {
		loadConfigs();
		populateChannels();
		loadMuteConfig();
		loadFormatConfig();
	}

	/**
	 * Reload all of the configuration files.
	 */
	public void reloadConfigs() {
		loadConfigs();
		populateChannels();
		loadMuteConfig();
		loadFormatConfig();
	}

	/**
	 * This method loads the plugins configuration file.
	 */
	public void loadConfigs() {
		Properties p = new Properties();
		final File CONFIGURATION_FILE = new File(Constants.SETTINGS_PATH
				+ Constants.SETTINGS_FILE);
		if (!CONFIGURATION_FILE.exists()) {
			new File(Constants.SETTINGS_PATH).mkdirs();
			log("Setting up default settings!");
		}
		try {
			try {
				p.load(new FileInputStream(CONFIGURATION_FILE));
			} catch (FileNotFoundException e) {
				debug(e);
			} catch (IOException e) {
				debug(e);
			}
			if (p.getProperty("NICKSERV_IDENTIFY") != null) {
				Variables.ident = Boolean.parseBoolean(p
						.getProperty("NICKSERV_IDENTIFY"));
			} else {
				p.setProperty("NICKSERV_IDENTIFY",
						String.valueOf(Variables.ident));
			}
			if (p.getProperty("NICKSERV_PASSWORD") != null) {
				Variables.password = p.getProperty("NICKSERV_PASSWORD");
			} else {
				p.setProperty("NICKSERV_PASSWORD", Variables.password);
			}
			if (p.getProperty("NICK_NAME") != null) {
				Variables.name = p.getProperty("NICK_NAME");
			} else {
				p.setProperty("NICK_NAME", Variables.name);
			}
			if (p.getProperty("SERVER") != null) {
				Variables.server = p.getProperty("SERVER");
			} else {
				p.setProperty("SERVER", Variables.server);
			}
			if (p.getProperty("PORT") != null) {
				Variables.port = Integer.parseInt(p.getProperty("PORT"));
			} else {
				p.setProperty("PORT", String.valueOf(Variables.port));
			}
			if (p.getProperty("PING_TIMEOUT") != null) {
				Variables.timeout = Integer.parseInt(p
						.getProperty("PING_TIMEOUT"));
			} else {
				p.setProperty("PING_TIMEOUT", String.valueOf(Variables.timeout));
			}
			if (p.getProperty("DEBUG") != null) {
				Variables.debug = Boolean.parseBoolean(p.getProperty("DEBUG"));
			} else {
				p.setProperty("DEBUG", String.valueOf(Variables.debug));
			}
			if (p.getProperty("IRC_INGAME_COMMANDS") != null) {
				Variables.ingamecommands = Boolean.parseBoolean(p
						.getProperty("IRC_INGAME_COMMANDS"));
			} else {
				p.setProperty("IRC_INGAME_COMMANDS",
						String.valueOf(Variables.ingamecommands));
			}
			if (p.getProperty("PASS_CHAT_ON_NAME") != null) {
				Variables.passOnName = Boolean.parseBoolean(p
						.getProperty("PASS_CHAT_ON_NAME"));
			} else {
				p.setProperty("PASS_CHAT_ON_NAME",
						String.valueOf(Variables.passOnName));
			}
			if (p.getProperty("ALLOW_COLORED_MESSAGES") != null) {
				Variables.colors = Boolean.parseBoolean(p
						.getProperty("ALLOW_COLORED_MESSAGES"));
			} else {
				p.setProperty("ALLOW_COLORED_MESSAGES",
						String.valueOf(Variables.colors));
			}
			if (p.getProperty("JOIN_AND_LEAVE_MESSAGES") != null) {
				Variables.joinAndQuit = Boolean.parseBoolean(p
						.getProperty("JOIN_AND_LEAVE_MESSAGES"));
			} else {
				p.setProperty("JOIN_AND_LEAVE_MESSAGES",
						String.valueOf(Variables.joinAndQuit));
			}
		} catch (Exception e) {
			debug(e);
		}
		try {
			p.store(new FileOutputStream(CONFIGURATION_FILE),
					"For more information please refer to http://dev.bukkit.org/server-mods/monsterirc/pages/config/");
		} catch (FileNotFoundException e) {
			debug(e);
		} catch (IOException e) {
			debug(e);
		}
		if (Variables.name.equalsIgnoreCase("Default")) {
			firstRun = true;
		}
	}

	/**
	 * This method loads the muted users text file.
	 */
	public void loadMuteConfig() {
		Properties p = new Properties();
		final File CONFIGURATION_FILE = new File(Constants.SETTINGS_PATH
				+ Constants.MUTED_FILE);
		if (Variables.muted == null) {
			Variables.muted = new ArrayList<String>();
		}
		Variables.muted.clear();
		if (!CONFIGURATION_FILE.exists()) {
			new File(Constants.SETTINGS_PATH).mkdirs();
			saveMuteConfig();
		} else {
			try {
				p.load(new FileInputStream(CONFIGURATION_FILE));
			} catch (FileNotFoundException e) {
				debug(e);
			} catch (IOException e) {
				debug(e);
			}
			for (Object o : p.keySet()) {
				String s = ((String) o);
				Variables.muted.add(p.getProperty(String.valueOf(s)));
			}
		}
	}

	/**
	 * This method saves the muted users text file.
	 */
	public void saveMuteConfig() {
		final File CONFIGURATION_FILE = new File(Constants.SETTINGS_PATH
				+ Constants.MUTED_FILE);
		Properties p = new Properties();
		p.clear();
		for (int i = 0; i < Variables.muted.size(); i++) {
			p.setProperty(String.valueOf(i), Variables.muted.get(i));
		}
		try {
			p.store(new FileOutputStream(CONFIGURATION_FILE),
					"MonsterIRC's Muted Users - Please don't edit unless you know how to properly!");
		} catch (FileNotFoundException e) {
			debug(e);
		} catch (IOException e) {
			debug(e);
		}
	}

	/**
	 * This method saves the format Config.
	 */
	public void saveFormatConfig() {
		final File CONFIGURATION_FILE = new File(Constants.SETTINGS_PATH
				+ Constants.FORMAT_FILE);
		Properties p = new Properties();
		p.clear();
		p.setProperty("Format", Variables.format);
		try {
			p.store(new FileOutputStream(CONFIGURATION_FILE),
					"This is the way the messages will be sent to the server, excluding adminchat.\n Must contain {name} and {message} or the default format will be loaded.");
		} catch (FileNotFoundException e) {
			debug(e);
		} catch (IOException e) {
			debug(e);
		}
	}

	/**
	 * This method loads the formatting for relaying messages to the server from
	 * irc.
	 */
	public void loadFormatConfig() {
		Variables.format = "<{name}>: {message}";
		Properties p = new Properties();
		final File CONFIGURATION_FILE = new File(Constants.SETTINGS_PATH
				+ Constants.FORMAT_FILE);
		if (!CONFIGURATION_FILE.exists()) {
			new File(Constants.SETTINGS_PATH).mkdirs();
			saveFormatConfig();
		} else {
			try {
				p.load(new FileInputStream(CONFIGURATION_FILE));
			} catch (FileNotFoundException e) {
				debug(e);
			} catch (IOException e) {
				debug(e);
			}
			String s = p.getProperty("Format");
			if (s.contains("{name}") && s.contains("{message}")) {
				Variables.format = s;
			} else {
				debug("Invalid format detected!");
			}
		}
	}

	/**
	 * This method loads all of the channels.
	 */
	public void populateChannels() {
		Variables.channels.clear();
		final File CHANNEL_DIR = new File(Constants.SETTINGS_PATH
				+ Constants.CHANNELS_PATH);
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
		for (File f : files) {
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(f));
			} catch (FileNotFoundException e) {
				debug(e);
			} catch (IOException e) {
				debug(e);
			}
			if (p.getProperty("Enabled") != null) {
				if (Boolean.parseBoolean(p.getProperty("Enabled"))) {
					Variables.channels.add(new IRCChannel(Boolean
							.parseBoolean(p.getProperty("AutoJoin", "false")),
							Boolean.parseBoolean(p.getProperty("Default",
									"false")), "#"
									+ f.getName().substring(0,
											f.getName().lastIndexOf(".")),
							ChatType.ALL));
				}
			} else if (p.getProperty("AdminChatEnabled") != null) {
				if (Boolean.parseBoolean(p.getProperty("AdminChatEnabled"))) {
					Variables.channels.add(new IRCChannel(Boolean
							.parseBoolean(p.getProperty("AutoJoin", "false")),
							Boolean.parseBoolean(p.getProperty("Default",
									"false")), "#"
									+ f.getName().substring(0,
											f.getName().lastIndexOf(".")),
							ChatType.ADMINCHAT));
				}
			} else if (p.getProperty("HeroChatEnabled") != null) {
				if (Boolean.parseBoolean(p.getProperty("HeroChatEnabled"))) {
					Variables.channels.add(new IRCChannel(Boolean
							.parseBoolean(p.getProperty("AutoJoin", "false")),
							Boolean.parseBoolean(p.getProperty("Default",
									"false")), "#"
									+ f.getName().substring(0,
											f.getName().lastIndexOf(".")), p
									.getProperty("HeroChatChannel"),
							ChatType.HEROCHAT));
				}
			}
		}
	}

	/**
	 * This methods creates the default sample channel files for the plugin.
	 */
	public void createDefaultChannel() {
		File f = new File(Constants.SETTINGS_PATH + Constants.CHANNELS_PATH);
		f.mkdirs();
		Properties p = new Properties();
		final File HeroChatSample = new File(Constants.SETTINGS_PATH
				+ Constants.CHANNELS_PATH + "HeroChatSample.channel");
		final File AdminChatSample = new File(Constants.SETTINGS_PATH
				+ Constants.CHANNELS_PATH + "AdminChatSample.channel");
		final File Sample = new File(Constants.SETTINGS_PATH
				+ Constants.CHANNELS_PATH + "Sample.channel");
		p.setProperty("HeroChatEnabled", String.valueOf(false));
		p.setProperty("Default", String.valueOf(true));
		p.setProperty("AutoJoin", String.valueOf(false));
		p.setProperty("HeroChatChannel", "PLACE INGAME CHANNEL NAME HERE!");
		try {
			p.store(new FileOutputStream(HeroChatSample),
					"This is a HeroChat sample, use this to create other .channel files that will integrate with herochat.\n This type of channel file will allow chat from players within that HeroChat channel ingame to communicate with the specified IRC channel.\n The plugin will connect to the IRC channel with the same name as this file!");
		} catch (FileNotFoundException e) {
			debug(e);
		} catch (IOException e) {
			debug(e);
		}
		p.clear();
		p.setProperty("Enabled", String.valueOf(false));
		p.setProperty("Default", String.valueOf(true));
		p.setProperty("AutoJoin", String.valueOf(false));
		try {
			p.store(new FileOutputStream(Sample),
					"This is a sample, use this to create other .channel files.\n This type of channel file will allow chat from all players ingame to communicate with the specified IRC channel.\n The plugin will connect to the IRC channel with the same name as this file!");
		} catch (FileNotFoundException e) {
			debug(e);
		} catch (IOException e) {
			debug(e);
		}
		p.clear();
		p.setProperty("AdminChatEnabled", String.valueOf(false));
		p.setProperty("Default", String.valueOf(true));
		p.setProperty("AutoJoin", String.valueOf(false));
		try {
			p.store(new FileOutputStream(AdminChatSample),
					"This is a AdminChat sample, use this to create other .channel files that will integrate with AdminChat.\n This type of channel file will allow chat from players within AdminChat ingame to communicate with the specified IRC channel.\n The plugin will connect to the IRC channel with the same name as this file!");
		} catch (FileNotFoundException e) {
			debug(e);
		} catch (IOException e) {
			debug(e);
		}
		p.clear();
		new CreateReadme();
		log("*************************************************");
		log("*************************************************");
		log("*************************************************");
		log("*************************************************");
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
