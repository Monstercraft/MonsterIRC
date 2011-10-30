package org.monstercraft.irc;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.monstercraft.irc.command.Commands;
import org.monstercraft.irc.handlers.IRCHandler;

public class IRC extends JavaPlugin {

	public static Server server = null;

	public void onEnable() {
		server = getServer();
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
