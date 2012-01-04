package org.monstercraft.irc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.monstercraft.irc.IRC;

public class Settings {

	private FileConfiguration config;
	private IRC plugin;
	private Properties p;

	public Settings(IRC plugin) {
		this.plugin = plugin;
		config = plugin.getConfig();
		p = new Properties();
		loadConfigs();
		loadMuteConfig();
	}

	public void saveConfig() {
		try {
			config.set("IRC.SETTINGS_VERSION", 1.0);
			config.set("IRC.AUTO_JOIN", Variables.autoJoin);
			config.set("IRC.NICKSERV_IDENTIFY", Variables.ident);
			config.set("IRC.NICKSERV_PASSWORD", Variables.password);
			config.set("IRC.NICKSERV_LOGIN", Variables.login);
			config.set("IRC.NICK_NAME", Variables.name);
			config.set("IRC.SERVER", Variables.server);
			config.set("IRC.PORT", Variables.port);
			config.set("IRC.CHANNEL", Variables.channel);
			config.set("IRC.ALL_CHAT.ENABLED", Variables.all);
			config.set("IRC.HEROCHAT.ENABLED", Variables.hc);
			config.set("IRC.HEROCHAT.INGAME_HEROCHAT_CHANNEL_IRC",
					Variables.hcc);
			config.set("IRC.HEROCHAT.INGAME_HEROCHAT_CHANNEL_ALERT",
					Variables.announce);
			config.save(new File(Constants.SETTINGS_PATH
					+ Constants.SETTINGS_FILE));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadConfigs() {
		final File CONFIGURATION_FILE = new File(Constants.SETTINGS_PATH
				+ Constants.SETTINGS_FILE);
		if (!CONFIGURATION_FILE.exists()) {
			new File(Constants.SETTINGS_PATH).mkdirs();
			plugin.log("Setting up default settings!");
			saveConfig();
		} else {
			try {
				config.load(CONFIGURATION_FILE);
				Variables.version = config.getDouble("IRC.SETTINGS_VERSION");
				Variables.autoJoin = config.getBoolean("IRC.AUTO_JOIN");
				Variables.ident = config.getBoolean("IRC.NICKSERV_IDENTIFY");
				Variables.password = config.getString("IRC.NICKSERV_PASSWORD");
				Variables.login = config.getString("IRC.NICKSERV_LOGIN");
				Variables.name = config.getString("IRC.NICK_NAME");
				Variables.server = config.getString("IRC.SERVER");
				Variables.port = config.getInt("IRC.PORT");
				Variables.channel = config.getString("IRC.CHANNEL");
				Variables.all = config.getBoolean("IRC.ALL_CHAT.ENABLED");
				Variables.hc = config.getBoolean("IRC.HEROCHAT.ENABLED");
				Variables.hcc = config
						.getString("IRC.HEROCHAT.INGAME_HEROCHAT_CHANNEL_IRC");
				Variables.announce = config
						.getString("IRC.HEROCHAT.INGAME_HEROCHAT_CHANNEL_ALERT");
			} catch (Exception e) {
				saveConfig();
				e.printStackTrace();
			}
		}
	}

	public void loadMuteConfig() {
		final File CONFIGURATION_FILE = new File(Constants.SETTINGS_PATH
				+ Constants.MUTED_FILE);
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

	public void saveMuteConfig() {
		final File CONFIGURATION_FILE = new File(Constants.SETTINGS_PATH
				+ Constants.MUTED_FILE);
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
}
