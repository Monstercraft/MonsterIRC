package org.monstercraft.irc.util;

import java.io.File;

import org.monstercraft.irc.IRC;

public class Settings {

	public void GenConfig() {
		try {
			IRC.config.set("irc.port", 6667);
			IRC.config.set("irc.server", "irc.esper.net");
			IRC.config.set("irc.channel", "#Monstercraft");
			IRC.config.set("irc.name", "MonsterCraft");
			IRC.config.set("irc.login", "MC");
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
				Variables.port = IRC.config.getInt("irc.port");
				Variables.server = IRC.config.getString("irc.server");
				Variables.channel = IRC.config.getString("irc.channel");
				Variables.name = IRC.config.getString("irc.name");
				Variables.login = IRC.config.getString("irc.login");
				Variables.amount = IRC.config.getInt("irc.messagelimit");
			} catch (Exception e) {
				GenConfig();
				e.printStackTrace();
			}
		}
	}

}
