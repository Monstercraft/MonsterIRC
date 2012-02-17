package org.monstercraft.irc.plugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.hooks.HeroChatHook;
import org.monstercraft.irc.plugin.hooks.VaultChatHook;
import org.monstercraft.irc.plugin.hooks.VaultPermissionsHook;
import org.monstercraft.irc.plugin.hooks.mcMMOHook;
import org.monstercraft.irc.plugin.util.ChatType;
import org.monstercraft.irc.plugin.util.IRCColor;
import org.monstercraft.irc.plugin.util.StringUtils;
import org.monstercraft.irc.plugin.util.Variables;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

import com.dthielke.herochat.Herochat;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRCListener extends IRC implements Listener {
	private IRC plugin;

	/**
	 * Creates an instance of the IRCPlayerListener class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public IRCListener(final IRC plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPluginEnable(PluginEnableEvent event) {
		String PluginName = event.getPlugin().getDescription().getName();
		if (plugin != null) {
			if (PluginName.equals("Vault")) {
				IRC.getHookManager().setPermissionsHook(
						new VaultPermissionsHook(plugin));
				IRC.getHandleManager().setPermissionsHandler(
						IRC.getHookManager().getPermissionsHook());
				IRC.getHookManager().setChatHook(new VaultChatHook(plugin));
			} else if (PluginName.equals("mcMMO")) {
				IRC.getHookManager().setmcMMOHook(new mcMMOHook(plugin));
			} else if (PluginName.equals("HeroChat")) {
				IRC.getHookManager().setHeroChatHook(new HeroChatHook(plugin));
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(PlayerChatEvent event) {
		try {
			if (plugin.isEnabled()) {
				Player player = event.getPlayer();
				for (IRCChannel c : Variables.channels) {
					StringBuffer result = new StringBuffer();
					result.append(Variables.ircformat
							.replace(
									"{prefix}",
									StringUtils.getPrefix(event.getPlayer()
											.getName()))
							.replace(
									"{name}",
									StringUtils.getName(event.getPlayer()
											.getName()))
							.replace(
									"{suffix}",
									StringUtils.getSuffix(event.getPlayer()
											.getName()))

							.replace(
									"{groupPrefix}",
									StringUtils.getGroupPrefix(event
											.getPlayer().getName()))
							.replace(
									"{groupSuffix}",
									StringUtils.getGroupSuffix(event
											.getPlayer().getName()))
							.replace(
									"{message}",
									IRCColor.NORMAL.getIRCColor()
											+ event.getMessage())
							.replace(
									"{world}",
									StringUtils.getWorld(event.getPlayer()
											.getName())).replace("&", "§"));
					if (c.getChatType() == ChatType.ADMINCHAT) {
						if (IRC.getHookManager().getmcMMOHook() != null) {
							if (IRC.getHookManager().getmcMMOHook()
									.getPlayerProfile(player)
									.getAdminChatMode()) {
								IRC.getHandleManager()
										.getIRCHandler()
										.sendMessage(
												IRCColor.formatMCMessage(result
														.toString()),
												c.getChannel());
							}
						}
					} else if (c.getChatType() == ChatType.HEROCHAT
							&& !Variables.hc4) {
						if (IRC.getHookManager().getmcMMOHook() != null) {
							if (IRC.getHookManager().getmcMMOHook()
									.getPlayerProfile(player)
									.getAdminChatMode()) {
								continue;
							}
						}
						if (Herochat.getChatterManager().getChatter(player)
								.getActiveChannel() == c.getHeroChatChannel()
								&& !Herochat.getChatterManager()
										.getChatter(player.getName()).isMuted()) {
							IRC.getHandleManager()
									.getIRCHandler()
									.sendMessage(
											IRCColor.formatMCMessage(result
													.toString()),
											c.getChannel());
						}
					} else if (c.getChatType() == ChatType.HEROCHAT
							&& IRC.getHookManager().getHeroChatHook() != null
							&& Variables.hc4) {
						if (IRC.getHookManager().getHeroChatHook().isEnabled()) {
							if (IRC.getHookManager().getmcMMOHook() != null) {
								if (IRC.getHookManager().getmcMMOHook()
										.getPlayerProfile(player)
										.getAdminChatMode()) {
									continue;
								}
							}
							if (IRC.getHookManager().getHeroChatHook()
									.getChannelManager()
									.getActiveChannel(player.getName()) == c
									.getHeroChatFourChannel()
									&& c.getHeroChatFourChannel().isEnabled()
									&& !IRC.getHookManager().getHeroChatHook()
											.getChannelManager().getMutelist()
											.contains(player.getName())
									&& !c.getHeroChatFourChannel()
											.getMutelist()
											.contains(player.getName())) {
								if (IRC.getHandleManager()
										.getPermissionsHandler()
										.anyGroupsInList(
												player,
												IRC.getHookManager()
														.getHeroChatHook()
														.getChannelManager()
														.getActiveChannel(
																player.getName())
														.getVoicelist())
										|| IRC.getHookManager()
												.getHeroChatHook()
												.getChannelManager()
												.getActiveChannel(
														player.getName())
												.getVoicelist().isEmpty()) {
									IRC.getHandleManager()
											.getIRCHandler()
											.sendMessage(
													IRCColor.formatMCMessage(result
															.toString()),
													c.getChannel());
								}
							}
						}
					} else if (c.getChatType() == ChatType.GLOBAL) {
						if (IRC.getHookManager().getmcMMOHook() != null) {
							if (IRC.getHookManager().getmcMMOHook()
									.getPlayerProfile(player)
									.getAdminChatMode()) {
								continue;
							}
						}
						IRC.getHandleManager()
								.getIRCHandler()
								.sendMessage(
										IRCColor.formatMCMessage(result
												.toString()), c.getChannel());
					}
				}
			}
		} catch (Exception e) {
			debug(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		for (IRCChannel c : Variables.channels) {
			if (c.showJoinLeave()) {
				IRC.getHandleManager()
						.getIRCHandler()
						.sendMessage(
								IRCColor.formatMCMessage(IRCColor.WHITE
										.getMinecraftColor()
										+ StringUtils.getName(event.getPlayer()
												.getName())
										+ IRCColor.RED.getMinecraftColor()
										+ " joined."), c.getChannel());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		for (IRCChannel c : Variables.channels) {
			if (c.showJoinLeave()) {
				IRC.getHandleManager()
						.getIRCHandler()
						.sendMessage(
								IRCColor.formatMCMessage(IRCColor.WHITE
										.getMinecraftColor()
										+ StringUtils.getName(event.getPlayer()
												.getName())
										+ IRCColor.RED.getMinecraftColor()
										+ " quit."), c.getChannel());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(PlayerKickEvent event) {
		for (IRCChannel c : Variables.channels) {
			if (c.showJoinLeave()) {
				IRC.getHandleManager()
						.getIRCHandler()
						.sendMessage(
								IRCColor.formatMCMessage(IRCColor.WHITE
										.getMinecraftColor()
										+ StringUtils.getName(event.getPlayer()
												.getName())
										+ IRCColor.RED.getMinecraftColor()
										+ " was kicked."), c.getChannel());
			}
		}
	}
}
