package org.monstercraft.irc.util;

import java.io.File;

import org.monstercraft.irc.IRC;

public class Settings {

	public void GenConfig() {
		try {
			IRC.config.options().copyDefaults();
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
		try {
			IRC.config.load(new File(Constants.SETTINGS_PATH
					+ Constants.SETTINGS_FILE));
			Variables.port = IRC.config.getInt("irc.port", 6667);
			Variables.server = IRC.config.getString("irc.server",
					"irc.esper.ner");
			Variables.channel = IRC.config.getString("irc.channel",
					"#Monstercraft");
			Variables.name = IRC.config.getString("irc.name", "MonsterCraft");
			Variables.login = IRC.config.getString("irc.login", "MC");
			Variables.amount = IRC.config.getInt("irc.messagelimit", 5);
		} catch (Exception e) {
			GenConfig();
		}
	}

}
