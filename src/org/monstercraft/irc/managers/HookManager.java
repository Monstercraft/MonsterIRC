package org.monstercraft.irc.managers;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.hooks.HeroChatHook;
import org.monstercraft.irc.hooks.PermissionsHook;
import org.monstercraft.irc.hooks.mcMMOHook;

import com.gmail.nossr50.mcMMO;
import com.herocraftonline.dthielke.herochat.HeroChat;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * This class manages all of the hooks used within the plugin.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class HookManager {

	private mcMMOHook mcmmo = null;
	private PermissionsHook permissions = null;
	private HeroChatHook herochat = null;

	/**
	 * Creates an instance of the HookManager class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public HookManager(final IRC plugin) {
		mcmmo = new mcMMOHook(plugin);
		permissions = new PermissionsHook(plugin);
		herochat = new HeroChatHook(plugin);
	}

	/**
	 * Fetches the HeroChat hook.
	 * 
	 * @return The hook into HeroChat.
	 */
	public HeroChat getHeroChatHook() {
		return herochat.getHook();
	}

	/**
	 * Fetches the mcMMO hook.
	 * 
	 * @return The hook into mcMMO.
	 */
	public mcMMO getmcMMOHook() {
		return mcmmo.getHook();
	}

	/**
	 * Creates a new hook into the plugin.
	 * 
	 * @param hook
	 *            A hook into HeroChat.
	 * @return The new HeroChatHook.
	 */
	public HeroChatHook setHeroChatHook(final HeroChatHook hook) {
		return herochat = hook;
	}

	/**
	 * Fetches the Permissions hook.
	 * 
	 * @return The hook into Permissions.
	 */
	public Permissions getPermissionsHook() {
		return permissions.getHook();
	}

	/**
	 * Creates a new hook into the plugin.
	 * 
	 * @param hook
	 *            A hook into mcMMO.
	 * @return The new mcMMOHook.
	 */
	public mcMMOHook setmcMMOHook(final mcMMOHook hook) {
		return mcmmo = hook;
	}

	/**
	 * Creates a new hook into the plugin.
	 * 
	 * @param hook
	 *            A hook into Permissions.
	 * @return The new PermissionsHook.
	 */
	public PermissionsHook setPermissionsHook(final PermissionsHook hook) {
		return permissions = hook;
	}

}
