package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;

public class Help extends GameCommand {

	@Override
	public String getPermission() {
		return "irc.help";
	}

	@Override
	public boolean canExecute(CommandSender sender, String[] split) {
		return split[0].contains("irc") && split[1].equalsIgnoreCase("help");
	}

	@Override
	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (MonsterIRC.getHandleManager().getPermissionsHandler() != null) {
				if (!MonsterIRC.getHandleManager().getPermissionsHandler()
						.hasCommandPerms(((Player) sender), this)) {
					sender.sendMessage("[IRC] You don't have permission to preform that command.");
					return true;
				} else {
					sendMenu(sender);
					return true;
				}
			}
			sender.sendMessage("[IRC]  No permissions loaded!");
			return true;
		} else {
			sendMenu((ConsoleCommandSender) sender);
			return true;
		}
	}

	public void sendMenu(CommandSender sender) {
		sender.sendMessage(ColorUtils.BLUE.getMinecraftColor() + "----- ["
				+ ColorUtils.WHITE + "MonsterIRC Help" + ColorUtils.BLUE
				+ "]-----");
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Connect())) {
			sender.sendMessage(ColorUtils.GREEN + "/irc connect ");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Disconnect())) {
			sender.sendMessage(ColorUtils.GREEN + "/irc disconnect");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Join())) {
			sender.sendMessage(ColorUtils.GREEN + "/irc join"
					+ ColorUtils.DARK_GRAY + " (channel)");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Leave())) {
			sender.sendMessage(ColorUtils.GREEN + "/irc leave"
					+ ColorUtils.DARK_GRAY + " (channel)");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Ban())) {
			sender.sendMessage(ColorUtils.GREEN + "/irc ban"
					+ ColorUtils.DARK_GRAY + " (user)");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Mute())) {
			sender.sendMessage(ColorUtils.GREEN + "/irc mute"
					+ ColorUtils.DARK_GRAY + " (user)");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Unmute())) {
			sender.sendMessage(ColorUtils.GREEN + "/irc unmute"
					+ ColorUtils.DARK_GRAY + " (user)");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Nick())) {
			sender.sendMessage(ColorUtils.GREEN + "/irc nick"
					+ ColorUtils.DARK_GRAY + " (nick)");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Say())) {
			sender.sendMessage(ColorUtils.GREEN + "/irc say"
					+ ColorUtils.DARK_GRAY + " (message)");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new ReloadConfig())) {
			sender.sendMessage(ColorUtils.GREEN + "/irc reload");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new PrivateMessage())) {
			sender.sendMessage(ColorUtils.GREEN + "/irc pm"
					+ ColorUtils.DARK_GRAY + "(user) (message)");
		}
		sender.sendMessage(ColorUtils.YELLOW
				+ "For more info on a certian command type" + ColorUtils.WHITE
				+ "/irc help (command)");
	}

	private void sendMenu(ConsoleCommandSender sender) {
		sender.sendMessage("----- MonsterIRCs Commands ----");
		sender.sendMessage("irc connect - Connects the Bot to the IRC server.");
		sender.sendMessage("irc disconnect - Disconnects the Bot from the IRC server.");
		sender.sendMessage("irc join (channel) - Connects the Bot to the channel");
		sender.sendMessage("irc leave (channel) - Disconnects the bot from the channel.");
		sender.sendMessage("irc ban (user) - Kicks and Bans a user from the IRC channel if your bot has OP.");
		sender.sendMessage("irc mute (user) - Stops a IRC users chat from appearing ingame.");
		sender.sendMessage("irc unmute (user) - Allows a muted IRC users chat to appear ingame.");
		sender.sendMessage("irc nick (new nick) - Changes the IRC bots nickname in IRC for that session.");
		sender.sendMessage("irc say (message) - An alternate way to talk to people in IRC.");
		sender.sendMessage("irc reload - Reloads the configuration file.");
		sender.sendMessage("irc pm (user) (message) - PM a user in the IRC channel.");
	}

}
