package org.monstercraft.irc.plugin.managers;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.managers.hooks.MultiverseHook;
import org.monstercraft.irc.plugin.managers.hooks.TownyChatHook;
import org.monstercraft.irc.plugin.managers.hooks.VaultChatHook;
import org.monstercraft.irc.plugin.managers.hooks.VaultPermissionsHook;

import com.onarandombox.MultiverseCore.MultiverseCore;

/**
 * This class manages all of the hooks used within the plugin.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class HookManager {

	private VaultPermissionsHook permissions = null;
	private VaultChatHook chat = null;
	private TownyChatHook townychat = null;
	private MultiverseHook mv = null;
	private final MonsterIRC plugin;

	/**
	 * Creates an instance of the HookManager class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public HookManager(final MonsterIRC plugin) {
		this.plugin = plugin;
		permissions = new VaultPermissionsHook(plugin);
		chat = new VaultChatHook(plugin);
		townychat = new TownyChatHook(plugin);
		mv = new MultiverseHook(plugin);
	}

	/**
	 * Fetches the mcMMO hook.
	 * 
	 * @return The hook into mcMMO.
	 */
	public MultiverseCore getMultiverseHook() {
		return mv.getHook();
	}

	/**
	 * Fetches the Permissions hook.
	 * 
	 * @return The hook into Permissions.
	 */
	public Permission getPermissionsHook() {
		return permissions.getHook();
	}

	/**
	 * Fetches the chat hook.
	 * 
	 * @return The hook into chat by vault.
	 */
	public Chat getChatHook() {
		return chat.getHook();
	}

	/**
	 * Fetches the PermissionsEx hook.
	 * 
	 * @return The hook into PermissionsEx.
	 */
	public com.palmergames.bukkit.TownyChat.Chat getTownyChatHook() {
		return townychat.getHook();
	}

	/**
	 * Creates a new hook into the plugin.
	 * 
	 * @return The new PermissionsHook.
	 */
	public void setPermissionsHook() {
		permissions = new VaultPermissionsHook(plugin);
	}

	/**
	 * Creates a new hook into the plugin.
	 * 
	 * @return The new chatHook.
	 */
	public void setChatHook() {
		chat = new VaultChatHook(plugin);
	}

	/**
	 * Creates a new hook into the plugin.
	 * 
	 * @param hook
	 *            A hook into townychat.
	 * @return The new TownyChatHook.
	 */
	public void setTownyChatHook() {
		townychat = new TownyChatHook(plugin);
	}

}