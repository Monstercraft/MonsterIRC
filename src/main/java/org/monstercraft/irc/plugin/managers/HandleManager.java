package org.monstercraft.irc.plugin.managers;

import net.milkbowl.vault.permission.Permission;

import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.handles.IRCHandler;
import org.monstercraft.irc.plugin.handles.IRCPermissionsHandler;
import org.monstercraft.irc.plugin.handles.IRCPluginHandler;

/**
 * This class contains all of the plugins handles.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class HandleManager {

	private IRCHandler irc = null;
	private IRCPermissionsHandler perms = null;
	private IRCPluginHandler ph = null;
	private final MonsterIRC plugin;

	/**
	 * Creates an instance of the Handle class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public HandleManager(final MonsterIRC plugin) {
		this.plugin = plugin;
		irc = new IRCHandler(plugin);
		perms = new IRCPermissionsHandler(MonsterIRC.getHookManager().getPermissionsHook());
		ph = new IRCPluginHandler(plugin);
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
	public IRCPermissionsHandler getPermissionsHandler() {
		return perms;
	}

	/**
	 * Fetches the plugin handler.
	 * 
	 * @return The PluginHandler.
	 */
	public IRCPluginHandler getPluginHandler() {
		return ph;
	}

	/**
	 * Sets the PermissionsHandler.
	 * 
	 * @param hook
	 *            The hook into Permissions.
	 * @return The new permissions Handler.
	 */
	public IRCPermissionsHandler setPermissionsHandler(final Permission hook) {
		if (hook != null) {
			return perms = new IRCPermissionsHandler(hook);
		}
		return perms;
	}

	/**
	 * Sets the new plugin handler.
	 * 
	 * @return The new plugin Handler.
	 */
	public void setIRCPluginHandler() {
		ph = new IRCPluginHandler(plugin);
	}

}
