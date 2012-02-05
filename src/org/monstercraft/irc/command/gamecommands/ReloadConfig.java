package org.monstercraft.irc.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.GameCommand;
import org.monstercraft.irc.util.Variables;

public class ReloadConfig extends GameCommand {

	public ReloadConfig(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(CommandSender sender, String[] split) {
		return IRC.getHandleManager().getIRCHandler().isConnected()
				&& split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("reloadconfig");
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
		IRC.getHandleManager().getIRCHandler().disconnect();
		plugin.getSettings().reloadConfigs();
		return IRC
				.getHandleManager()
				.getIRCHandler()
				.connect(Variables.server, Variables.port, Variables.name,
						Variables.password, Variables.ident, Variables.timeout);
	}

	@Override
	public String getPermissions() {
		return "irc.console";
	}

}
