package org.monstercraft.irc.plugin.towny;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.util.ChatType;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.util.StringUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

import com.gmail.nossr50.util.Users;
import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.CraftIRCHandler;
import com.palmergames.bukkit.TownyChat.TownyChatFormatter;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.channels.channelTypes;
import com.palmergames.bukkit.TownyChat.config.ChatSettings;
import com.palmergames.bukkit.TownyChat.listener.LocalTownyChatEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.util.BukkitTools;
import com.palmergames.util.StringMgmt;

@SuppressWarnings("deprecation")
public class TownyChatListener implements Listener {
    private static Chat plugin;
    private WeakHashMap<Player, String> directedChat = new WeakHashMap<Player, String>();

    public TownyChatListener(Chat paramChat) {
        plugin = paramChat;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(
            PlayerCommandPreprocessEvent paramPlayerCommandPreprocessEvent) {
        Player localPlayer = paramPlayerCommandPreprocessEvent.getPlayer();
        try {
            TownyUniverse.getDataSource().getResident(localPlayer.getName());
        } catch (NotRegisteredException localNotRegisteredException) {
            return;
        }
        String[] arrayOfString = paramPlayerCommandPreprocessEvent.getMessage()
                .split("\\ ");
        String str1 = arrayOfString[0].trim().toLowerCase().replace("/", "");
        String str2 = "";
        if (arrayOfString.length > 1)
            str2 = StringMgmt.join(StringMgmt.remFirstArg(arrayOfString), " ");
        Channel localChannel = plugin.getChannelsHandler().getChannel(
                localPlayer, str1);
        if (localChannel != null) {
            paramPlayerCommandPreprocessEvent.setMessage(str2);
            if (str2.isEmpty()) {
                if (plugin.getTowny().hasPlayerMode(localPlayer,
                        localChannel.getName()))
                    plugin.getTowny().removePlayerMode(localPlayer);
                else
                    plugin.getTowny().setPlayerMode(localPlayer,
                            new String[] { localChannel.getName() }, true);
            } else {
                directedChat.put(localPlayer, str1);
                localPlayer.chat(str2);
            }
            paramPlayerCommandPreprocessEvent.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(PlayerChatEvent paramPlayerChatEvent) {
        Player localPlayer = paramPlayerChatEvent.getPlayer();
        Object localObject1;
        localObject1 = plugin.getChannelsHandler().getChannel(localPlayer,
                channelTypes.GLOBAL);
        if (localObject1 != null) {
            for (IRCChannel c : MonsterIRC.getChannels()) {
                if (c.getChatType() == ChatType.TOWNYCHAT) {
                    handle(c, localPlayer, paramPlayerChatEvent.getMessage());
                }
            }
        }
    }

    public void handle(IRCChannel c, Player player, String message) {
        if (c.getChatType() == ChatType.TOWNYCHAT) {
            if (Bukkit.getServer().getPluginManager().getPlugin("mcMMO") != null) {
                if (Users.getProfile(player.getName()).getAdminChatMode()) {
                    return;
                }
                if (Users.getProfile(player.getName()).getPartyChatMode()) {
                    return;
                }
            }
            String msg = (Variables.ircformat
                    .replace("{HCchannelColor}", "")
                    .replace("{heroChatTag}", "")
                    .replace("{prefix}",
                            StringUtils.getPrefix(player.getName())

                    )
                    .replace("{name}",
                            StringUtils.getDisplayName(player.getDisplayName()))
                    .replace("{suffix}",
                            StringUtils.getSuffix(player.getName()))

                    .replace("{groupPrefix}",
                            StringUtils.getGroupPrefix(player.getName()))
                    .replace("{groupSuffix}",
                            StringUtils.getGroupSuffix(player.getName()))
                    .replace("{message}", " " + message)
                    .replace(
                            "{mvWorld}",
                            StringUtils.getMvWorldAlias(player.getWorld()
                                    .getName()))
                    .replace(
                            "{mvColor}",
                            StringUtils.getMvWorldColor(player.getWorld()
                                    .getName())).replace("{world}",
                    StringUtils.getWorld(player.getWorld().getName())));
            Variables.linesToIrc++;
            IRC.sendMessageToChannel(c, ColorUtils.formatGametoIRC(msg));
        }
    }

    public static void sendMessage(String sender, String message, IRCChannel c) {
        Channel channel = c.getTownyChannel();
        chatProcess(sender, channel, message);
    }

    public static void chatProcess(String sernder, Channel channel, String mesage) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (plugin.getTowny().isPermissions()
                    && TownyUniverse.getPermissionSource().has(p,
                            channel.getPermission())) {

            }
        }
    }
}