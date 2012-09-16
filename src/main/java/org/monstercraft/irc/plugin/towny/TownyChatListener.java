package org.monstercraft.irc.plugin.towny;

import java.util.Iterator;

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

@SuppressWarnings("deprecation")
public class TownyChatListener implements Listener {
    private static Chat plugin;

    public boolean inChannel(Channel c, Player p) {
        Iterator<Channel> localObject1 = plugin.getChannelsHandler()
                .getAllChannels().values().iterator();
        Object localObject2;
        while ((localObject1).hasNext()) {
            localObject2 = (Channel) (localObject1).next();
            if (plugin.getTowny().hasPlayerMode(p,
                    ((Channel) localObject2).getName())) {
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