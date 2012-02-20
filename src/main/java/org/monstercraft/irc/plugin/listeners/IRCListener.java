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
import org.bukkit.event.server.ServerCommandEvent;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.hooks.HeroChatHook;
import org.monstercraft.irc.plugin.util.ChatType;
import org.monstercraft.irc.plugin.util.IRCColor;
import org.monstercraft.irc.plugin.util.Methods;
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
				IRC.getHookManager().setPermissionsHook();
				IRC.getHandleManager().setPermissionsHandler(
						IRC.getHookManager().getPermissionsHook());
				IRC.getHookManager().setChatHook();
			} else if (PluginName.equals("mcMMO")) {
				IRC.getHookManager().setmcMMOHook();
			} else if (PluginName.equals("HeroChat")) {
				IRC.getHookManager().setHeroChatHook(new HeroChatHook(plugin));
			} else if (PluginName.equals("TownyChat")) {
				IRC.getHookManager().setTownyChatHook();
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}
		try {
			if (plugin.isEnabled()) {
				Player player = event.getPlayer();
				for (IRCChannel c : IRC.getChannels()) {
					handleMessage(player, c, event.getMessage());
				}
			}
		} catch (Exception e) {
			Methods.debug(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		for (IRCChannel c : Variables.channels) {
			if (c.showJoinLeave()) {
				IRC.getHandleManager()
						.getIRCHandler()
						.sendMessage(
								IRCColor.formatMCMessage(Variables.joinformat
										.replace(
												"{name}",
												StringUtils.getName(event
														.getPlayer().getName()))
										.replace(
												"{prefix}",
												StringUtils.getPrefix(event
														.getPlayer().getName()))
										.replace(
												"{suffix}",
												StringUtils.getSuffix(event
														.getPlayer().getName()))
										.replace(
												"{groupPrefix}",
												StringUtils
														.getGroupPrefix(event
																.getPlayer()
																.getName()))
										.replace(
												"{groupSuffix}",
												StringUtils
														.getGroupSuffix(event
																.getPlayer()
																.getName()))
										.replace(
												"{world}",
												StringUtils.getWorld(event
														.getPlayer().getName()))
										.replace("&", "§")), c.getChannel());
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
								IRCColor.formatMCMessage(Variables.leaveformat
										.replace(
												"{name}",
												StringUtils.getName(event
														.getPlayer().getName()))
										.replace(
												"{prefix}",
												StringUtils.getPrefix(event
														.getPlayer().getName()))
										.replace(
												"{suffix}",
												StringUtils.getSuffix(event
														.getPlayer().getName()))
										.replace(
												"{groupPrefix}",
												StringUtils
														.getGroupPrefix(event
																.getPlayer()
																.getName()))
										.replace(
												"{groupSuffix}",
												StringUtils
														.getGroupSuffix(event
																.getPlayer()
																.getName()))
										.replace(
												"{world}",
												StringUtils.getWorld(event
														.getPlayer().getName()))
										.replace("&", "§")), c.getChannel());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.isCancelled()) {
			return;
		}
		for (IRCChannel c : Variables.channels) {
			if (c.showJoinLeave()) {
				IRC.getHandleManager()
						.getIRCHandler()
						.sendMessage(
								IRCColor.formatMCMessage(event.getPlayer()
										.getName()
										+ IRCColor.RED
										+ "has been kicked! ("
										+ event.getReason() + ")"),
								c.getChannel());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onServerCommand(ServerCommandEvent event) {
		if (event.getCommand().startsWith("say")) {
			for (IRCChannel c : IRC.getChannels()) {
				handleMessage(null, c,
						event.getCommand().toString().substring(4));
			}
		}
	}

	private void handleMessage(final Player player, final IRCChannel c,
			final String message) {
		if (c.getChatType() == ChatType.NONE) {
			return;
		}
		if (player == null) {
			StringBuffer result2 = new StringBuffer();
			result2.append(Variables.ircformat
					.replace("{prefix}", StringUtils.getPrefix("Console"))
					.replace("{name}", StringUtils.getName("Console"))
					.replace("{suffix}", StringUtils.getSuffix("Console"))

					.replace("{groupPrefix}",
							StringUtils.getGroupPrefix("Console"))
					.replace("{groupSuffix}",
							StringUtils.getGroupSuffix("Console"))
					.replace("{message}",
							IRCColor.NORMAL.getIRCColor() + " " + message)
					.replace("{world}", StringUtils.getWorld("Console"))
					.replace("&", "§"));
			IRC.getHandleManager()
					.getIRCHandler()
					.sendMessage(IRCColor.formatMCMessage(result2.toString()),
							c.getChannel());
			return;
		}
		StringBuffer result = new StringBuffer();
		result.append(Variables.ircformat
				.replace("{prefix}", StringUtils.getPrefix(player.getName()))
				.replace("{name}", StringUtils.getName(player.getName()))
				.replace("{suffix}", StringUtils.getSuffix(player.getName()))

				.replace("{groupPrefix}",
						StringUtils.getGroupPrefix(player.getName()))
				.replace("{groupSuffix}",
						StringUtils.getGroupSuffix(player.getName()))
				.replace("{message}",
						IRCColor.NORMAL.getIRCColor() + " " + message)
				.replace("{world}", StringUtils.getWorld(player.getName()))
				.replace("&", "§"));
		if (c.getChatType() == ChatType.ADMINCHAT) {
			if (IRC.getHookManager().getmcMMOHook() != null) {
				if (IRC.getHookManager().getmcMMOHook()
						.getPlayerProfile(player).getAdminChatMode()) {
					IRC.getHandleManager()
							.getIRCHandler()
							.sendMessage(
									IRCColor.formatMCMessage(result.toString()),
									c.getChannel());
				}
			}
		} else if (c.getChatType() == ChatType.HEROCHAT && !Variables.hc4) {
			if (IRC.getHookManager().getmcMMOHook() != null) {
				if (IRC.getHookManager().getmcMMOHook()
						.getPlayerProfile(player).getAdminChatMode()) {
					return;
				}
			}
			if ((Herochat.getChatterManager().getChatter(player)
					.getActiveChannel() == c.getHeroChatChannel() || c
					.isHeroChatListenChannel(Herochat.getChatterManager()
							.getChatter(player).getActiveChannel().getName()))
					&& !Herochat.getChatterManager()
							.getChatter(player.getName()).isMuted()) {
				IRC.getHandleManager()
						.getIRCHandler()
						.sendMessage(
								IRCColor.formatMCMessage(result.toString()),
								c.getChannel());
			}
		} else if (c.getChatType() == ChatType.HEROCHAT
				&& IRC.getHookManager().getHeroChatHook() != null
				&& Variables.hc4) {
			if (IRC.getHookManager().getHeroChatHook().isEnabled()) {
				if (IRC.getHookManager().getmcMMOHook() != null) {
					if (IRC.getHookManager().getmcMMOHook()
							.getPlayerProfile(player).getAdminChatMode()) {
						return;
					}
				}
				if ((IRC.getHookManager().getHeroChatHook().getChannelManager()
						.getActiveChannel(player.getName()) == c
						.getHeroChatFourChannel() || c
						.isHeroChatListenChannel(IRC.getHookManager()
								.getHeroChatHook().getChannelManager()
								.getActiveChannel(player.getName()).getName()))
						&& c.getHeroChatFourChannel().isEnabled()
						&& !IRC.getHookManager().getHeroChatHook()
								.getChannelManager().getMutelist()
								.contains(player.getName())
						&& !c.getHeroChatFourChannel().getMutelist()
								.contains(player.getName())) {
					if (IRC.getHandleManager()
							.getPermissionsHandler()
							.anyGroupsInList(
									player,
									IRC.getHookManager().getHeroChatHook()
											.getChannelManager()
											.getActiveChannel(player.getName())
											.getVoicelist())
							|| IRC.getHookManager().getHeroChatHook()
									.getChannelManager()
									.getActiveChannel(player.getName())
									.getVoicelist().isEmpty()) {
						IRC.getHandleManager()
								.getIRCHandler()
								.sendMessage(
										IRCColor.formatMCMessage(result
												.toString()), c.getChannel());
					}
				}
			}
		} else if (c.getChatType() == ChatType.GLOBAL) {
			if (IRC.getHookManager().getmcMMOHook() != null) {
				if (IRC.getHookManager().getmcMMOHook()
						.getPlayerProfile(player).getAdminChatMode()) {
					return;
				}
			}
			IRC.getHandleManager()
					.getIRCHandler()
					.sendMessage(IRCColor.formatMCMessage(result.toString()),
							c.getChannel());
		} else if (c.getChatType() == ChatType.TOWNYCHAT) {
			if (IRC.getHookManager().getmcMMOHook() != null) {
				if (IRC.getHookManager().getmcMMOHook()
						.getPlayerProfile(player).getAdminChatMode()) {
					return;
				}
			}
			IRC.getHandleManager()
					.getIRCHandler()
					.sendMessage(IRCColor.formatMCMessage(result.toString()),
							c.getChannel());
		}
	}
}
