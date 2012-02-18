package org.monstercraft.irc.plugin.managers;

import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.monstercraft.irc.IRC;
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
import org.monstercraft.irc.plugin.command.irccommand.List;
import org.monstercraft.irc.plugin.command.irccommand.ListCommands;
import org.monstercraft.irc.plugin.command.irccommand.Other;
import org.monstercraft.irc.plugin.util.Variables;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

/**
 * This class manages all of the plugins commands.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class CommandManager extends IRC {

	private Hashtable<Integer, GameCommand> gameCommands = new Hashtable<Integer, GameCommand>();

	private Hashtable<Integer, IRCCommand> IRCCommands = new Hashtable<Integer, IRCCommand>();

	/**
	 * Creates an instance
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public CommandManager(final IRC plugin) {
		gameCommands.put(11, new PrivateMessage(plugin));
		gameCommands.put(10, new Ban(plugin));
		gameCommands.put(9, new Mute(plugin));
		gameCommands.put(8, new Unmute(plugin));
		gameCommands.put(7, new Connect(plugin));
		gameCommands.put(6, new Disconnect(plugin));
		gameCommands.put(5, new Join(plugin));
		gameCommands.put(4, new Leave(plugin));
		gameCommands.put(3, new Nick(plugin));
		gameCommands.put(2, new Say(plugin));
		gameCommands.put(1, new ReloadConfig(plugin));
		IRCCommands.put(4, new Announce(plugin));
		IRCCommands.put(3, new ListCommands(plugin));
		IRCCommands.put(2, new List(plugin));
		if (Variables.ingamecommands) {
			IRCCommands.put(1, new Other(plugin));
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
						c.execute(sender, split);
					} catch (Exception ex) {
						debug(ex);
					}
					return true;
				}
			}
		}
		return false;
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
					debug(ex);
				}
				return true;
			}
		}
		log("Invalid IRC command");
		return false;
	}

}