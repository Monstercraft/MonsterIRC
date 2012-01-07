package org.monstercraft.irc.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.command.GameCommand;
import org.monstercraft.irc.util.Variables;

public class Say extends GameCommand {

	public Say(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(CommandSender sender, String[] split) {
		return plugin.getHandleManager().getIRCHandler().isConnected()
				&& split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("say");
	}

	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (!plugin.getHandleManager().getPermissionsHandler()
					.hasCommandPerms(((Player) sender), this)) {
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

		plugin.getHandleManager().getIRCHandler()
				.sendMessage(result.toString(), Variables.channel);
		if (Variables.hc && plugin.getHookManager().getHeroChatHook() != null) {
			plugin.getHookManager()
					.getHeroChatHook()
					.getChannelManager()
					.getChannel(Variables.hcc)
					.sendMessage(
							"<" + sender.getName() + ">",
							result2.toString(),
							plugin.getHookManager().getHeroChatHook()
									.getChannelManager()
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
