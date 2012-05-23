package org.monstercraft.irc.ircplugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.util.ChatType;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.util.StringUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

import com.gmail.nossr50.mcPermissions;
import com.palmergames.bukkit.towny.TownyMessaging;

public class IRC {

	private final static Logger logger = Logger.getLogger(IRC.class
			.getSimpleName());

	/**
	 * Fetches the logger.
	 * 
	 * @return The logger.
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * Logs a message to the console.
	 * 
	 * @param msg
	 *            The message to print.
	 */
	public static void log(final String msg) {
		logger.log(Level.INFO, "[MonsterIRC] " + msg);
	}

	/**
	 * Logs debugging messages to the console.
	 * 
	 * @param error
	 *            The message to print.
	 */
	public static void debug(final String error, final boolean console) {
		if (console) {
			logger.log(Level.WARNING, "[MonsterIRC - Debug] " + error);
		}
	}

	/**
	 * Sends a message to the MonsterIRC channel.
	 * 
	 * @param channel
	 *            The channel to send it to.
	 * @param message
	 *            The message to send.
	 */
	public static void sendMessage(final IRCChannel channel,
			final String message) {
		MonsterIRC.getHandleManager().getIRCHandler()
				.sendMessage(channel.getChannel(), message);
	}

	/**
	 * Sends a raw message to the IRC server.
	 * 
	 * @param RawMessage
	 *            The message to send.
	 */
	public static void sendRawMessage(final String RawMessage) {
		MonsterIRC.getHandleManager().getIRCHandler().sendRaw(RawMessage);
	}

	/**
	 * Sends a message to the MonsterIRC channel.
	 * 
	 * @param channel
	 *            The channel to send it to.
	 * @param message
	 *            The message to send.
	 */
	public static void kick(final String channel, final String nick,
			final String reason) {
		MonsterIRC.getHandleManager().getIRCHandler()
				.kick(MonsterIRC.getIRCServer(), nick, channel, reason);
	}

	/**
	 * Sends a message to the MonsterIRC channel.
	 * 
	 * @param channel
	 *            The channel to send it to.
	 * @param message
	 *            The message to send.
	 */
	public static void ban(final String channel, final String nick) {
		MonsterIRC.getHandleManager().getIRCHandler()
				.ban(MonsterIRC.getIRCServer(), nick, channel);
	}

	/**
	 * Sends a message to the MonsterIRC channel.
	 * 
	 * @param channel
	 *            The channel to send it to.
	 * @param message
	 *            The message to send.
	 */
	public static void mode(final String channel, final String nick,
			final String mode) {
		MonsterIRC.getHandleManager().getIRCHandler()
				.mode(MonsterIRC.getIRCServer(), nick, channel, mode);
	}

	/**
	 * Sends a message to the MonsterIRC channel.
	 * 
	 * @param to
	 *            The person to send the message to.
	 * @param message
	 *            The message to send.
	 */
	public static void sendMessage(final String to, final String message) {
		MonsterIRC.getHandleManager().getIRCHandler().sendMessage(to, message);
	}

	/**
	 * Sends a message to a user on the MonsterIRC server.
	 * 
	 * @param to
	 *            The user to send it to.
	 * @param message
	 *            The message to send.
	 */
	public static void sendNotice(final String to, final String message) {
		MonsterIRC.getHandleManager().getIRCHandler().sendNotice(to, message);
	}

	/**
	 * Logs debugging messages to the console.
	 * 
	 * @param error
	 *            The message to print.
	 */
	public static void debug(final Exception error) {
		logger.log(Level.SEVERE, "[MonsterIRC - Critical error detected!]");
		error.printStackTrace();
	}

	public static Plugin getPlugin() {
		return Bukkit.getServer().getPluginManager().getPlugin("MonsterIRC");
	}

