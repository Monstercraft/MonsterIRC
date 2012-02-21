package org.monstercraft.irc.plugin.managers;

import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.command.gamecommands.Ban;
import org.monstercraft.irc.plugin.command.gamecommands.Connect;
import org.monstercraft.irc.plugin.command.gamecommands.Disconnect;
import org.monstercraft.irc.plugin.command.gamecommands.Join;
import org.monstercraft.irc.plugin.command.gamecommands.Leave;
import org.monstercraft.irc.plugin.command.gamecommands.Mute;
import org.monstercraft.irc.plugin.command.gamecommands.Nick;
import org.monstercraft.irc.plugin.command.gamecommands.PrivateMessage;
import org.monstercraft.irc.plugin.command.gamecommands.ReloadConfig;
import org.monstercraft.irc.plugin.command.gamecommands.Say;
import org.monstercraft.irc.plugin.command.gamecommands.Unmute;
import org.monstercraft.irc.plugin.command.irccommand.Announce;
import org.monstercraft.irc.plugin.command.irccommand.Commands;
import org.monstercraft.irc.plugin.command.irccommand.List;
import org.monstercraft.irc.plugin.command.irccommand.Other;
import org.monstercraft.irc.plugin.util.Variables;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

/**
 * This class manages all of the plugins commands.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class CommandManager extends MonsterIRC {

	private Hashtable<Integer, GameCommand> gameCommands = new Hashtable<Integer, GameCommand>();

	private Hashtable<Integer, IRCCommand> IRCCommands = new Hashtable<Integer, IRCCommand>();

	/**
	 * Creates an instance
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public CommandManager(final MonsterIRC plugin) {
		gameCommands.put(11, new PrivateMessage());
		gameCommands.put(10, new Ban());
		gameCommands.put(9, new Mute());
		gameCommands.put(8, new Unmute());
		gameCommands.put(7, new Connect());
		gameCommands.put(6, new Disconnect());
		gameCommands.put(5, new Join());
		gameCommands.put(4, new Leave());
		gameCommands.put(3, new Nick());
		gameCommands.put(2, new Say());
		gameCommands.put(1, new ReloadConfig());
		IRCCommands.put(4, new Announce());
		IRCCommands.put(3, new Commands());
		IRCCommands.put(2, new List());
		if (Variables.ingamecommands) {
			IRCCommands.put(1, new Other());
		}
	}

	/**
	 * Executes a command that was ran in game or through the console.
	 * 
	 * @param sender
	 *            The command sender.
	 * @param command
	 *            The command.
	 * @param label
	 *            The commands label.
	 * @param args
	 *            The arguments in the command.
	 * @return True if the command executed successfully; Otherwise false.
	 */
	public boolean onGameCommand(final CommandSender sender,
			final Command command, final String label, final String[] args) {
		if (args.length > 0) {
			String[] split = new String[args.length + 1];
			split[0] = label;
			for (int a = 0; a < args.length; a++) {
				split[a + 1] = args[a];
			}
			for (Enumeration<Integer> e = gameCommands.keys(); e
					.hasMoreElements();) {
				int key = e.nextElement();
				GameCommand c = gameCommands.get(key);
				if (c.canExecute(sender, split)) {
					try {
						boolean b = c.execute(sender, split);
						if (!b) {
							if (sender instanceof ConsoleCommandSender) {
								sendMenu((ConsoleCommandSender) sender);
							} else if (sender instanceof CommandSender) {
								sendMenu(sender);
							}
						}
					} catch (Exception ex) {
						IRC.debug(ex);
					}
				}
			}
		} else {
			if (sender instanceof ConsoleCommandSender) {
				sendMenu((ConsoleCommandSender) sender);
			} else if (sender instanceof CommandSender) {
				sendMenu(sender);
			}
			return true;
		}
		return true;
	}

	/**
	 * Executes a command ran through IRC.
	 * 
	 * @param sender
	 *            The user that send the command.
	 * @param arg
	 *            The arguments in the command.
	 * @return True if the command executed successfully; Otherwise false.
	 */
	public boolean onIRCCommand(final String sender, final String arg,
			final IRCChannel channel) {
		for (Enumeration<Integer> e = IRCCommands.keys(); e.hasMoreElements();) {
			int key = e.nextElement();
			IRCCommand c = IRCCommands.get(key);
			if (c.canExecute(sender, arg, channel)) {
				try {
					c.execute(sender, arg, channel);
				} catch (Exception ex) {
					IRC.debug(ex);
				}
				return true;
			}
		}
		IRC.log("Invalid IRC command");
		return false;
	}

	public void sendMenu(CommandSender sender) {
		sender.sendMessage("----- MonsterIRCs Commands ----");
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Connect())) {
			sender.sendMessage("irc connect - Connects the Bot to the IRC server.");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Disconnect())) {
			sender.sendMessage("irc disconnect - Disconnects the Bot from the IRC server.");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Join())) {
			sender.sendMessage("irc join (channel) - Connects the Bot to the channel");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Leave())) {
			sender.sendMessage("irc leave (channel) - Disconnects the bot from the channel.");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Ban())) {
			sender.sendMessage("irc ban (user) - Kicks and Bans a user from the IRC channel if your bot has OP.");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Mute())) {
			sender.sendMessage("irc mute (user) - Stops a IRC users chat from appearing ingame.");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Unmute())) {
			sender.sendMessage("irc unmute (user) - Allows a muted IRC users chat to appear ingame.");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Nick())) {
			sender.sendMessage("irc nick (new nick) - Changes the IRC bots nickname in IRC for that session.");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new Say())) {
			sender.sendMessage("irc say (message) - An alternate way to talk to people in IRC.");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new ReloadConfig())) {
			sender.sendMessage("irc reload - Reloads the configuration file.");
		}
		if (MonsterIRC.getHandleManager().getPermissionsHandler()
				.hasCommandPerms((Player) sender, new PrivateMessage())) {
			sender.sendMessage("irc pm (user) (message) - PM a user in the IRC channel.");
		}
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