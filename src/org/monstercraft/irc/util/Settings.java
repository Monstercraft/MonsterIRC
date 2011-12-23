package org.monstercraft.irc.util;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.monstercraft.irc.IRC;

public class Settings {
	
	private FileConfiguration config;
	
	public Settings(IRC plugin) {
		config = plugin.getConfig();
		loadConfigs();
	}

	public void saveConfig() {
		try {
			config.set("irc.AUTO_JOIN", false);
			config.set("irc.NICKSERV_IDENTIFY", false);
			config.set("irc.NICKSERV_PASSWORD", "default");
			config.set("irc.NICKSERV_LOGIN", "default");
			config.set("irc.NICK_NAME", "default");
			config.set("irc.SERVER", "default");
			config.set("irc.PORT", 6667);
			config.set("irc.CHANNEL", "#default");
			config.set("irc.INGAME_HEROCHAT_CHANNEL", "IRC");
			config.set("irc.MUTED_IRC_USERS", Variables.muted);
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
			saveConfig();
		} else {
			try {
				config.load(CONFIGURATION_FILE);
				Variables.autoJoin = config.getBoolean("irc.AUTO_JOIN");
				Variables.ident = config.getBoolean("irc.NICKSERV_IDENTIFY");
				Variables.password = config.getString("irc.NICKSERV_PASSWORD");
				Variables.login = config.getString("irc.NICKSERV_LOGIN");
				Variables.name = config.getString("irc.NICK_NAME");
				Variables.server = config.getString("irc.SERVER");
				Variables.port = config.getInt("irc.PORT");
				Variables.channel = config.getString("irc.CHANNEL");
				Variables.hc = config.getString("irc.INGAME_HEROCHAT_CHANNEL");
				Variables.muted = config.getStringList("irc.MUTED_IRC_USERS");
			} catch (Exception e) {
				saveConfig();
				e.printStackTrace();
			}
		}
	}
	
	public void saveMuteConfig() {
		try {
			config.set("irc.MUTED_IRC_USERS", Variables.muted);
			config.save(new File(Constants.SETTINGS_PATH
					+ Constants.SETTINGS_FILE));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
