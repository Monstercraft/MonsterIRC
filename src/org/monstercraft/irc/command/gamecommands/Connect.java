package org.monstercraft.irc.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.GameCommand;
import org.monstercraft.irc.util.Variables;

public class Connect extends GameCommand {

	public Connect(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc") && split[1].equalsIgnoreCase("connect")
				&& !IRC.getHandleManager().getIRCHandler().isConnected();
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (!IRC.getHandleManager().getPermissionsHandler()
					.hasCommandPerms(((Player) sender), this)) {
				sender.sendMessage("[IRC] You don't have permission to preform that command.");
				return false;
			}
		}
		return IRC
				.getHandleManager()
				.getIRCHandler()
				.connect(Variables.server, Variables.port, Variables.login,
						Variables.name, Variables.password, Variables.ident);
	}

	@Override
	public String getPermissions() {
		return "irc.connect";
	}

}
