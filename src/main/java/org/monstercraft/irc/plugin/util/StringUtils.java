package org.monstercraft.irc.plugin.util;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.Configuration;

public class StringUtils {

	/**
	 * Fetches the users prefix.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The users prefix.
	 */
	public static String getPrefix(String name) {
		try {
			String s = "";
			if (MonsterIRC.getHookManager() != null) {
				if (MonsterIRC.getHookManager().getChatHook() != null) {
					if (MonsterIRC.getHookManager().getChatHook().isEnabled()) {
						if (MonsterIRC.getHookManager().getChatHook()
								.getPlayerPrefix("", name) != null) {
							s = MonsterIRC.getHookManager().getChatHook()
									.getPlayerPrefix("", name);
						}
					}
				}
			}
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Fetches the users suffix.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The users suffix.
	 */
	public static String getSuffix(String name) {
		try {
			String s = "";
			if (MonsterIRC.getHookManager() != null) {
				if (MonsterIRC.getHookManager().getChatHook() != null) {
					if (MonsterIRC.getHookManager().getChatHook()
							.getPlayerSuffix("", name) != null) {
						s = MonsterIRC.getHookManager().getChatHook()
								.getPlayerSuffix("", name);

					}
				}
			}
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Fetches the special name of the user.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The users name.
	 */
	public static String getDisplayName(String name) {
		return name.trim();
	}

	/**
	 * Fetches the group suffix for the user.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The groups suffix.
	 */
	public static String getGroupSuffix(String name) {
		try {
			String s = "";
			if (MonsterIRC.getHookManager() != null) {
				if (MonsterIRC.getHookManager().getChatHook() != null) {
					if (MonsterIRC
							.getHookManager()
							.getChatHook()
							.getGroupSuffix(
									"",
									MonsterIRC.getHookManager().getChatHook()
											.getPrimaryGroup("", name)) != null) {
						s = MonsterIRC
								.getHookManager()
								.getChatHook()
								.getGroupSuffix(
										"",
										MonsterIRC.getHookManager()
												.getChatHook()
												.getPrimaryGroup("", name));

					}
				}
			}
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Fetches the group prefix for the user.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The groups prefix.
	 */
	public static String getGroupPrefix(String name) {
		try {
			String s = "";
			if (MonsterIRC.getHookManager() != null) {
				if (MonsterIRC.getHookManager().getChatHook() != null) {
					if (MonsterIRC
							.getHookManager()
							.getChatHook()
							.getGroupPrefix(
									"",
									MonsterIRC.getHookManager().getChatHook()
											.getPrimaryGroup("", name)) != null) {
						s = MonsterIRC
								.getHookManager()
								.getChatHook()
								.getGroupPrefix(
										"",
										MonsterIRC.getHookManager()
												.getChatHook()
												.getPrimaryGroup("", name));

					}
				}
			}
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Fetches the group prefix for the user.
	 * 
	 * @param name
	 *            The user's name to look up.
	 * @return The groups prefix.
	 */
	public static String getWorld(String name) {
		try {
			String s = "";
			if (Bukkit.getServer().getPlayer(name) != null) {
				s = Bukkit.getServer().getPlayer(name).getWorld().getName();
				if (!s.equalsIgnoreCase("")) {
					return s;
				}
			}
			return name;
		} catch (Exception e) {
			return "";
		}
	}

	public static String getMvWorldAlias(String name) {
		String s = "";
		if (Configuration.usingMultiverse()) {
			if (MonsterIRC.getHookManager().getMultiverseHook()
					.getMVWorldManager().getMVWorld(name.trim()) != null) {
				s = MonsterIRC.getHookManager().getMultiverseHook()
						.getMVWorldManager().getMVWorld(name.trim()).getAlias();
				if (!s.equalsIgnoreCase("")) {
					return s;
				}
			}
		}
		return s;
	}

	public static String getMvWorldColor(String name) {
		String s = "";
		if (Configuration.usingMultiverse()) {
			if (MonsterIRC.getHookManager().getMultiverseHook()
					.getMVWorldManager().getMVWorld(name.trim()) != null) {
				s = MonsterIRC.getHookManager().getMultiverseHook()
						.getMVWorldManager().getMVWorld(name.trim()).getColor()
						.toString();
				if (!s.equalsIgnoreCase("")) {
					return s;
				}
			}
		}
		return s;
	}

	public static String[] split(final int limit, String text) {
		ArrayList<String> splits = new ArrayList<String>();
		if (text.length() > limit) {
			while (text.length() > limit) {
				String sub = text.substring(0, limit);
				int lastSpace = sub.lastIndexOf(" ");
				text = text.substring(lastSpace + 1);
				splits.add(sub);
			}
			return (String[]) splits.toArray();
		}
		return new String[] { text };
	}
}