	/**
	 * Handles a message accoradingly.
	 * 
	 * @param c
	 *            The IRCChannel to handle the message for.
	 * @param name
	 *            The sender's name.
	 * @param message
	 *            The message to handle.
	 */
	public static void sendGameMessage(final IRCChannel c, final String name,
			final String message) {
		if (!c.passToGame()) {
			return;
		}
		Variables.linesToGame++;
		try {
			if (c.getChatType() == ChatType.ADMINCHAT) {
				if (MonsterIRC.getHookManager().getmcMMOHook() != null) {
					String format = ColorUtils.CYAN.getMinecraftColor() + "{"
							+ ColorUtils.WHITE.getMinecraftColor() + "[IRC] "
							+ StringUtils.getPrefix(name)
							+ StringUtils.getDisplayName(name)
							+ StringUtils.getSuffix(name)
							+ ColorUtils.CYAN.getMinecraftColor() + "} "
							+ message;
					for (Player p : getPlugin().getServer().getOnlinePlayers()) {
						if (p.isOp()
								|| mcPermissions.getInstance().adminChat(p))
							p.sendMessage(ColorUtils.formatIRCMessage(format));
					}
				}
			} else if (c.getChatType() == ChatType.HEROCHAT) {
				if (c.getHeroChatChannel() != null) {
					c.getHeroChatChannel().announce(
							ColorUtils.formatIRCMessage(Variables.mcformat
									.replace("&", "§")
									.replace("{name}",
											StringUtils.getDisplayName(name))
									.replace(
											"{HCchannelColor}",
											"§"
													+ c.getHeroChatChannel()
															.getColor()
															.getChar())
									.replace("{message}", message)
									.replace("{prefix}",
											StringUtils.getPrefix(name))
									.replace("{suffix}",
											StringUtils.getSuffix(name))
									.replace("{groupPrefix}",
											StringUtils.getGroupPrefix(name))
									.replace("{groupSuffix}",
											StringUtils.getGroupSuffix(name))
									.replace("{world}",
											StringUtils.getWorld(name))));
				} else {
					log("Invalid herochat channel detected for "
							+ c.getChannel());
				}
			} else if (c.getChatType() == ChatType.GLOBAL) {
				getPlugin().getServer()
						.broadcastMessage(
								ColorUtils.formatIRCMessage(Variables.mcformat
										.replace("&", "§")
										.replace(
												"{name}",
												StringUtils
														.getDisplayName(name))
										.replace(
												"{message}",
												ColorUtils.WHITE
														.getMinecraftColor()
														+ message)
										.replace("{prefix}",
												StringUtils.getPrefix(name))
										.replace("{suffix}",
												StringUtils.getSuffix(name))
										.replace(
												"{groupPrefix}",
												StringUtils
														.getGroupPrefix(name))
										.replace(
												"{groupSuffix}",
												StringUtils
														.getGroupSuffix(name))
										.replace("{world}",
												StringUtils.getWorld(name))));
			} else if (c.getChatType() == ChatType.TOWNYCHAT) {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (MonsterIRC.getHandleManager().getPermissionsHandler()
							.hasNode(p, c.getTownyNode())) {
						TownyMessaging.sendMsg(p, ColorUtils.formatIRCMessage(c
								.getTownyChannel().getChannelTag()
								.replace("&", "§")
								+ Variables.mcformat
										.replace("&", "§")
										.replace(
												"{name}",
												StringUtils
														.getDisplayName(name))
										.replace(
												"{message}",
												c.getTownyChannel()
														.getMessageColour()
														.replace("&", "§")
														+ message.replace("&",
																"§"))
										.replace("{prefix}",
												StringUtils.getPrefix(name))
										.replace("{suffix}",
												StringUtils.getSuffix(name))
										.replace(
												"{groupPrefix}",
												StringUtils
														.getGroupPrefix(name))
										.replace(
												"{groupSuffix}",
												StringUtils
														.getGroupSuffix(name))
										.replace("{world}",
												StringUtils.getWorld(name))));
					}
				}
			}
		} catch (Exception e) {
			IRC.debug(e);
		}
	}

	/**
	 * Fetches the list of Operaters in the current IRC channel.
	 * 
	 * @return The list of Operators.
	 */
	public static boolean isOp(final IRCChannel channel, final String sender) {
		return channel.getOpList().contains(sender);
	}

	/**
	 * Fetches the list of Operaters in the current IRC channel.
	 * 
	 * @return The list of Operators.
	 */
	public static boolean isHalfOP(final IRCChannel channel, final String sender) {
		return channel.getOpList().contains(sender);
	}

	/**
	 * Fetches the list of Admins in the current IRC channel.
	 * 
	 * @return True if the user is admin; otherwise false.
	 */
	public static boolean isAdmin(final IRCChannel channel, final String sender) {
		return channel.getOpList().contains(sender);
	}

	/**
	 * Fetches the list of Voices in the current IRC channel.
	 * 
	 * @return The list of Voices.
	 */
	public static boolean isVoice(final IRCChannel channel, final String sender) {
		return channel.getVoiceList().contains(sender);
	}

	public static boolean isVoicePlus(final IRCChannel channel,
			final String sender) {
		if (isVoice(channel, sender)) {
			return true;
		}
		if (isHalfOP(channel, sender)) {
			return true;
		}
		if (isAdmin(channel, sender)) {
			return true;
		}
		if (isOp(channel, sender)) {
			return true;
		}
		return false;
	}
}