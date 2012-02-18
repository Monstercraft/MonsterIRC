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
		String s = "";
		if (IRC.getHookManager().getChatHook() != null) {
			if (IRC.getHookManager().getChatHook()
					.getPlayerPrefix(getWorld(name), name) != null) {
				s = IRC.getHookManager().getChatHook()
						.getPlayerPrefix(getWorld(name), name)
						.replace("&", "§");

			}
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
		String s = "";
		if (IRC.getHookManager().getChatHook() != null) {
			if (IRC.getHookManager().getChatHook()
					.getPlayerSuffix(getWorld(name), name) != null) {
				s = IRC.getHookManager().getChatHook()
						.getPlayerSuffix(getWorld(name), name)
						.replace("&", "§");

			}
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
		return name.replace("&", "§");
	}

	/**
	 * Fetches the group suffix for the user.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The groups suffix.
	 */
	public static String getGroupSuffix(String name) {
		String s = "";
		if (IRC.getHookManager().getChatHook() != null) {
			if (IRC.getHookManager()
					.getChatHook()
					.getGroupSuffix(
							getWorld(name),
							IRC.getHookManager().getChatHook()
									.getPrimaryGroup(getWorld(name), name)) != null) {
				s = IRC.getHookManager()
						.getChatHook()
						.getGroupSuffix(
								getWorld(name),
								IRC.getHookManager().getChatHook()
										.getPrimaryGroup(getWorld(name), name))
						.replace("&", "§");

			}
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
		String s = "";
		if (IRC.getHookManager().getChatHook() != null) {
			if (IRC.getHookManager()
					.getChatHook()
					.getGroupPrefix(
							getWorld(name),
							IRC.getHookManager().getChatHook()
									.getPrimaryGroup(getWorld(name), name)) != null) {
				s = IRC.getHookManager()
						.getChatHook()
						.getGroupPrefix(
								getWorld(name),
								IRC.getHookManager().getChatHook()
										.getPrimaryGroup(getWorld(name), name))
						.replace("&", "§");

			}
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
