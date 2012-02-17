package org.monstercraft.irc.util;

/**
 * This enum contains all of the supported colors.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public enum IRCColor {
	WHITE("\u000300", "f"), 
	BLACK("\u000301", "0"), 
	DARK_BLUE("\u000302", "1"), 
	DARK_GREEN("\u000303", "2"), 
	RED("\u000304", "c"), 
	BROWN("\u000305", "4"), 
	PURPLE("\u000306", "5"), 
	OLIVE("\u000307", "6"), 
	YELLOW("\u000308", "e"), 
	GREEN("\u000309", "a"), 
	TEAL("\u000310", "3"), 
	CYAN("\u000311", "b"), 
	BLUE("\u000312", "9"),
	MAGENTA("\u000313", "d"),
	DARK_GRAY("\u000314","8"), 
	LIGHT_GRAY("\u000315", "7"), 
	NORMAL("\u0003", "");

	/**
	 * Colors in minecraft and IRC.
	 * 
	 * @param IRCColor
	 *            The color code in IRC.
	 * @param MinecraftColor
	 *            The color code in Minecraft.
	 */
	IRCColor(String IRCColor, String MinecraftColor) {
		this.IRCColor = IRCColor;
		this.MinecraftColor = MinecraftColor;
	}

	/**
	 * Fetches the color in minecraft.
	 * 
	 * @return The minecraft color code.
	 */
	public String getMinecraftColor() {
		return "§" + MinecraftColor;
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
	public static String formatIRCMessage(final String message) {
		String msg = message;
		if (Variables.colors) {
			for (IRCColor c : values()) {
				if (msg.contains(c.getIRCColor())) {
					msg = msg.replace(c.getIRCColor(), c.getMinecraftColor());
				}
			}
		} else {
			for (IRCColor c : values()) {
				if (msg.contains(c.getIRCColor())) {
					msg = msg.replace(c.getIRCColor(), "");
				}
			}
		}
		return msg;
	}

	/**
	 * Creates a formatted message with proper colors.
	 * 
	 * @param message
	 *            The inital message to format.
	 * @return The formatted message.
	 */
	public static String formatMCMessage(final String message) {
		String msg = message;
		if (Variables.colors) {
			for (IRCColor c : values()) {
				if (msg.contains(c.getMinecraftColor())) {
					msg = msg.replace(c.getMinecraftColor(), c.getIRCColor());
				}
			}
		} else {
			for (IRCColor c : values()) {
				if (msg.contains(c.getMinecraftColor())) {
					msg = msg.replace(c.getMinecraftColor(), "");
				}
			}
		}
		return msg;
	}

	private final String IRCColor;

	private final String MinecraftColor;

}