package org.monstercraft.irc.managers;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.handlers.IRCHandler;
import org.monstercraft.irc.handlers.PermissionsHandler;

import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * This class contains all of the plugins handles.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class HandleManager {

	private IRCHandler IRC = null;;
	private PermissionsHandler perms = null;

	/**
	 * Creates an instance of the Handle class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public HandleManager(final IRC plugin) {
		IRC = new IRCHandler(plugin);
		if (org.monstercraft.irc.IRC.getHookManager().getPermissionsHook() != null) {
			if (org.monstercraft.irc.IRC.getHookManager().getPermissionsHook()
					.isEnabled()) {
				perms = new PermissionsHandler(org.monstercraft.irc.IRC
						.getHookManager().getPermissionsHook().getHandler());
			}
		}
	}

	/**
	 * Fetches the IRCHandler.
	 * 
	 * @return The IRCHandler.
	 */
	public IRCHandler getIRCHandler() {
		return IRC;
	}

	/**
	 * Fetches the permission handler.
	 * 
	 * @return The PermissionsHandler.
	 */
	public PermissionsHandler getPermissionsHandler() {
		return perms;
	}

	/**
	 * Sets the PermissionsHandler.
	 * 
	 * @param hook
	 *            The hook into Permissions.
	 * @return The new permissions Handler.
	 */
	public PermissionsHandler setPermissionsHandler(final Permissions hook) {
		if (hook != null) {
			if (hook.isEnabled()) {
				return perms = new PermissionsHandler(hook.getHandler());
			}
		}
		return perms;
	}

}
