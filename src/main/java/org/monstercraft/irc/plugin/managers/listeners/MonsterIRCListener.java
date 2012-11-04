package org.monstercraft.irc.plugin.managers.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.util.ChatType;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.util.StringUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

import com.dthielke.herochat.Herochat;
import com.gmail.nossr50.util.Users;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class MonsterIRCListener implements Listener {
    private final MonsterIRC plugin;

    /**
     * 
     * @param plugin
     *            The parent plugin for the listener.
     */
    public MonsterIRCListener(final MonsterIRC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginEnable(final PluginEnableEvent event) {
        final String PluginName = event.getPlugin().getDescription().getName();
        if (plugin != null) {
            if (PluginName.equals("Vault")) {
                MonsterIRC.getHookManager().setPermissionsHook();
                MonsterIRC.getHandleManager().setPermissionsHandler(
                        MonsterIRC.getHookManager().getPermissionsHook());
                MonsterIRC.getHookManager().setChatHook();
            } else if (PluginName.equals("TownyChat")) {
                MonsterIRC.getHookManager().setTownyChatHook();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(final AsyncPlayerChatEvent event) {
        if (Bukkit.getServer().getPluginManager().getPlugin("mcMMO") != null) {
            if (!Users.getProfile(event.getPlayer().getName())
                    .getAdminChatMode()) {
                if (event.isCancelled()) {
                    return;
                }
            }
        } else if (event.isCancelled()) {
            return;
        }
        try {
            final Player player = event.getPlayer();
            for (final IRCChannel c : MonsterIRC.getChannels()) {
                if (c.getChatType() == ChatType.MTADMINCHAT) {
                    continue;
                }
                if (c.getChatType() == ChatType.TOWNYCHAT) {
                    continue;
                }
                MonsterIRCListener.handleMessage(player, c, event.getMessage());
            }
        } catch (final Exception e) {
            IRC.debug(e);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (event.getJoinMessage() == null) {
            return;
        }
        for (final IRCChannel c : Variables.channels) {
            if (!c.getBlockedEvents().contains("game_join")) {
                IRC.sendMessageToChannel(
                        c.getChannel(),
                        ColorUtils.formatGametoIRC(event.getPlayer()
                                .getDisplayName() + " has joined."));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (event.getQuitMessage() == null) {
            return;
        }
        for (final IRCChannel c : Variables.channels) {
            if (!c.getBlockedEvents().contains("game_quit")) {
                IRC.sendMessageToChannel(
                        c.getChannel(),
                        ColorUtils.formatGametoIRC(event.getPlayer()
                                .getDisplayName() + " has quit."));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (event.getDeathMessage() == null) {
            return;
        }
        for (final IRCChannel c : Variables.channels) {
            if (!c.getBlockedEvents().contains("game_death")) {
                IRC.sendMessageToChannel(
                        c.getChannel(),
                        ColorUtils.formatGametoIRC(event.getEntity()
                                .getDisplayName() + " has died."));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerKick(final PlayerKickEvent event) {
        if (event.isCancelled() || event.getLeaveMessage() == null) {
            return;
        }
        for (final IRCChannel c : Variables.channels) {
            if (!c.getBlockedEvents().contains("game_kick")) {
                IRC.sendMessageToChannel(
                        c.getChannel(),
                        ColorUtils.formatGametoIRC(event.getPlayer()
                                .getDisplayName()

                        + " has been kicked."));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerCommand(final ServerCommandEvent event) {
        if (Variables.passSay) {
            if (event.getCommand().startsWith("say")) {
                for (final IRCChannel c : MonsterIRC.getChannels()) {
                    MonsterIRCListener.handleMessage(null, c, event
                            .getCommand().toString().substring(4));
                }
            }
        }
    }

    protected static void handleMessage(final Player player,
            final IRCChannel c, final String message) {
        if (player != null) {
            if (MonsterIRC.getHandleManager().getPermissionsHandler()
                    .hasNode(player, "irc.nochat")
                    && !player.isOp()
                    && !MonsterIRC.getHandleManager().getPermissionsHandler()
                            .hasNode(player, "*")) {
                player.sendMessage("You are blocked from sending messages to irc!");
                return;
            }
        }
        if (c.getBlockedEvents().contains("game_chat")) {
            return;
        }
        if (player == null) {
            final StringBuffer result2 = new StringBuffer();
            result2.append(Variables.ircformat
                    .replace("{HCchannelColor}", "&f")
                    .replace("{heroChatTag}", "[Console]")
                    .replace("{heroChatName}", "[Console]")
                    .replace("{prefix}", StringUtils.getPrefix("Console"))
                    .replace("{name}", StringUtils.getDisplayName("Console"))
                    .replace("{displayName}",
                            StringUtils.getDisplayName("Console"))
                    .replace("{suffix}", StringUtils.getSuffix("Console"))
                    .replace("{groupPrefix}",
                            StringUtils.getGroupPrefix("Console"))
                    .replace("{groupSuffix}",
                            StringUtils.getGroupSuffix("Console"))
                    .replace("{message}", message)
                    .replace("{mvWorld}",
                            StringUtils.getMvWorldAlias("console"))
                    .replace("{mvColor}",
                            StringUtils.getMvWorldColor("console"))
                    .replace("{world}", StringUtils.getWorld("Console")));
            Variables.linesToIrc++;
            IRC.sendMessageToChannel(c,
                    ColorUtils.formatGametoIRC(result2.toString()));
            return;
        }
        final StringBuffer result = new StringBuffer();
        if (c.getChatType() == ChatType.MCMMOADMINCHAT) {
            if (Bukkit.getServer().getPluginManager().getPlugin("mcMMO") != null) {
                if (Users.getProfile(player.getName()).getAdminChatMode()) {
                    result.append(Variables.ircformat
                            .replace("{HCchannelColor}", "")
                            .replace("{heroChatTag}", "")
                            .replace("{prefix}",
                                    StringUtils.getPrefix(player.getName())

                            )
                            .replace(
                                    "{name}",
                                    StringUtils.getDisplayName(player.getName()))
                            .replace(
                                    "{displayName}",
                                    StringUtils.getDisplayName(player
                                            .getDisplayName()))
                            .replace("{suffix}",
                                    StringUtils.getSuffix(player.getName()))

                            .replace(
                                    "{groupPrefix}",
                                    StringUtils.getGroupPrefix(player.getName()))
                            .replace(
                                    "{groupSuffix}",
                                    StringUtils.getGroupSuffix(player.getName()))
                            .replace("{message}", " " + message)
                            .replace(
                                    "{mvWorld}",
                                    StringUtils.getMvWorldAlias(player
                                            .getWorld().getName()))
                            .replace(
                                    "{mvColor}",
                                    StringUtils.getMvWorldColor(player
                                            .getWorld().getName()))
                            .replace(
                                    "{world}",
                                    StringUtils.getWorld(player.getWorld()
                                            .getName())));
                    Variables.linesToIrc++;
                    IRC.sendMessageToChannel(c,
                            ColorUtils.formatGametoIRC(result.toString()));
                }
            }
        } else if (c.getChatType() == ChatType.HEROCHAT) {
            if (Bukkit.getServer().getPluginManager().getPlugin("mcMMO") != null) {
                if (Users.getProfile(player.getName()).getAdminChatMode()) {
                    return;
                }
                if (Users.getProfile(player.getName()).getPartyChatMode()) {
                    return;
                }
            }
            if (c.getHeroChatChannel() != null) {
                if ((Herochat.getChatterManager().getChatter(player)
                        .getActiveChannel() == c.getHeroChatChannel() || c
                        .isHeroChatListenChannel(Herochat.getChatterManager()
                                .getChatter(player.getName())
                                .getActiveChannel().getName()))
                        && !Herochat.getChatterManager()
                                .getChatter(player.getName()).isMuted()) {
                    result.append(Variables.ircformat
                            .replace(
                                    "{heroChatTag}",
                                    Herochat.getChatterManager()
                                            .getChatter(player)
                                            .getActiveChannel().getColor()
                                            + "["
                                            + Herochat.getChatterManager()
                                                    .getChatter(player)
                                                    .getActiveChannel()
                                                    .getNick()
                                            + "]"
                                            + ColorUtils.NORMAL.getIRCColor()
                                            + "")
                            .replace(
                                    "{heroChatName}",
                                    Herochat.getChatterManager()
                                            .getChatter(player)
                                            .getActiveChannel().getColor()
                                            + "["
                                            + Herochat.getChatterManager()
                                                    .getChatter(player)
                                                    .getActiveChannel()
                                                    .getName()
                                            + "]"
                                            + ColorUtils.NORMAL.getIRCColor()
                                            + "")
                            .replace("{prefix}",
                                    StringUtils.getPrefix(player.getName()))
                            .replace(
                                    "{name}",
                                    StringUtils.getDisplayName(player.getName()))
                            .replace(
                                    "{displayName}",
                                    StringUtils.getDisplayName(player
                                            .getDisplayName()))
                            .replace("{suffix}",
                                    StringUtils.getSuffix(player.getName()))
                            .replace(
                                    "{groupPrefix}",
                                    StringUtils.getGroupPrefix(player.getName()))
                            .replace(
                                    "{groupSuffix}",
                                    StringUtils.getGroupSuffix(player.getName()))
                            .replace(
                                    "{HCchannelColor}",
                                    Herochat.getChatterManager()
                                            .getChatter(player)
                                            .getActiveChannel().getColor()
                                            .toString())
                            .replace("{message}", " " + message)
                            .replace(
                                    "{mvWorld}",
                                    StringUtils.getMvWorldAlias(player
                                            .getWorld().getName()))
                            .replace(
                                    "{mvColor}",
                                    StringUtils.getMvWorldColor(player
                                            .getWorld().getName()))
                            .replace(
                                    "{world}",
                                    StringUtils.getWorld(player.getWorld()
                                            .getName())));
                    Variables.linesToIrc++;
                    IRC.sendMessageToChannel(c.getChannel(),
                            ColorUtils.formatGametoIRC(result.toString()));
                }
            } else {
                IRC.log("Invalid herochat channel detected for "
                        + c.getChannel());
            }
        } else if (c.getChatType() == ChatType.GLOBAL) {
            if (Bukkit.getServer().getPluginManager().getPlugin("mcMMO") != null) {
                if (Users.getProfile(player.getName()).getAdminChatMode()) {
                    return;
                }
                if (Users.getProfile(player.getName()).getPartyChatMode()) {
                    return;
                }
            }
            result.append(Variables.ircformat
                    .replace("{HCchannelColor}", "&f")
                    .replace("{heroChatTag}", "")
                    .replace("{heroChatName}", "")
                    .replace("{prefix}",
                            StringUtils.getPrefix(player.getName()))
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
                            StringUtils.getWorld(player.getWorld().getName())));
            Variables.linesToIrc++;
            IRC.sendMessageToChannel(c,
                    ColorUtils.formatGametoIRC(result.toString()));
        }
    }
}