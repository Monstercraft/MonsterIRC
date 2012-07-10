package org.monstercraft.irc.plugin.util;

import org.bukkit.ChatColor;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration.Variables;

/**
 * This enum contains all of the supported colors.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public enum ColorUtils {
	DARK_BLUE("\u00032", ChatColor.DARK_BLUE.toString()), DARK_GREEN("\u00033",
			ChatColor.DARK_GREEN.toString()), RED("\u00034", ChatColor.DARK_RED
			.toString()), BROWN("\u00035", ChatColor.DARK_GRAY.toString()), PURPLE(
			"\u00036", ChatColor.DARK_PURPLE.toString()), OLIVE("\u00037",
			ChatColor.GOLD.toString()), YELLOW("\u00038", ChatColor.YELLOW
			.toString()), GREEN("\u00039", ChatColor.GREEN.toString()), TEAL(
			"\u000310", ChatColor.DARK_AQUA.toString()), CYAN("\u000311",
			ChatColor.AQUA.toString()), BLUE("\u000312", ChatColor.BLUE
			.toString()), MAGENTA("\u000313", ChatColor.LIGHT_PURPLE.toString()), DARK_GRAY(
			"\u000314", ChatColor.RED.toString()), LIGHT_GRAY("\u000315",
			ChatColor.GRAY.toString()), NORMAL("\u000f", ChatColor.RESET
			.toString()), WHITE("\u000300", ChatColor.WHITE.toString()), WHITE2(
			"\u00030", ChatColor.WHITE.toString()), BLACK("\u00031",
			ChatColor.BLACK.toString()), BOLD("\u0002", ChatColor.BOLD
			.toString()), UNDERLINE("\u001f", ChatColor.UNDERLINE.toString()), ITALIC(
			"\u001D", ChatColor.ITALIC.toString());

	/**
	 * Colors in minecraft and IRC.
	 * 
	 * @param IRCColor
	 *            The color code in IRC.
	 * @param MinecraftColor
	 *            The color code in Minecraft.
	 */
	ColorUtils(String IRCColor, String MinecraftColor) {
		this.IRCColor = IRCColor;
		this.MinecraftColor = MinecraftColor;
	}

	/**
	 * Fetches the color in minecraft.
	 * 
	 * @return The minecraft color code.
	 */
	public String getMinecraftColor() {
		return MinecraftColor;
	}

	/**
	 * Fetches the color in IRC.
	 * 
	 * @return The IRC color code.
	 */
	public String getIRCColor() {
		return IRCColor;
	}

	/**
	 * Creates a formatted message with proper colors.
	 * 
	 * @param message
	 *            The inital message to format.
	 * @return The formatted message.
	 */
	public static String formatIRCtoGame(final String message, final String main) {
		int index = find(message, main);
		String msg = resolve(replace(message), main, index);
		if (Variables.colors) {
			for (ColorUtils c : values()) {
				msg = msg.replace(c.getIRCColor(), c.getMinecraftColor());
			}
		} else {
			for (ColorUtils c : values()) {
				msg = msg.replace(c.getIRCColor(), "");
			}
		}
		IRC.log(ChatColor.stripColor(msg));
		return msg;
	}

	/**
	 * Creates a formatted message with proper colors.
	 * 
	 * @param message
	 *            The inital message to format.
	 * @return The formatted message.
	 */
	public static String formatGametoIRC(final String message) {
		String msg = replace(message);
		if (Variables.colors) {
			for (ColorUtils c : values()) {
				msg = msg.replace(c.getMinecraftColor(), c.getIRCColor());
			}
		} else {
			msg = ChatColor.stripColor(msg);
		}
		msg = resolve(msg.replace(WHITE.getIRCColor(), NORMAL.getIRCColor()));
		return msg;
	}

	private static int find(String message, String main) {
		return message.indexOf(main);
	}

	private static String resolve(String main, String replacement, int index) {
		char[] chars = main.toCharArray();
		for (int i = index; i < index + replacement.length(); i++) {
			chars[i] = replacement.charAt(i - index);
		}
		return String.valueOf(chars);
	}

	private static String replace(String input) {
		return input.replace("&", "�");
	}

	private static String resolve(String input) {
		return input.replace("�", "&");
	}

	private final String IRCColor;

	private final String MinecraftColor;

}