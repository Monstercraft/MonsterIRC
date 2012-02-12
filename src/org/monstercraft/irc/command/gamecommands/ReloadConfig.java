package org.monstercraft.irc.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.GameCommand;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;

public class ReloadConfig extends GameCommand {

	public ReloadConfig(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("reload");
	}

	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (IRC.getHandleManager().getPermissionsHandler() != null) {
				if (!IRC.getHandleManager().getPermissionsHandler()
						.hasCommandPerms(((Player) sender), this)) {
					sender.sendMessage("[IRC] You must run this command via the console.");
					return false;
				}
			} else {
				sender.sendMessage("[IRC] You must run this command via the console.");
				return false;
			}
			sender.sendMessage("[IRC] You must run this command via the console.");
			return false;
		}
		if (IRC.getHandleManager().getIRCHandler()
				.isConnected(IRC.getIRCServer())) {
			IRC.getHandleManager().getIRCHandler()
					.disconnect(IRC.getIRCServer());
		}
		plugin.getSettingsManager().reload();
		IRC.getHandleManager().getIRCHandler().connect(IRC.getIRCServer());
		for (IRCChannel c : Variables.channels) {
			if (c.isAutoJoin()) {
				c.join();
			}
		}
		return true;
	}

	@Override
	public String getPermissions() {
		return "irc.console";
	}

}
