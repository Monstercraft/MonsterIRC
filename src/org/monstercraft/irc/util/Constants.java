package org.monstercraft.irc.util;

import java.io.File;

/**
 * This class holds all of the constants used within the plugin.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class Constants {

	/**
	 * The name of the muted users file.
	 */
	public static final String MUTED_FILE = "Muted.txt";

	/**
	 * The location in which to save the files.
	 */
	public static final String SETTINGS_PATH = "plugins" + File.separator
			+ "MonsterIRC" + File.separator;

	/**
	 * The configuration file.
	 */
	public static final String SETTINGS_FILE = "Config.yml";

	/**
	 * The Channel Directory.
	 */
	public static final String CHANNELS_PATH = "Channels" + File.separator;

	public static final String FORMAT_FILE = "Format.txt";

}
