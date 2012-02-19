package org.monstercraft.irc.plugin.managers;

import net.milkbowl.vault.chat.Chat;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.hooks.HeroChatHook;
import org.monstercraft.irc.plugin.hooks.TownyChatHook;
import org.monstercraft.irc.plugin.hooks.VaultChatHook;
import org.monstercraft.irc.plugin.hooks.VaultPermissionsHook;
import org.monstercraft.irc.plugin.hooks.mcMMOHook;

import com.gmail.nossr50.mcMMO;
import com.herocraftonline.dthielke.herochat.HeroChat;

/**
 * This class manages all of the hooks used within the plugin.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class HookManager {

	private mcMMOHook mcmmo = null;
	private VaultPermissionsHook permissions = null;
	private VaultChatHook chat = null;
	private HeroChatHook herochat = null;
	private TownyChatHook townychat = null;
	private final IRC plugin;

	/**
	 * Creates an instance of the HookManager class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public HookManager(final IRC plugin) {
		this.plugin = plugin;
		mcmmo = new mcMMOHook(plugin);
		permissions = new VaultPermissionsHook(plugin);
		chat = new VaultChatHook(plugin);
		herochat = new HeroChatHook(plugin);
		townychat = new TownyChatHook(plugin);
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
	public VaultPermissionsHook getPermissionsHook() {
		return permissions;
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
	 * @return The new mcMMOHook.
	 */
	public void setmcMMOHook() {
		mcmmo = new mcMMOHook(plugin);
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
