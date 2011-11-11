package org.monstercraft.signature;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;
import org.monstercraft.signature.listener.MCPlayerListener;
import org.monstercraft.signature.util.Settings;

public class Signature extends JavaPlugin {

	public static Server server = null;
	public static Settings settings = null;
	public static FileConfiguration config = null;
	public static MCPlayerListener playerListener = null;
	public static List<org.monstercraft.irc.command.Command> commands = null;

	public void onEnable() {
		server = getServer();
		config = getConfig();
		settings = new Settings();
		commands = new ArrayList<org.monstercraft.irc.command.Command>();
		settings.LoadConfigs();
		setUp();
		System.out.println("[SIG] Successfully started up.");

	}

	public void onDisable() {
		
		System.out.println("[SIG] Successfully disabled plugin.");
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

	private void setUp() {
		playerListener = new MCPlayerListener(this);
		server.getPluginManager().registerEvent(Event.Type.PLAYER_JOIN,
				playerListener, Priority.Normal, this);
		server.getPluginManager().registerEvent(Event.Type.PLAYER_QUIT,
				playerListener, Priority.Normal, this);
	}
}
