package org.monstercraft.irc.plugin.command.irccommand;

import org.bukkit.command.CommandException;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.util.Variables;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCCommandSender;

public class Other extends IRCCommand {

	public Other(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(String sender, String message, IRCChannel channel) {
		return IRC.getHandleManager().getIRCHandler()
				.isConnected(IRC.getIRCServer());
	}

	@Override
	public boolean execute(String sender, String message, IRCChannel channel) {
		if (IRC.getHandleManager().getIRCHandler().isOp(channel, sender)) {
			if (channel.getOpCommands().contains("*")) {
				try {
					IRCCommandSender console = new IRCCommandSender(plugin,
							sender);
					plugin.getServer().dispatchCommand(
							console,
							message.substring(message
									.indexOf(Variables.commandPrefix) + 1));
					return true;
				} catch (CommandException e) {
					IRC.getHandleManager()
							.getIRCHandler()
							.sendMessage(
									sender
											+ ": Error executing ingame command! "
											+ e.toString(), sender);
				}
			} else if (!channel.getOpCommands().isEmpty()) {
				for (String s : channel.getOpCommands()) {
					String lol = message.substring(message
							.indexOf(Variables.commandPrefix) + 1);
					if (lol != null) {
						if (lol.toLowerCase().startsWith(s.toLowerCase())) {
							try {
								IRCCommandSender console = new IRCCommandSender(
										plugin, sender);
								plugin.getServer()
										.dispatchCommand(
												console,
												message.substring(message
														.indexOf(Variables.commandPrefix) + 1));
								return true;
							} catch (CommandException e) {
								IRC.getHandleManager()
										.getIRCHandler()
										.sendMessage(
												sender
														+ ": Error executing ingame command! "
														+ e.toString(), sender);
							}
						}

					}
				}
				if (!channel.getVoiceCommands().isEmpty()) {
					for (String s : channel.getVoiceCommands()) {
						String lol = message.substring(message
								.indexOf(Variables.commandPrefix) + 1);
						if (lol != null) {
							if (lol.toLowerCase().startsWith(s.toLowerCase())) {
								try {
									IRCCommandSender console = new IRCCommandSender(
											plugin, sender);
									plugin.getServer()
											.dispatchCommand(
													console,
													message.substring(message
															.indexOf(Variables.commandPrefix) + 1));
									return true;
								} catch (CommandException e) {
									IRC.getHandleManager()
											.getIRCHandler()
											.sendMessage(
													sender
															+ ": Error executing ingame command! "
															+ e.toString(),
													sender);
								}
							}

						}
					}
				}
				if (!channel.getUserCommands().isEmpty()) {
					for (String s : channel.getUserCommands()) {
						String lol = message.substring(message
								.indexOf(Variables.commandPrefix) + 1);
						if (lol != null) {
							if (lol.toLowerCase().startsWith(s.toLowerCase())) {
								try {
									IRCCommandSender console = new IRCCommandSender(
											plugin, sender);
									plugin.getServer()
											.dispatchCommand(
													console,
													message.substring(message
															.indexOf(Variables.commandPrefix) + 1));
									return true;
								} catch (CommandException e) {
									IRC.getHandleManager()
											.getIRCHandler()
											.sendMessage(
													sender
															+ ": Error executing ingame command! "
															+ e.toString(),
													sender);
								}
							}

						}
					}
				}
				IRC.getHandleManager()
						.getIRCHandler()
						.sendNotice("You cannot use that command from IRC.",
								sender);
			} else {
				IRC.getHandleManager()
						.getIRCHandler()
						.sendNotice(
								"You are not allowed to execute that command from IRC.",
								sender);
				return true;
			}
		} else if (IRC.getHandleManager().getIRCHandler()
				.isVoice(channel, sender)) {
			if (channel.getVoiceCommands().contains("*")) {
				try {
					IRCCommandSender console = new IRCCommandSender(plugin,
							sender);
					plugin.getServer().dispatchCommand(
							console,
							message.substring(message
									.indexOf(Variables.commandPrefix) + 1));
					return true;
				} catch (CommandException e) {
					IRC.getHandleManager()
							.getIRCHandler()
							.sendMessage(
									sender
											+ ": Error executing ingame command! "
											+ e.toString(), sender);
				}
			} else if (!channel.getVoiceCommands().isEmpty()) {
				for (String s : channel.getVoiceCommands()) {
					String lol = message.substring(message
							.indexOf(Variables.commandPrefix) + 1);
					if (lol != null) {
						if (lol.toLowerCase().startsWith(s.toLowerCase())) {
							try {
								IRCCommandSender console = new IRCCommandSender(
										plugin, sender);
								plugin.getServer()
										.dispatchCommand(
												console,
												message.substring(message
														.indexOf(Variables.commandPrefix) + 1));
								return true;
							} catch (CommandException e) {
								IRC.getHandleManager()
										.getIRCHandler()
										.sendMessage(
												sender
														+ ": Error executing ingame command! "
														+ e.toString(), sender);
							}
						}

					}
				}
				if (!channel.getUserCommands().isEmpty()) {
					for (String s : channel.getUserCommands()) {
						String lol = message.substring(message
								.indexOf(Variables.commandPrefix) + 1);
						if (lol != null) {
							if (lol.toLowerCase().startsWith(s.toLowerCase())) {
								try {
									IRCCommandSender console = new IRCCommandSender(
											plugin, sender);
									plugin.getServer()
											.dispatchCommand(
													console,
													message.substring(message
															.indexOf(Variables.commandPrefix) + 1));
									return true;
								} catch (CommandException e) {
									IRC.getHandleManager()
											.getIRCHandler()
											.sendMessage(
													sender
															+ ": Error executing ingame command! "
															+ e.toString(),
													sender);
								}
							}

						}
					}
				}
				IRC.getHandleManager()
						.getIRCHandler()
						.sendNotice("You cannot use that command from IRC.",
								sender);
			} else {
				IRC.getHandleManager()
						.getIRCHandler()
						.sendNotice(
								"You are not allowed to execute that command from IRC.",
								sender);
				return true;
			}
		} else {
			if (channel.getUserCommands().contains("*")) {
				try {
					IRCCommandSender console = new IRCCommandSender(plugin,
							sender);
					plugin.getServer().dispatchCommand(
							console,
							message.substring(message
									.indexOf(Variables.commandPrefix) + 1));
					return true;
				} catch (CommandException e) {
					IRC.getHandleManager()
							.getIRCHandler()
							.sendMessage(
									sender
											+ ": Error executing ingame command! "
											+ e.toString(), sender);
				}
			} else if (!channel.getUserCommands().isEmpty()) {
				for (String s : channel.getUserCommands()) {
					String lol = message.substring(message
							.indexOf(Variables.commandPrefix) + 1);
					if (lol != null) {
						if (lol.toLowerCase().startsWith(s.toLowerCase())) {
							try {
								IRCCommandSender console = new IRCCommandSender(
										plugin, sender);
								plugin.getServer()
										.dispatchCommand(
												console,
												message.substring(message
														.indexOf(Variables.commandPrefix) + 1));
								return true;
							} catch (CommandException e) {
								IRC.getHandleManager()
										.getIRCHandler()
										.sendMessage(
												sender
														+ ": Error executing ingame command! "
														+ e.toString(), sender);
							}
						}

					}
				}
				IRC.getHandleManager()
						.getIRCHandler()
						.sendNotice("You cannot use that command from IRC.",
								sender);
			} else {
				IRC.getHandleManager()
						.getIRCHandler()
						.sendNotice(
								"You are not allowed to execute that command from IRC.",
								sender);
				return true;
			}
		}
		return false;
	}
}
