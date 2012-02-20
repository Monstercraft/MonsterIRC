package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.command.GameCommand;

public class ReloadConfig extends GameCommand {

	public ReloadConfig(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("reload");
	}

	@Override
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
		IRC.getSettingsManager().reload();
		IRC.getHandleManager().getIRCHandler().connect(IRC.getIRCServer());
		return true;
	}

	@Override
	public String getPermissions() {
		return "irc.reload";
	}

}
