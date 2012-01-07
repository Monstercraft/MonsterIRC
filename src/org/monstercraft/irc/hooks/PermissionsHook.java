package org.monstercraft.irc.hooks;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.IRC;

import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class PermissionsHook extends IRC {

	private Permissions PermissionsHook;
	private IRC plugin;

	/**
	 * Creates an instance of the PermissionsHook class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public PermissionsHook(final IRC plugin) {
		this.plugin = plugin;
		initPermissionsHook();
	}

	private void initPermissionsHook() {
		if (PermissionsHook != null) {
			return;
		}
		Plugin PermissionsPlugin = plugin.getServer().getPluginManager()
				.getPlugin("Permissions");

		if (PermissionsPlugin == null) {
			log("Permissions not detected.");
			PermissionsHook = null;
			return;
		}

		PermissionsHook = ((Permissions) PermissionsPlugin);
		log("Permissions detected; hooking: "
				+ ((Permissions) PermissionsPlugin).getDescription()
						.getFullName());
	}

	/**
	 * Fetches the hook into Permissions.
	 * 
	 * @return The hook into Permissions.
	 */
	public Permissions getHook() {
		return PermissionsHook;
	}

}
