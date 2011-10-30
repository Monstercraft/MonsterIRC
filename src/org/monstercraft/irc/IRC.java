package org.monstercraft.irc;

import java.io.File;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.monstercraft.irc.command.Commands;
import org.monstercraft.irc.handlers.IRCHandler;
import org.monstercraft.irc.util.Constants;
import org.monstercraft.irc.util.Settings;

public class IRC extends JavaPlugin {

	public static Server server = null;
	public Settings settings = null;
	public static FileConfiguration config;

	public void onEnable() {
		server = getServer();
		config = getConfig();
		settings = new Settings();
		File con = new File(Constants.SETTINGS_PATH + Constants.SETTINGS_FILE);
		if (!con.exists()) {
			new File(Constants.SETTINGS_PATH).mkdirs();
			settings.GenConfig();
		} else {
			settings.LoadConfigs();
		}
		IRCHandler.connect();
		if (IRCHandler.isConnected()) {
			System.out.println("[IRC] Successfully connected to IRC.");
		} else {
			System.out.println("[IRC] Could not connect to IRC, Perhaps the name was taken.");
		}
	}

	public void onDisable() {
		IRCHandler.disconnect();
		System.out.println("[IRC] Successfully disabled plugin.");
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		return Commands.processCommands(sender, command, label, args);
	}

}
