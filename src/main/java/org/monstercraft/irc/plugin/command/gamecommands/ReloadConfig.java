package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.command.GameCommand;

public class ReloadConfig extends GameCommand {

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("reload");
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (MonsterIRC.getHandleManager().getPermissionsHandler() != null) {
				if (!MonsterIRC.getHandleManager().getPermissionsHandler()
						.hasCommandPerms(((Player) sender), this)) {
					sender.sendMessage("[IRC] You don't have permission to preform that command.");
					return true;
				}
			} else {
				sender.sendMessage("[IRC] PEX not detected, unable to run any IRC commands.");
				return true;
			}
		}
		Thread t = new Thread(connect);
		t.setPriority(Thread.MAX_PRIORITY);
		t.setDaemon(false);
		t.start();
		return true;
	}

	private Runnable connect = new Runnable() {
		@Override
		public void run() {
			MonsterIRC.getSettingsManager().reload();
			MonsterIRC.getHandleManager().getPluginHandler().stopPlugins();
			MonsterIRC.getHandleManager().setIRCPluginHandler();
			MonsterIRC.getHandleManager().getIRCHandler()
					.connect(MonsterIRC.getIRCServer());
		}
	};

	@Override
	public String getPermissions() {
		return "irc.reload";
	}

}
