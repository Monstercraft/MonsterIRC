package org.monstercraft.irc.util;

import java.io.File;

import org.monstercraft.irc.IRC;

public class Settings {

	public void GenConfig() {
		try {
			IRC.config.set("irc.auto", false);
			IRC.config.set("irc.identify", false);
			IRC.config.set("irc.password", "default");
			IRC.config.set("irc.port", 6667);
			IRC.config.set("irc.server", "default");
			IRC.config.set("irc.channel", "#default");
			IRC.config.set("irc.name", "default");
			IRC.config.set("irc.login", "default");
			IRC.config.set("irc.herochatchan", "IRC");
			IRC.config.set("irc.messagelimit", 5);
			IRC.config.save(new File(Constants.SETTINGS_PATH
					+ Constants.SETTINGS_FILE));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void LoadConfigs() {
		final File CONFIGURATION_FILE = new File(Constants.SETTINGS_PATH
				+ Constants.SETTINGS_FILE);
		if (!CONFIGURATION_FILE.exists()) {
			new File(Constants.SETTINGS_PATH).mkdirs();
			GenConfig();
		} else {
			try {
				IRC.config.load(CONFIGURATION_FILE);
				Variables.autoJoin = IRC.config.getBoolean("irc.auto");
				Variables.ident = IRC.config.getBoolean("irc.identify");
				Variables.password = IRC.config.getString("irc.password");
				Variables.port = IRC.config.getInt("irc.port");
				Variables.server = IRC.config.getString("irc.server");
				Variables.channel = IRC.config.getString("irc.channel");
				Variables.name = IRC.config.getString("irc.name");
				Variables.login = IRC.config.getString("irc.login");
				Variables.hc = IRC.config.getString("irc.herochatchan");
				Variables.amount = IRC.config.getInt("irc.messagelimit");
			} catch (Exception e) {
				GenConfig();
				e.printStackTrace();
			}
		}
	}

}
