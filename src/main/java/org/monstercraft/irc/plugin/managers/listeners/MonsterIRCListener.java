package org.monstercraft.irc.plugin.managers.listeners;

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
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
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
public class MonsterIRCListener extends MonsterIRC implements Listener {
	private MonsterIRC plugin;

	/**
	 * Creates an instance of the IRCPlayerListener class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public MonsterIRCListener(final MonsterIRC plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPluginEnable(PluginEnableEvent event) {
		String PluginName = event.getPlugin().getDescription().getName();
		if (plugin != null) {
			if (PluginName.equals("Vault")) {
				MonsterIRC.getHookManager().setPermissionsHook();
				MonsterIRC.getHandleManager().setPermissionsHandler(
						MonsterIRC.getHookManager().getPermissionsHook());
				MonsterIRC.getHookManager().setChatHook();
			} else if (PluginName.equals("mcMMO")) {
				MonsterIRC.getHookManager().setmcMMOHook();
			} else if (PluginName.equals("HeroChat")) {
				MonsterIRC.getHookManager().setHeroChatHook();
			} else if (PluginName.equals("TownyChat")) {
				MonsterIRC.getHookManager().setTownyChatHook();
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(PlayerChatEvent event) {
		if (MonsterIRC.getHookManager().getmcMMOHook() != null) {
			if (!MonsterIRC.getHookManager().getmcMMOHook()
					.getPlayerProfile(event.getPlayer()).getAdminChatMode()) {
				if (event.isCancelled()) {
					return;
				}
			}
		} else if (event.isCancelled()) {
			return;
		}
		try {
			if (plugin.isEnabled()) {
				Player player = event.getPlayer();
				for (IRCChannel c : MonsterIRC.getChannels()) {
					handleMessage(player, c, event.getMessage());
				}
			}
		} catch (Exception e) {
			IRC.debug(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		for (IRCChannel c : Variables.channels) {
			if (c.showJoinLeave()) {
				IRC.sendMessage(c.getChannel(), event.getPlayer().getName()
						+ " has joined.");
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		for (IRCChannel c : Variables.channels) {
			if (c.showJoinLeave()) {
				IRC.sendMessage(c.getChannel(), event.getPlayer().getName()
						+ " has quit.");
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
				IRC.sendMessage(c.getChannel(), event.getPlayer().getName()
						+ " has been kicked.");
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onServerCommand(ServerCommandEvent event) {
		if (Variables.passSay) {
			if (event.getCommand().startsWith("say")) {
				for (IRCChannel c : MonsterIRC.getChannels()) {
					handleMessage(null, c, event.getCommand().toString()
							.substring(4));
				}
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
			IRC.sendMessage(c, IRCColor.formatMCMessage(result2.toString()));
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
			if (MonsterIRC.getHookManager().getmcMMOHook() != null) {
				if (MonsterIRC.getHookManager().getmcMMOHook()
						.getPlayerProfile(player).getAdminChatMode()) {
					IRC.sendMessage(c,
							IRCColor.formatMCMessage(result.toString()));
				}
			}
		} else if (c.getChatType() == ChatType.HEROCHAT && !Variables.hc4) {
			if (MonsterIRC.getHookManager().getmcMMOHook() != null) {
				if (MonsterIRC.getHookManager().getmcMMOHook()
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
				IRC.sendMessage(
						c.getChannel(),
						IRCColor.formatMCMessage("§"
								+ Herochat.getChatterManager()
										.getChatter(player).getActiveChannel()
										.getColor().getChar()
								+ "["
								+ Herochat.getChatterManager()
										.getChatter(player).getActiveChannel()
										.getNick() + "]: "
								+ IRCColor.NORMAL.getIRCColor()
								+ result.toString()));
			}
		} else if (c.getChatType() == ChatType.HEROCHAT
				&& MonsterIRC.getHookManager().getHeroChatHook() != null
				&& Variables.hc4) {
			if (MonsterIRC.getHookManager().getHeroChatHook().isEnabled()) {
				if (MonsterIRC.getHookManager().getmcMMOHook() != null) {
					if (MonsterIRC.getHookManager().getmcMMOHook()
							.getPlayerProfile(player).getAdminChatMode()) {
						return;
					}
				}
				if ((MonsterIRC.getHookManager().getHeroChatHook()
						.getChannelManager().getActiveChannel(player.getName())
						.equals(c.getHeroChatFourChannel()) || c
						.isHeroChatListenChannel(MonsterIRC.getHookManager()
								.getHeroChatHook().getChannelManager()
								.getActiveChannel(player.getName()).getName()))
						&& c.getHeroChatFourChannel().isEnabled()
						&& !MonsterIRC.getHookManager().getHeroChatHook()
								.getChannelManager().getMutelist()
								.contains(player.getName())
						&& !c.getHeroChatFourChannel().getMutelist()
								.contains(player.getName())) {
					if (getHandleManager().getPermissionsHandler()
							.anyGroupsInList(
									player,
									MonsterIRC.getHookManager()
											.getHeroChatHook()
											.getChannelManager()
											.getActiveChannel(player.getName())
											.getVoicelist())
							|| MonsterIRC.getHookManager().getHeroChatHook()
									.getChannelManager()
									.getActiveChannel(player.getName())
									.getVoicelist().isEmpty()) {
						IRC.sendMessage(
								c,
								IRCColor.formatMCMessage(MonsterIRC
										.getHookManager().getHeroChatHook()
										.getChannelManager()
										.getActiveChannel(player.getName())
										.getColor().str
										+ "["
										+ MonsterIRC
												.getHookManager()
												.getHeroChatHook()
												.getChannelManager()
												.getActiveChannel(
														player.getName())
												.getNick()
										+ "] : "
										+ IRCColor.NORMAL.getIRCColor()
										+ result.toString()));
					}
				}
			}
		} else if (c.getChatType() == ChatType.GLOBAL) {
			if (MonsterIRC.getHookManager().getmcMMOHook() != null) {
				if (MonsterIRC.getHookManager().getmcMMOHook()
						.getPlayerProfile(player).getAdminChatMode()) {
					return;
				}
			}
			IRC.sendMessage(c, IRCColor.formatMCMessage(result.toString()));
		} else if (c.getChatType() == ChatType.TOWNYCHAT) {
			if (MonsterIRC.getHookManager().getmcMMOHook() != null) {
				if (MonsterIRC.getHookManager().getmcMMOHook()
						.getPlayerProfile(player).getAdminChatMode()) {
					return;
				}
			}
			IRC.sendMessage(c, IRCColor.formatMCMessage(result.toString()));
		}
	}
}
