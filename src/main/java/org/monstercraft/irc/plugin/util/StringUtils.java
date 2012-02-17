package org.monstercraft.irc.plugin.util;

import org.bukkit.Bukkit;
import org.monstercraft.irc.IRC;

public class StringUtils extends IRC {

	/**
	 * Fetches the users prefix.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The users prefix.
	 */
	public static String getPrefix(String name) {
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
	public static String getSuffix(String name) {
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
	public static String getName(String name) {
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
	public static String getGroupSuffix(String name) {
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
	public static String getGroupPrefix(String name) {
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

	/**
	 * Fetches the group prefix for the user.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The groups prefix.
	 */
	public static String getWorld(String name) {
		String s = "";
		if (Bukkit.getServer().getPlayer(name) != null) {
			s = Bukkit.getServer().getPlayer(name).getWorld().getName();
		}
		return s;
	}

}
