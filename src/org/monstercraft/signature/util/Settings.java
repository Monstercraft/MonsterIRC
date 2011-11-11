package org.monstercraft.signature.util;

import java.io.File;

import org.monstercraft.signature.Signature;

public class Settings {

	public void GenConfig() {
		try {
			Signature.config.set("sig.view", "Monstercraft.org/Signature/View.php");
			Signature.config.set("sig.save", "Monstercraft.org/Signature/Submit.php");
			Signature.config.save(new File(Constants.SETTINGS_PATH
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
				Signature.config.load(CONFIGURATION_FILE);
				Variables.view = Signature.config.getString("sig.view");
				Variables.save = Signature.config.getString("sig.save");
			} catch (Exception e) {
				GenConfig();
				e.printStackTrace();
			}
		}
	}

}
