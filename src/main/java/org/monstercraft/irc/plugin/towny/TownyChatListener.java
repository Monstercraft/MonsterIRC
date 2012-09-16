package org.monstercraft.irc.plugin.towny;

import java.util.Iterator;
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
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.channels.channelTypes;

@SuppressWarnings("deprecation")
public class TownyChatListener implements Listener {
    private static Chat plugin;

    public boolean inChannel(Channel c, Player p) {
        Iterator<Channel> localObject1 = plugin.getChannelsHandler()
                .getAllChannels().values().iterator();
        while ((localObject1).hasNext()) {
            if (plugin.getChannelsHandler().getChannel(p, channelTypes.GLOBAL) == c) {
                return true;
            }
        }
        return false;
    }

    public TownyChatListener(Chat paramChat) {
        plugin = paramChat;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(PlayerChatEvent paramPlayerChatEvent) {
        Player localPlayer = paramPlayerChatEvent.getPlayer();
        for (IRCChannel c : MonsterIRC.getChannels()) {
            if (c.getChatType() == ChatType.TOWNYCHAT) {
                if (inChannel(c.getTownyChannel(), localPlayer)) {
                    handle(c, localPlayer, paramPlayerChatEvent.getMessage());
                } else if (TownyChatListener.directedChat.get(localPlayer)
                        .equals(c.getTownyChannel())
                        && MonsterIRC
                                .getHandleManager()
                                .getPermissionsHandler()
                                .hasNode(localPlayer,
                                        c.getTownyChannel().getPermission())) {
                    handle(c, localPlayer, paramPlayerChatEvent.getMessage());
                }
            }
        }
    }

    private static WeakHashMap<Player, Channel> directedChat = new WeakHashMap<Player, Channel>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(
            final PlayerCommandPreprocessEvent event) {
        if (MonsterIRC.getHookManager().getTownyChatHook() != null) {
            final Player player = event.getPlayer();
            final String split[] = event.getMessage().split("\\ ");
            final String command = split[0].trim().toLowerCase()
                    .replace("/", "");
            final Channel channel = MonsterIRC.getHookManager()
                    .getTownyChatHook().getChannelsHandler()
                    .getChannel(player, command);
            if (channel != null) {
                if (TownyChatListener.directedChat.containsKey(player)) {
                    boolean doReturn = false;
                    if (TownyChatListener.directedChat.get(player).equals(
                            channel)) {
                        doReturn = true;
                    }
                    TownyChatListener.directedChat.remove(player);
                    if (doReturn) {
                        return;
                    }
                }
                TownyChatListener.directedChat.put(player, channel);
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
                    .replace("{HCchannelColor}",
                            c.getTownyChannel().getMessageColour())
                    .replace("{heroChatTag}",
                            c.getTownyChannel().getChannelTag())
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

    public static void sendMessage(String message, IRCChannel c) {
        Channel channel = c.getTownyChannel();
        chatProcess(channel, message);
    }

    public static void chatProcess(Channel channel, String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission(channel.getPermission())) {
                p.sendMessage(message);
            }
        }
    }
}