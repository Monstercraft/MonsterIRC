package org.monstercraft.irc.plugin.managers;

import java.util.LinkedList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.command.gamecommands.Ban;
import org.monstercraft.irc.plugin.command.gamecommands.Connect;
import org.monstercraft.irc.plugin.command.gamecommands.Disconnect;
import org.monstercraft.irc.plugin.command.gamecommands.Help;
import org.monstercraft.irc.plugin.command.gamecommands.Join;
import org.monstercraft.irc.plugin.command.gamecommands.Leave;
import org.monstercraft.irc.plugin.command.gamecommands.Mute;
import org.monstercraft.irc.plugin.command.gamecommands.Nick;
import org.monstercraft.irc.plugin.command.gamecommands.PrivateMessage;
import org.monstercraft.irc.plugin.command.gamecommands.ReloadConfig;
import org.monstercraft.irc.plugin.command.gamecommands.Reply;
import org.monstercraft.irc.plugin.command.gamecommands.Say;
import org.monstercraft.irc.plugin.command.gamecommands.SendRaw;
import org.monstercraft.irc.plugin.command.gamecommands.Unmute;
import org.monstercraft.irc.plugin.command.irccommand.Announce;
import org.monstercraft.irc.plugin.command.irccommand.Commands;
import org.monstercraft.irc.plugin.command.irccommand.List;
import org.monstercraft.irc.plugin.command.irccommand.Other;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

/**
 * This class manages all of the plugins commands.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class CommandManager extends MonsterIRC {

	private LinkedList<GameCommand> gameCommands = new LinkedList<GameCommand>();

	private LinkedList<IRCCommand> IRCCommands = new LinkedList<IRCCommand>();

	/**
	 * Creates an instance
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public CommandManager() {
		gameCommands.add(new Help());
		gameCommands.add(new SendRaw());
		gameCommands.add(new Reply());
		gameCommands.add(new PrivateMessage());
		gameCommands.add(new Ban());
		gameCommands.add(new Mute());
		gameCommands.add(new Unmute());
		gameCommands.add(new Connect());
		gameCommands.add(new Disconnect());
		gameCommands.add(new Join());
		gameCommands.add(new Leave());
		gameCommands.add(new Nick());
		gameCommands.add(new Say());
		gameCommands.add(new ReloadConfig());
		IRCCommands.add(new List());
		IRCCommands.add(new Announce());
		IRCCommands.add(new Commands());
		if (Variables.ingamecommands) {
			IRCCommands.add(new Other());
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
			for (GameCommand c : gameCommands) {
				if (c.canExecute(sender, split)) {
					Variables.commandsGame++;
					try {
						if (split.length > 2) {
							if (c instanceof Help) {
								return ((Help) c).CommandHelp(gameCommands,
										sender, split);
							}
						}
						boolean b = c.execute(sender, split);
						if (!b) {
							if (sender instanceof ConsoleCommandSender) {
								Help.sendMenu((ConsoleCommandSender) sender);
							} else if (sender instanceof Player) {
								Help.sendMenu((Player) sender);
							}
						}
					} catch (Exception ex) {
						IRC.debug(ex);
					}
				}
			}
		} else {
			if (sender instanceof ConsoleCommandSender) {
				Help.sendMenu((ConsoleCommandSender) sender);
			} else if (sender instanceof Player) {
				Help.sendMenu((Player) sender);
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
		for (IRCCommand c : IRCCommands) {
			if (c.canExecute(sender, arg, channel)) {
				try {
					Variables.commandsIRC++;
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
}