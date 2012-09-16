package org.monstercraft.irc.plugin.towny;

import java.lang.reflect.Field;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
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
import com.palmergames.bukkit.TownyChat.listener.TownyChatPlayerListener;

@SuppressWarnings("deprecation")
public class TownyChatListener implements Listener {
    private static Chat plugin;

    @SuppressWarnings("unchecked")
    public static WeakHashMap<Player, String> getChat() throws Exception {
        final Field field = TownyChatPlayerListener.class
                .getDeclaredField("directedChat");
        final Field chat = plugin.getClass().getDeclaredField("TownyPlayerListener");
        field.setAccessible(true);
        chat.setAccessible(true);
        TownyChatPlayerListener instance = (TownyChatPlayerListener) chat
                .get(MonsterIRC.getHookManager().getChatHook());
        return (WeakHashMap<Player, String>) field.get(instance);
    }

    public TownyChatListener(Chat paramChat) {
        plugin = paramChat;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(PlayerChatEvent paramPlayerChatEvent) {
        Player localPlayer = paramPlayerChatEvent.getPlayer();
        Channel channel = null;
        try {
            channel = plugin.getChannelsHandler().getChannel(localPlayer,
                    (String) getChat().get(localPlayer));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        for (IRCChannel c : MonsterIRC.getChannels()) {
            if (c.getChatType() == ChatType.TOWNYCHAT) {
                if (c.getTownyChannel() == channel) {
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

    public static void chatProcess(String sender, Channel channel,
            String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission(channel.getPermission())) {
                p.sendMessage(message);
            }
        }
    }
}