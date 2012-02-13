package org.monstercraft.irc.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.GameCommand;
import org.monstercraft.irc.util.ChatType;
import org.monstercraft.irc.util.IRCColor;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;

import com.gmail.nossr50.mcPermissions;

public class Say extends GameCommand {

	public Say(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(CommandSender sender, String[] split) {
		return IRC.getHandleManager().getIRCHandler()
				.isConnected(IRC.getIRCServer())
				&& split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("say");
	}

	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (IRC.getHandleManager().getPermissionsHandler() != null) {
				if (!IRC.getHandleManager().getPermissionsHandler()
						.hasCommandPerms(((Player) sender), this)) {
					sender.sendMessage("[IRC] You don't have permission to preform that command.");
					return false;
				}
			} else {
				sender.sendMessage("[IRC] PEX not detected, unable to run any IRC commands.");
				return false;
			}
		}
		if (split.length <= 2) {
			sender.sendMessage("Invalid usage!");
			sender.sendMessage("Proper usage: irc say -c:[irc channel] [message]");
			sender.sendMessage("or");
			sender.sendMessage("Proper usage: irc say [message]");
			return false;
		}
		String channel = null;
		int j = 2;
		if (split[2].startsWith("-c:")) {
			String s = split[2].toString();
			channel = s.substring(3);
			j = 3;
		}
		StringBuffer result = new StringBuffer();
		StringBuffer result2 = new StringBuffer();
		result.append("<" + sender.getName() + "> ");
		for (int i = j; i < split.length; i++) {
			result.append(split[i]);
			result.append(" ");
			result2.append(split[i]);
			result2.append(" ");
		}

		for (IRCChannel c : Variables.channels) {
			if (channel != null) {
				if (c.getChannel().equalsIgnoreCase(channel)) {
					IRC.getHandleManager().getIRCHandler()
							.sendMessage(result.toString(), c.getChannel());
					handleMessage(c, sender.getName(), result2.toString());
					break;
				}
			} else {
				if (c.isDefaultChannel()) {
					IRC.getHandleManager().getIRCHandler()
							.sendMessage(result.toString(), c.getChannel());
					handleMessage(c, sender.getName(), result2.toString());
				}
			}
		}
		return false;
	}

	private void handleMessage(IRCChannel c, String name, String message) {
		if (c.getChatType() == ChatType.ADMINCHAT) {
			if (IRC.getHookManager().getmcMMOHook() != null) {
				String format = "§b" + "{" + "§f" + "[IRC] " + name + "§b"
						+ "} " + message;
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if (p.isOp() || mcPermissions.getInstance().adminChat(p))
						p.sendMessage(format);
				}
			}
		} else if (c.getChatType() == ChatType.HEROCHAT && !Variables.hc4) {
			c.getHeroChatChannel().announce(
					Variables.mcformat
							.replace("{name}", getName(name))
							.replace("{message}",
									IRCColor.formatIRCMessage(message))
							.replace("{prefix}", getPrefix(name))
							.replace("{suffix}", getSuffix(name))
							.replace("{groupPrefix}", getGroupPrefix(name))
							.replace("{groupSuffix}", getGroupSuffix(name))
							.replace("&", "§")
							+ c.getHeroChatChannel().getColor());
		} else if (c.getChatType() == ChatType.HEROCHAT
				&& IRC.getHookManager().getHeroChatHook() != null
				&& Variables.hc4) {
			c.getHeroChatFourChannel().sendMessage(
					Variables.mcformat.replace("{name}", getName(name))
							.replace("{message}", "").replace("{colon}", "")
							.replace("{prefix}", getPrefix(name))
							.replace("{suffix}", getSuffix(name))
							.replace("{groupPrefix}", getGroupPrefix(name))
							.replace("{groupSuffix}", getGroupSuffix(name))
							.replace("&", "§"),
					IRCColor.formatIRCMessage(IRCColor
							.formatIRCMessage(message)),
					c.getHeroChatFourChannel().getMsgFormat(), false);
		} else if (c.getChatType() == ChatType.GLOBAL) {
			plugin.getServer().broadcastMessage(
					Variables.mcformat
							.replace("{name}", getName(name))
							.replace("{message}",
									IRCColor.formatIRCMessage(message))
							.replace("{prefix}", getPrefix(name))
							.replace("{suffix}", getSuffix(name))
							.replace("{groupPrefix}", getGroupPrefix(name))
							.replace("{groupSuffix}", getGroupSuffix(name))
							.replace("&", "§")
							+ "§f");
		}
	}

	/**
	 * Fetches the users prefix.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The users prefix.
	 */
	private String getPrefix(String name) {
		StringBuilder sb = new StringBuilder();
		String s = "";
		if (IRC.getHookManager().getChatHook() != null) {
			String prefix = IRC.getHookManager().getChatHook()
					.getPlayerPrefix("", name);
			sb.append(prefix);
			String temp = sb.toString();
			s = temp.replace("&", "§");
		}
		return s;
	}

	/**
	 * Fetches the users suffix.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The users suffix.
	 */
	private String getSuffix(String name) {
		StringBuilder sb = new StringBuilder();
		String s = "";
		if (IRC.getHookManager().getChatHook() != null) {
			String suffix = IRC.getHookManager().getChatHook()
					.getPlayerSuffix("", name);
			sb.append(suffix);
			String temp = sb.toString();
			s = temp.replace("&", "§");
		}
		return s;
	}

	/**
	 * Fetches the special name of the user.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The users name.
	 */
	private String getName(String name) {
		StringBuilder sb = new StringBuilder();
		String s = name;
		if (IRC.getHookManager().getChatHook() != null) {
			String color = name;
			sb.append(color);
			String temp = sb.toString();
			s = temp.replace("&", "§");
		}
		return s;
	}

	/**
	 * Fetches the group suffix for the user.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The groups suffix.
	 */
	private String getGroupSuffix(String name) {
		StringBuilder sb = new StringBuilder();
		String s = "";
		if (IRC.getHookManager().getChatHook() != null) {
			String prefix = IRC
					.getHookManager()
					.getChatHook()
					.getGroupSuffix(
							"",
							IRC.getHookManager().getChatHook()
									.getPrimaryGroup("", name));
			sb.append(prefix);
			String temp = sb.toString();
			s = temp.replace("&", "§");
		}
		return s;
	}

	/**
	 * Fetches the group prefix for the user.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The groups prefix.
	 */
	private String getGroupPrefix(String name) {
		StringBuilder sb = new StringBuilder();
		String s = "";
		if (IRC.getHookManager().getChatHook() != null) {
			String prefix = IRC
					.getHookManager()
					.getChatHook()
					.getGroupPrefix(
							"",
							IRC.getHookManager().getChatHook()
									.getPrimaryGroup("", name));
			sb.append(prefix);
			String temp = sb.toString();
			s = temp.replace("&", "§");
		}
		return s;
	}

	@Override
	public String getPermissions() {
		return "irc.say";
	}

}
