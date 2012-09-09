package org.monstercraft.irc.plugin.managers.hooks;

import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;

import com.onarandombox.MultiverseCore.MultiverseCore;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class MultiverseHook extends MonsterIRC {

    private MultiverseCore mcMMOHook;
    private final MonsterIRC plugin;

    /**
     * Creates a hook into mcmmo.
     * 
     * @param plugin
     *            The IRC plugin.
     */
    public MultiverseHook(final MonsterIRC plugin) {
        this.plugin = plugin;
        initmcMMOHook();
    }

    /**
     * Hooks into mcmmo.
     */
    private void initmcMMOHook() {
        if (mcMMOHook != null) {
            return;
        }
        final Plugin mcMMOPlugin = plugin.getServer().getPluginManager()
                .getPlugin("Multiverse-Core");

        if (mcMMOPlugin == null) {
            IRC.log("Multiverse not detected.");
            mcMMOHook = null;
            return;
        }

        if (!mcMMOPlugin.isEnabled()) {
            IRC.log("Multiverse not enabled.");
            mcMMOHook = null;
            return;
        }

        mcMMOHook = ((MultiverseCore) mcMMOPlugin);
        IRC.log("Multiverse detected; hooking: "
                + ((MultiverseCore) mcMMOPlugin).getDescription().getFullName());
    }

    /**
     * Fetches the hook into mcMMO.
     * 
     * @return the hook into mcMMO.
     */
    public MultiverseCore getHook() {
        return mcMMOHook;
    }

}