package org.monstercraft.irc;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.monstercraft.irc.command.commands.Connect;
import org.monstercraft.irc.command.commands.Disconnect;
import org.monstercraft.irc.command.commands.Nick;
import org.monstercraft.irc.command.commands.Say;
import org.monstercraft.irc.handlers.IRCHandler;
import org.monstercraft.irc.util.Settings;

public class IRC extends JavaPlugin {

	public static Server server = null;
	public static Settings settings = null;
	public static FileConfiguration config = null;
	public static List<org.monstercraft.irc.command.Command> commands = null;

	public void onEnable() {
		server = getServer();
		config = getConfig();
		settings = new Settings();
		commands = new ArrayList<org.monstercraft.irc.command.Command>();
		settings.LoadConfigs();
		setCommands();
		IRCHandler.connect();
		System.out.println("[IRC] Successfully started up.");

	}

	public void onDisable() {
		IRCHandler.disconnect();
		System.out.println("[IRC] Successfully disabled plugin.");
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String[] split = new String[args.length + 1];
		split[0] = label;
		for (int a = 0; a < args.length; a++) {
			split[a + 1] = args[a];
		}
		for (org.monstercraft.irc.command.Command c : commands) {
			if (c.canExecute(sender, split)) {
				c.execute(sender, split);
			}
		}
		return true;
	}

	private void setCommands() {
		commands.add(new Connect());
		commands.add(new Disconnect());
		commands.add(new Nick());
		commands.add(new Say());
	}
}
