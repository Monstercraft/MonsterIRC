package org.monstercraft.irc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.wrappers.IRCChannel;

/**
 * This class contains all of the plugins settings.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class Settings extends IRC {

	private FileConfiguration config;
	private boolean firstRun = false;

	/**
	 * Creates an instance of the Settings class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public Settings(IRC plugin) {
		config = plugin.getConfig();
		loadConfigs();
		populateChannels();
		loadMuteConfig();
	}

	/**
	 * This method saves the plugins configuration file.
	 */
	public void saveConfig() {
		try {
			config.set("IRC.NICKSERV_IDENTIFY", Variables.ident);
			config.set("IRC.NICKSERV_LOGIN", Variables.login);
			config.set("IRC.NICKSERV_PASSWORD", Variables.password);
			config.set("IRC.NICK_NAME", Variables.name);
			config.set("IRC.SERVER", Variables.server);
			config.set("IRC.PORT", Variables.port);
			config.save(new File(Constants.SETTINGS_PATH
					+ Constants.SETTINGS_FILE));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method loads the plugins configuration file.
	 */
	public void loadConfigs() {
		final File CONFIGURATION_FILE = new File(Constants.SETTINGS_PATH
				+ Constants.SETTINGS_FILE);
		if (!CONFIGURATION_FILE.exists()) {
			new File(Constants.SETTINGS_PATH).mkdirs();
			log("Setting up default settings!");
			saveConfig();
		} else {
			try {
				config.load(CONFIGURATION_FILE);
				Variables.ident = config.getBoolean("IRC.NICKSERV_IDENTIFY");
				Variables.password = config.getString("IRC.NICKSERV_PASSWORD");
				Variables.login = config.getString("IRC.NICKSERV_LOGIN");
				Variables.name = config.getString("IRC.NICK_NAME");
				Variables.server = config.getString("IRC.SERVER");
				Variables.port = config.getInt("IRC.PORT");
			} catch (Exception e) {
				saveConfig();
				e.printStackTrace();
			}
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
		if (!CONFIGURATION_FILE.exists()) {
			new File(Constants.SETTINGS_PATH).mkdirs();
			saveMuteConfig();
		} else {
			try {
				p.load(new FileInputStream(CONFIGURATION_FILE));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (Object o : p.keySet()) {
				Variables.muted
						.add(p.getProperty(String.valueOf(((String) o))));
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method loads all of the channels.
	 */
	public void populateChannels() {
		final File CHANNEL_DIR = new File(Constants.SETTINGS_PATH
				+ Constants.CHANNELS_PATH);
		HashSet<File> files = new HashSet<File>();
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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (p.getProperty("Enabled") != null) {
				if (Boolean.parseBoolean(p.getProperty("Enabled"))) {
					Variables.channels
							.add(new IRCChannel(Boolean.parseBoolean(p
									.getProperty("AutoJoin")), "#"
									+ f.getName().substring(0,
											f.getName().lastIndexOf(".")),
									ChatType.ALL));
				}
			} else if (p.getProperty("AdminChatEnabled") != null) {
				if (Boolean.parseBoolean(p.getProperty("AdminChatEnabled"))) {
					Variables.channels.add(new IRCChannel(Boolean
							.parseBoolean(p.getProperty("AutoJoin")), "#"
							+ f.getName().substring(0,
									f.getName().lastIndexOf(".")),
							ChatType.ADMINCHAT));
				}
			} else if (p.getProperty("HeroChatEnabled") != null) {
				if (Boolean.parseBoolean(p.getProperty("HeroChatEnabled"))) {
					Variables.channels
							.add(new IRCChannel(Boolean.parseBoolean(p
									.getProperty("AutoJoin")), "#"
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
		p.setProperty("AutoJoin", String.valueOf(false));
		p.setProperty("HeroChatChannel", "PLACE INGAME CHANNEL NAME HERE!");
		try {
			p.store(new FileOutputStream(HeroChatSample),
					"This is a HeroChat sample, use this to create other .channel files that will integrate with herochat.\n This type of channel file will allow chat from players within that HeroChat channel ingame to communicate with the specified IRC channel.\n The plugin will connect to the IRC channel with the same name as this file!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		p.clear();
		p.setProperty("Enabled", String.valueOf(false));
		p.setProperty("AutoJoin", String.valueOf(false));
		try {
			p.store(new FileOutputStream(Sample),
					"This is a sample, use this to create other .channel files.\n This type of channel file will allow chat from all players ingame to communicate with the specified IRC channel.\n The plugin will connect to the IRC channel with the same name as this file!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		p.clear();
		p.setProperty("AdminChatEnabled", String.valueOf(false));
		p.setProperty("AutoJoin", String.valueOf(false));
		try {
			p.store(new FileOutputStream(AdminChatSample),
					"This is a AdminChat sample, use this to create other .channel files that will integrate with AdminChat.\n This type of channel file will allow chat from players within AdminChat ingame to communicate with the specified IRC channel.\n The plugin will connect to the IRC channel with the same name as this file!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public boolean firstRun() {
		return firstRun;
	}
}
