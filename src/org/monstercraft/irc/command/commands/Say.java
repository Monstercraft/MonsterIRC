package org.monstercraft.irc.command.commands;

import org.bukkit.command.CommandSender;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.Command;
import org.monstercraft.irc.util.Variables;

public class Say extends Command {

	public Say(IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(CommandSender sender, String[] split) {
		return plugin.handle.isConnected() && split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("say");
	}

	public boolean execute(CommandSender sender, String[] split) {
		StringBuffer result = new StringBuffer();
		StringBuffer result2 = new StringBuffer();
		result.append("<" + sender.getName() + ">" + ":");
		for (int i = 2; i < split.length; i++) {
			result.append(split[i]);
			result.append(" ");
			result2.append(split[i]);
			result2.append(" ");
		}

		plugin.handle.sendMessage(result.toString());
		if (this.plugin.HeroChat.getChannel(Variables.hc) != null) {
			plugin.HeroChat.getChannel(Variables.hc).sendMessage(
					sender.getName(), result2.toString());
		}
		return true;
	}

}
