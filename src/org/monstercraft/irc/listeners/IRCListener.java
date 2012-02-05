package org.monstercraft.irc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.hooks.HeroChatHook;
import org.monstercraft.irc.hooks.PermissionsExHook;
import org.monstercraft.irc.hooks.PermissionsHook;
import org.monstercraft.irc.hooks.mcMMOHook;
import org.monstercraft.irc.util.ChatType;
import org.monstercraft.irc.util.IRCColor;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;

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
						new PermissionsHook(plugin));
				IRC.getHandleManager().setPermissionsHandler(
						IRC.getHookManager().getPermissionsHook());
			} else if (PluginName.equals("PermissionsEx")) {
				IRC.getHookManager().setPermissionsExHook(
						new PermissionsExHook(plugin));
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
					if (c.getChatType() == ChatType.ADMINCHAT) {
						if (IRC.getHookManager().getmcMMOHook() != null) {
							if (IRC.getHookManager().getmcMMOHook()
									.getPlayerProfile(player)
									.getAdminChatMode()) {
								StringBuffer result = new StringBuffer();
								result.append("<" + player.getName() + ">"
										+ " ");
								result.append(event.getMessage());
								IRC.getHandleManager()
										.getIRCHandler()
										.sendMessage(
												formatMessage(result.toString()),
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
							StringBuffer result = new StringBuffer();
							result.append("<" + player.getName() + ">" + " ");
							result.append(event.getMessage());
							IRC.getHandleManager()
									.getIRCHandler()
									.sendMessage(
											formatMessage(result.toString()),
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
									StringBuffer result = new StringBuffer();
									result.append("<" + player.getName() + ">"
											+ " ");
									result.append(event.getMessage());
									IRC.getHandleManager()
											.getIRCHandler()
											.sendMessage(
													formatMessage(result
															.toString()),
													c.getChannel());
								}
							}
						}
					} else if (c.getChatType() == ChatType.ALL) {
						if (IRC.getHookManager().getmcMMOHook() != null) {
							if (IRC.getHookManager().getmcMMOHook()
									.getPlayerProfile(player)
									.getAdminChatMode()) {
								continue;
							}
						}
						StringBuffer result = new StringBuffer();
						result.append("<" + player.getName() + ">" + " ");
						result.append(event.getMessage());
						IRC.getHandleManager()
								.getIRCHandler()
								.sendMessage(formatMessage(result.toString()),
										c.getChannel());
					}
				}
			}
		} catch (Exception e) {
			debug(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (Variables.joinAndQuit) {
			for (IRCChannel c : Variables.channels) {
				IRC.getHandleManager()
						.getIRCHandler()
						.sendMessage(
								IRCColor.RED.getIRCColor()
										+ event.getPlayer().getName()
										+ " has joined the server.",
								c.getChannel());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (Variables.joinAndQuit) {
			for (IRCChannel c : Variables.channels) {
				IRC.getHandleManager()
						.getIRCHandler()
						.sendMessage(
								IRCColor.RED.getIRCColor()
										+ event.getPlayer().getName()
										+ " has left the server.",
								c.getChannel());
			}
		}
	}

	private String formatMessage(final String message) {
		String msg = message;
		if (Variables.colors) {
			for (IRCColor c : IRCColor.values()) {
				if (msg.contains(c.getMinecraftColor())) {
					msg = msg.replace(c.getMinecraftColor(), c.getIRCColor());
				}
			}
		} else {
			for (IRCColor c : IRCColor.values()) {
				if (msg.contains(c.getMinecraftColor())) {
					msg = msg.replace(c.getMinecraftColor(), "");
				}
			}
		}
		return msg;
	}
}
