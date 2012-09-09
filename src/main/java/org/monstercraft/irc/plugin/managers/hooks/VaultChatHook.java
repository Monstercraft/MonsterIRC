package org.monstercraft.irc.plugin.managers.hooks;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class VaultChatHook extends MonsterIRC {

    private Chat ChatHook;
    private final MonsterIRC plugin;

    /**
     * Creates an instance of the VaultChatHook class.
     * 
     * @param plugin
     *            The parent plugin.
     */
    public VaultChatHook(final MonsterIRC plugin) {
        this.plugin = plugin;
        final boolean b = setupChat();
        if (b) {
            final Plugin permsPlugin = plugin.getServer().getPluginManager()
                    .getPlugin(ChatHook.getName());
            if (ChatHook != null) {
                if (permsPlugin != null) {
                    IRC.log("Vault chat detected; hooking: "
                            + permsPlugin.getDescription().getFullName());
                } else {
                    IRC.log("Chat found!");
                }
            }
        } else {
            IRC.log("Could not hook into chat using vault! (no prefix's or suffix's this means)");
        }
    }

    private Boolean setupChat() {
        final RegisteredServiceProvider<Chat> chatProvider = plugin.getServer()
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
