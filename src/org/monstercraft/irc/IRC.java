package org.monstercraft.irc;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.monstercraft.irc.chat.hChat;
import org.monstercraft.irc.command.commands.Connect;
import org.monstercraft.irc.command.commands.Disconnect;
import org.monstercraft.irc.command.commands.Nick;
import org.monstercraft.irc.command.commands.Say;
import org.monstercraft.irc.handlers.IRCHandler;
import org.monstercraft.irc.listeners.IRCPlayerListener;
import org.monstercraft.irc.util.Settings;
import org.monstercraft.irc.util.Variables;

public class IRC extends JavaPlugin {

	public Server server;
	public Settings settings;
	public static FileConfiguration config;
	public List<org.monstercraft.irc.command.Command> commands;
	public IRCPlayerListener playerListener;
	public hChat HeroChat;
	public IRCHandler handle;

	public void onEnable() {
		server = getServer();
		config = getConfig();
		settings = new Settings();
		HeroChat = new hChat(this);
		handle = new IRCHandler(this);
		commands = new ArrayList<org.monstercraft.irc.command.Command>();
		settings.LoadConfigs();
		registerEvents();
		registerCommands();
		if (Variables.autoJoin) {
			handle.connect();
		}
		System.out.println("[IRC] Successfully started up.");

	}

	public void onDisable() {
		handle.disconnect();
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

	private void registerCommands() {
		commands.add(new Connect(this));
		commands.add(new Disconnect(this));
		commands.add(new Nick(this));
		commands.add(new Say(this));
	}

	private void registerEvents() {
		playerListener = new IRCPlayerListener(this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_CHAT,
				playerListener, Priority.High, this);
	}
}
