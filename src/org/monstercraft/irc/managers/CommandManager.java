package org.monstercraft.irc.managers;

import java.util.HashSet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.GameCommand;
import org.monstercraft.irc.command.IRCCommand;
import org.monstercraft.irc.command.gamecommands.Ban;
import org.monstercraft.irc.command.gamecommands.Connect;
import org.monstercraft.irc.command.gamecommands.Disconnect;
import org.monstercraft.irc.command.gamecommands.Mute;
import org.monstercraft.irc.command.gamecommands.Nick;
import org.monstercraft.irc.command.gamecommands.ReloadConfig;
import org.monstercraft.irc.command.gamecommands.Say;
import org.monstercraft.irc.command.gamecommands.Unmute;
import org.monstercraft.irc.command.irccommands.Announce;
import org.monstercraft.irc.command.irccommands.List;
import org.monstercraft.irc.command.irccommands.Other;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;

/**
 * This class manages all of the plugins commands.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class CommandManager extends IRC {

	private HashSet<GameCommand> gameCommands = new HashSet<GameCommand>();

	private HashSet<IRCCommand> IRCCommands = new HashSet<IRCCommand>();

	/**
	 * Creates an instance
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public CommandManager(final IRC plugin) {
		gameCommands.add(new Ban(plugin));
		gameCommands.add(new Mute(plugin));
		gameCommands.add(new Unmute(plugin));
		gameCommands.add(new Connect(plugin));
		gameCommands.add(new Disconnect(plugin));
		gameCommands.add(new Nick(plugin));
		gameCommands.add(new Say(plugin));
		gameCommands.add(new ReloadConfig(plugin));
		IRCCommands.add(new Announce(plugin));
		IRCCommands.add(new List(plugin));
		if (Variables.ingamecommands) {
			IRCCommands.add(new Other(plugin));
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
					c.execute(sender, split);
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
		for (IRCCommand c : IRCCommands) {
			if (c.canExecute(sender, arg, channel)) {
				c.execute(sender, arg, channel);
				return true;
			}
		}
		log("Invalid IRC command");
		return false;
	}

}