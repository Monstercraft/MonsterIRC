package org.monstercraft.irc.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.command.Command;

public class Say extends Command {

	public Say(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(CommandSender sender, String[] split) {
		return (!(sender instanceof Player) && plugin.IRC.isConnected() && split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("say"));
	}

	public boolean execute(CommandSender sender, String[] split) {
		StringBuffer result = new StringBuffer();
		StringBuffer result2 = new StringBuffer();
		result.append("[IRC]<" + sender.getName() + ">" + " ");
		for (int i = 2; i < split.length; i++) {
			result.append(split[i]);
			result.append(" ");
			result2.append(split[i]);
			result2.append(" ");
		}

		plugin.IRC.sendMessage(result.toString());
		server.broadcastMessage(result.toString());
		return true;
	}

}
