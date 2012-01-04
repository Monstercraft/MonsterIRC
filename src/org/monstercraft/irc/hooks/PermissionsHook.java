package org.monstercraft.irc.hooks;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.handlers.PermissionsHandler;

import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissionsHook {

	public Permissions PermissionsHook;
	private IRC plugin;

	public PermissionsHook(IRC plugin) {
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
			plugin.log("Permissions not detected.");
			PermissionsHook = null;
			return;
		}

		PermissionsHook = ((Permissions) PermissionsPlugin);
		plugin.log("Permissions detected; hooking: "
				+ ((Permissions) PermissionsPlugin).getDescription()
						.getFullName());
		if (PermissionsHook != null) {
			if (PermissionsHook.isEnabled()) {
				plugin.permissionManager = PermissionsHook.getHandler();
				plugin.perms = new PermissionsHandler(plugin.permissionManager,
						plugin);
			}
		}
	}

}
