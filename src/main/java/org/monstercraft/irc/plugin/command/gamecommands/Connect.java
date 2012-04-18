package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;

public class Connect extends GameCommand {

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc") && split[1].equalsIgnoreCase("connect");
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
		sender.sendMessage("Attempting to connect to IRC server!");
		Thread t = new Thread(connect);
		t.setPriority(Thread.MAX_PRIORITY);
		t.setDaemon(false);
		t.start();
		sender.sendMessage("Successfully connected!");
		return true;
	}

	private Runnable connect = new Runnable() {
		@Override
		public void run() {
			MonsterIRC.getHandleManager().getIRCHandler()
					.connect(MonsterIRC.getIRCServer());
		}
	};

	@Override
	public String getPermission() {
		return "irc.connect";
	}

	@Override
	public String[] getHelp() {
		return new String[] {
				ColorUtils.RED.getMinecraftColor() + "Command:"
						+ ColorUtils.WHITE.getMinecraftColor() + "Connect",
				ColorUtils.RED.getMinecraftColor() + "Description:"
						+ ColorUtils.WHITE.getMinecraftColor()
						+ "Connects the bot to the IRC channel.",
				ColorUtils.RED.getMinecraftColor() + "Usage:"
						+ ColorUtils.WHITE.getMinecraftColor() + "/connect" };
	}

	@Override
	public String getCommandName() {
		return "Connect";
	}

}
