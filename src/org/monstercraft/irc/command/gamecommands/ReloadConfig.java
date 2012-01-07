package org.monstercraft.irc.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.command.GameCommand;
import org.monstercraft.irc.util.Variables;

public class ReloadConfig extends GameCommand {

	public ReloadConfig(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(CommandSender sender, String[] split) {
		return plugin.getHandleManager().getIRCHandler().isConnected()
				&& split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("reloadconfig");
	}

	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (!plugin.getHandleManager().getPermissionsHandler()
					.hasCommandPerms(((Player) sender), this)) {
				sender.sendMessage("[IRC] You don't have permission to preform that command.");
				return false;
			}
			sender.sendMessage("[IRC] You must run this command via the console.");
			return false;
		}
		plugin.getHandleManager().getIRCHandler().disconnect(Variables.channel);
		plugin.getSettings().loadConfigs();
		return plugin
				.getHandleManager()
				.getIRCHandler()
				.connect(Variables.channel, Variables.server, Variables.port,
						Variables.login, Variables.name, Variables.password,
						Variables.ident);
	}

	@Override
	public String getPermissions() {
		return "irc.console";
	}

}
