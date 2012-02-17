package org.monstercraft.irc.plugin.hooks;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.monstercraft.irc.IRC;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class VaultChatHook extends IRC {

	private Chat ChatHook;
	private IRC plugin;

	/**
	 * Creates an instance of the VaultChatHook class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public VaultChatHook(final IRC plugin) {
		this.plugin = plugin;
		boolean b = setupChat();
		if (b) {
			Plugin permsPlugin = plugin.getServer().getPluginManager()
					.getPlugin(ChatHook.getName());
			if (ChatHook != null) {
				if (permsPlugin != null) {
					log("Vault chat detected; hooking: "
							+ permsPlugin.getDescription().getFullName());
				} else {
					log("Chat found!");
				}
			}
		} else {
			log("Could not hook into chat using vault! (no prefix's or suffix's this means)");
		}
	}

	private Boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = plugin.getServer()
				.getServicesManager()
				.getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			ChatHook = chatProvider.getProvider();
		}

		return (ChatHook != null);
	}

	/**
	 * Fetches the hook into Permissions.
	 * 
	 * @return The hook into Permissions.
	 */
	public Chat getHook() {
		return ChatHook;
	}

}
