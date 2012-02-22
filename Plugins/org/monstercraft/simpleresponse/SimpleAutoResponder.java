package org.monstercraft.simpleresponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.FileConfiguration;
import org.monstercraft.irc.ircplugin.IRCPlugin;
import org.monstercraft.irc.ircplugin.PluginManifest;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCServer;

//Must have a plugin manifest and extends IRC plugin for it to be a valid plugin
//the name is the name of your plugin
@PluginManifest(name = "Simple Auto Responder")
public class SimpleAutoResponder extends IRCPlugin implements IRCListener {

	private FileConfiguration config;
	private List<String> input;
	private List<String> output;

	@Override
	public void onFinish() {
		// Logs that it has successfully stopped.
		log("Simple auto responder has stopped.");
	}

	@Override
	public boolean onStart() {
		// Gets the file configuration.
		config = getConfig();

		// Creats an empty array list for us to save the settings to.
		output = new ArrayList<String>();
		input = new ArrayList<String>();

		// The settings file to load
		File SETTINGS_FILE = new File(getCacheDirectory() + File.separator
				+ "Config.yml");

		// Checks if the file exists.
		boolean exists = SETTINGS_FILE.exists();

		if (exists) {
			try {
				// Loads the settings file if it exists
				config.load(SETTINGS_FILE);

				// set the variables
				input = config.getStringList("AUTO_RESPONDER.MESSAGE");
				output = config.getStringList("AUTO_RESPONDER.RESPONSE");
			} catch (Exception e) {
				debug(e);
			}
		} else {
			// add samples so people know how to correctly modify it
			input.add("hi");
			output.add("Hello.");

			// set the configs
			config.set("AUTO_RESPONDER.MESSAGE", input);
			config.set("AUTO_RESPONDER.RESPONSE", output);

			// save the configs
			saveConfig(config, SETTINGS_FILE);
		}

		// Logs in the console that we have started up successfully
		log("Simple auto responder started.");
		return true;// True for the plugin to start, otherwise false to just
					// kill the plugin
	}

	@Override
	public void onAction(IRCChannel arg0, String arg1, String arg2) {
	}

	@Override
	public void onConnect(IRCServer arg0) {
	}

	@Override
	public void onDisconnect(IRCServer arg0) {
	}

	@Override
	public void onJoin(IRCChannel arg0, String arg1) {
	}

	@Override
	public void onKick(IRCChannel arg0, String arg1, String arg2) {
	}

	@Override
	public void onMessage(IRCChannel c, String sender, String msg) {
		// Check if the input strings
		for (String s : input) {
			// Get the indes of the string in the input
			int index = input.indexOf(s);
			// Check if the input contains the string
			if (LineContainsWord(msg, s)) {
				// check for the output message
				if (output.get(index) != null) {
					// send the output message
					sendMessage(c, output.get(index));
				} else {
					// The output message was null, something in the config was
					// wrong.
					log("Invalid configuration file for SimpleAutoResponder");
				}
			}
		}
	}

	@Override
	public void onMode(IRCChannel arg0, String arg1, String arg2, String arg3) {
	}

	@Override
	public void onPart(IRCChannel arg0, String arg1) {
	}

	@Override
	public void onPrivateMessage(String arg0, String arg1, String arg2) {
	}

	public void onQuit(IRCChannel channel, String arg1) {
	}

	public boolean LineContainsWord(final String line, final String Word) {
		try {
			return Pattern.compile("\\b" + Word + "\\b").matcher(line).find();
		} catch (IllegalStateException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}

}
