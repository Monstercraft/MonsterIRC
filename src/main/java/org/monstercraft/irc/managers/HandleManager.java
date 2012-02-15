package org.monstercraft.irc.managers;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.handlers.IRCHandler;
import org.monstercraft.irc.handlers.PermissionsHandler;
import org.monstercraft.irc.hooks.VaultPermissionsHook;

/**
 * This class contains all of the plugins handles.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class HandleManager {

	private IRCHandler irc = null;;
	private PermissionsHandler perms = null;

	/**
	 * Creates an instance of the Handle class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public HandleManager(final IRC plugin) {
		irc = new IRCHandler(plugin);
		perms = new PermissionsHandler(IRC.getHookManager()
				.getPermissionsHook().getHook());
	}

	/**
	 * Fetches the IRCHandler.
	 * 
	 * @return The IRCHandler.
	 */
	public IRCHandler getIRCHandler() {
		return irc;
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
	public PermissionsHandler setPermissionsHandler(final VaultPermissionsHook hook) {
		if (hook != null) {
			if (perms.isEnabled()) {
				return perms = new PermissionsHandler(hook.getHook());
			}
		}
		return perms;
	}

}
