package org.monstercraft.irc.util;

import org.monstercraft.irc.IRC;

public class Settings extends IRC {

	public void GenConfig() {
		try {
			getConfig().set("PORT", 6667);
			getConfig().set("SERVER", "irc.esper.net");
			getConfig().set("CHANNEL", "#Monstercraft");
			getConfig().set("NAME", "MonsterCraft");
			getConfig().set("LOGIN", "MC");
			getConfig().set("MESSAGE_LIMIT", 5);
			getConfig().save(Constants.SETTINGS_PATH + Constants.SETTINGS_FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void LoadConfigs() {
		try {
			getConfig().load(Constants.SETTINGS_PATH + Constants.SETTINGS_FILE);
			Variables.port = getConfig().getInt("PORT", 6667);
			Variables.server = getConfig().getString("SERVER", "irc.esper.ner");
			Variables.channel = getConfig().getString("CHANNEL",
					"#Monstercraft");
			Variables.name = getConfig().getString("NAME", "MonsterCraft");
			Variables.login = getConfig().getString("LOGIN", "MC");
			Variables.amount = getConfig().getInt("MESSAGE_LIMIT", 5);
		} catch (Exception e) {
			GenConfig();
		}
	}

}
