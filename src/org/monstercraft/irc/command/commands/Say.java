package org.monstercraft.irc.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.command.Command;
import org.monstercraft.irc.util.Variables;

public class Say extends Command {

	public Say(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(CommandSender sender, String[] split) {
		return plugin.IRC.isConnected() && split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("say");
	}

	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (!plugin.perms.hasCommandPerms(((Player) sender), this)) {
				sender.sendMessage("[IRC] You don't have permission to preform that command.");
				return false;
			}
		}
		StringBuffer result = new StringBuffer();
		StringBuffer result2 = new StringBuffer();
		result.append("<" + sender.getName() + "> ");
		for (int i = 2; i < split.length; i++) {
			result.append(split[i]);
			result.append(" ");
			result2.append(split[i]);
			result2.append(" ");
		}

		plugin.IRC.sendMessage(result.toString());
		if (Variables.hc && plugin.herochat.HeroChatHook != null) {
			plugin.herochat.HeroChatHook
					.getChannelManager()
					.getChannel(Variables.hcc)
					.sendMessage(
							"<" + sender.getName() + ">",
							result2.toString(),
							plugin.herochat.HeroChatHook.getChannelManager()
									.getChannel(Variables.hcc).getMsgFormat(),
							false);
		}
		return true;
	}

	@Override
	public String getPermissions() {
		return "irc.say";
	}

}
