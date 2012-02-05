package org.monstercraft.irc.hooks;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.IRC;

import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class PermissionsExHook extends IRC {

	private PermissionsEx PermissionsExHook;
	private IRC plugin;

	/**
	 * Creates an instance of the PermissionsHook class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public PermissionsExHook(final IRC plugin) {
		this.plugin = plugin;
		initPermissionsHook();
	}

	private void initPermissionsHook() {
		if (PermissionsExHook != null) {
			return;
		}
		Plugin PermissionsPlugin = plugin.getServer().getPluginManager()
				.getPlugin("PermissionsEx");

		if (PermissionsPlugin == null) {
			log("Permissions not detected.");
			PermissionsExHook = null;
			return;
		}

		PermissionsExHook = ((PermissionsEx) PermissionsPlugin);
		log("PermissionsEx detected; hooking: "
				+ ((PermissionsEx) PermissionsPlugin).getDescription()
						.getFullName());
	}

	/**
	 * Fetches the hook into Permissions.
	 * 
	 * @return The hook into Permissions.
	 */
	public PermissionsEx getHook() {
		return PermissionsExHook;
	}

}
