package org.monstercraft.irc.plugin.towny;

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

    public static void chatProcess(final Channel channel, final String message) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission(channel.getPermission())) {
                p.sendMessage(message);
            }
        }
    }

    public static void sendMessage(final String message, final IRCChannel c) {
        final Channel channel = c.getTownyChannel();
        TownyChatListener.chatProcess(channel, message);
    }

    private static Chat plugin;

    private static WeakHashMap<Player, Channel> directedChat = new WeakHashMap<Player, Channel>();

    public TownyChatListener(final Chat paramChat) {

        TownyChatListener.plugin = paramChat;
    }

    public void handle(final IRCChannel c, final Player player,
            final String message) {
        if (c.getChatType() == ChatType.TOWNYCHAT) {
            if (Bukkit.getServer().getPluginManager().getPlugin("mcMMO") != null) {
                if (Users.getProfile(player.getName()).getAdminChatMode()) {
                    return;
                }
                if (Users.getProfile(player.getName()).getPartyChatMode()) {
                    return;
                }
            }
            final String msg = Variables.ircformat
                    .replace("{HCchannelColor}",
                            c.getTownyChannel().getMessageColour())
                    .replace("{heroChatTag}",
                            c.getTownyChannel().getChannelTag())
                    .replace("{heroChatName}", "")
                    .replace("{prefix}",
                            StringUtils.getPrefix(player.getName())

                    )
                    .replace("{name}",
                            StringUtils.getDisplayName(player.getName()))
                    .replace("{displayName}",
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
                                    .getName()))
                    .replace("{world}",
                            StringUtils.getWorld(player.getWorld().getName()));
            Variables.linesToIrc++;
            IRC.sendMessageToChannel(c, ColorUtils.formatGametoIRC(msg));
        }
    }

    public boolean inChannel(final Channel c, final Player p) {
        if (TownyChatListener.plugin.getChannelsHandler()
                .getChannel(p, channelTypes.GLOBAL).equals(c)) {
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(final PlayerChatEvent paramPlayerChatEvent) {
        final Player localPlayer = paramPlayerChatEvent.getPlayer();
        for (final IRCChannel c : MonsterIRC.getChannels()) {
            if (c.getChatType() == ChatType.TOWNYCHAT) {
                if (c.getTownyChannel() == null) {
                    IRC.log("The towny channel you entered was not found!");
                    return;
                }
                if (this.inChannel(c.getTownyChannel(), localPlayer)
                        || TownyChatListener.directedChat.get(localPlayer)
                                .equals(c.getTownyChannel())
                        && MonsterIRC
                                .getHandleManager()
                                .getPermissionsHandler()
                                .hasNode(localPlayer,
                                        c.getTownyChannel().getPermission())) {
                    this.handle(c, localPlayer,
                            paramPlayerChatEvent.getMessage());
                }
            }
        }
    }

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
}