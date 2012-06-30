package org.monstercraft.irc.plugin.managers.listeners;

import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
import com.palmergames.bukkit.TownyChat.channels.Channel;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class MonsterIRCListener extends MonsterIRC implements Listener {
	private MonsterIRC plugin;

	/**
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
			} else if (PluginName.equals("TownyChat")) {
				MonsterIRC.getHookManager().setTownyChatHook();
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onChat(PlayerChatEvent event) {
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
		if (event.getJoinMessage() == null) {
			return;
		}
		for (IRCChannel c : Variables.channels) {
			if (c.showJoinLeave()) {
				IRC.sendMessageToChannel(
						c.getChannel(),
						ColorUtils.formatGameMessage(event.getPlayer()
								.getDisplayName().replace("�", "§")
								.replace("&", "§")
								+ " has joined."));
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (event.getQuitMessage() == null) {
			return;
		}
		for (IRCChannel c : Variables.channels) {
			if (c.showJoinLeave()) {
				IRC.sendMessageToChannel(
						c.getChannel(),
						ColorUtils.formatGameMessage(event.getPlayer()
								.getDisplayName().replace("�", "§")
								.replace("&", "§")
								+ " has quit."));
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getDeathMessage() == null) {
			return;
		}
		for (IRCChannel c : Variables.channels) {
			if (c.showDeath()) {
				IRC.sendMessageToChannel(
						c.getChannel(),
						ColorUtils.formatGameMessage(event.getEntity()
								.getDisplayName().replace("�", "§")
								.replace("&", "§")
								+ " has died."));
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.isCancelled() || event.getLeaveMessage() == null) {
			return;
		}
		for (IRCChannel c : Variables.channels) {
			if (c.showJoinLeave()) {
				IRC.sendMessageToChannel(
						c.getChannel(),
						ColorUtils.formatGameMessage(event.getPlayer()
								.getDisplayName().replace("�", "§")
								.replace("&", "§")
								+ " has been kicked."));
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
		if (player != null) {
			if (MonsterIRCListener.getHandleManager().getPermissionsHandler()
					.hasNode(player, "irc.nochat")
					&& !player.isOp()
					&& !MonsterIRCListener.getHandleManager()
							.getPermissionsHandler().hasNode(player, "*")) {
				player.sendMessage("You are blocked from sending messages to irc!");
				return;
			}
		}
		if (!c.passToIRC()) {
			return;
		}
		if (player == null) {
			StringBuffer result2 = new StringBuffer();
			result2.append(Variables.ircformat
					.replace("{HCchannelColor}", "&f")
					.replace("{heroChatTag}", "[Console]")
					.replace(
							"{prefix}",
							StringUtils.getPrefix("Console").replace("�", "§")
									.replace("&", "§"))
					.replace("{name}", StringUtils.getDisplayName("Console"))
					.replace("{suffix}", StringUtils.getSuffix("Console"))
					.replace("{groupPrefix}",
							StringUtils.getGroupPrefix("Console"))
					.replace("{groupSuffix}",
							StringUtils.getGroupSuffix("Console"))
					.replace("{message}",
							message.replace("�", "§").replace("&", "§"))
					.replace("{world}", StringUtils.getWorld("Console"))
					.replace("&", "§"));
			Variables.linesToIrc++;
			IRC.sendMessageToChannel(
					c,
					ColorUtils.formatGameMessage(result2.toString().replace(
							ColorUtils.WHITE.getMinecraftColor(),
							ColorUtils.NORMAL.getIRCColor())));
			return;
		}
		StringBuffer result = new StringBuffer();
		if (c.getChatType() == ChatType.ADMINCHAT) {
			if (Bukkit.getServer().getPluginManager().getPlugin("mcMMO") != null) {
				if (Users.getProfile(player.getName()).getAdminChatMode()) {
					result.append(Variables.ircformat
							.replace("{HCchannelColor}", "&f")
							.replace("{heroChatTag}", "")
							.replace(
									"{prefix}",
									StringUtils.getPrefix(player)
											.replace("�", "§")
											.replace("&", "§"))
							.replace(
									"{name}",
									StringUtils.getDisplayName(player
											.getDisplayName()))
							.replace("{suffix}", StringUtils.getSuffix(player))

							.replace("{groupPrefix}",
									StringUtils.getGroupPrefix(player))
							.replace("{groupSuffix}",
									StringUtils.getGroupSuffix(player))
							.replace(
									"{message}",
									" "
											+ message.replace("�", "§")
													.replace("&", "§"))
							.replace("{world}",
									StringUtils.getWorld(player.getName()))
							.replace("&", "§"));
					Variables.linesToIrc++;
					IRC.sendMessageToChannel(c, ColorUtils
							.formatGameMessage(result.toString().replace(
									ColorUtils.WHITE.getMinecraftColor(),
									ColorUtils.NORMAL.getIRCColor())));
				}
			}
		} else if (c.getChatType() == ChatType.HEROCHAT) {
			if (Bukkit.getServer().getPluginManager().getPlugin("mcMMO") != null) {
				if (Users.getProfile(player.getName()).getAdminChatMode()) {
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
									"§"
											+ Herochat.getChatterManager()
													.getChatter(player)
													.getActiveChannel()
													.getColor().getChar()
											+ "["
											+ Herochat.getChatterManager()
													.getChatter(player)
													.getActiveChannel()
													.getNick() + "]"
											+ ColorUtils.NORMAL.getIRCColor()
											+ "")
							.replace(
									"{prefix}",
									StringUtils.getPrefix(player)
											.replace("�", "§")
											.replace("&", "§"))
							.replace(
									"{name}",
									StringUtils.getDisplayName(player
											.getDisplayName()))
							.replace("{suffix}", StringUtils.getSuffix(player))

							.replace("{groupPrefix}",
									StringUtils.getGroupPrefix(player))
							.replace("{groupSuffix}",
									StringUtils.getGroupSuffix(player))
							.replace(
									"{HCchannelColor}",
									"§"
											+ Herochat.getChatterManager()
													.getChatter(player)
													.getActiveChannel()
													.getColor().getChar())
							.replace(
									"{message}",
									" "
											+ message.replace("�", "§")
													.replace("&", "§"))
							.replace("{world}",
									StringUtils.getWorld(player.getName()))
							.replace("&", "§"));
					Variables.linesToIrc++;
					IRC.sendMessageToChannel(c.getChannel(), ColorUtils
							.formatGameMessage(result.toString().replace(
									ColorUtils.WHITE.getMinecraftColor(),
									ColorUtils.NORMAL.getIRCColor())));
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
			}
			result.append(Variables.ircformat
					.replace("{HCchannelColor}", "&f")
					.replace("{heroChatTag}", "")
					.replace(
							"{prefix}",
							StringUtils.getPrefix(player).replace("�", "§")
									.replace("&", "§"))
					.replace("{name}",
							StringUtils.getDisplayName(player.getDisplayName()))
					.replace("{suffix}", StringUtils.getSuffix(player))

					.replace("{groupPrefix}",
							StringUtils.getGroupPrefix(player))
					.replace("{groupSuffix}",
							StringUtils.getGroupSuffix(player))
					.replace("{message}",
							" " + message.replace("�", "§").replace("&", "§"))
					.replace("{world}", StringUtils.getWorld(player.getName()))
					.replace("&", "§"));
			Variables.linesToIrc++;
			IRC.sendMessageToChannel(
					c,
					ColorUtils.formatGameMessage(result.toString().replace(
							ColorUtils.WHITE.getMinecraftColor(),
							ColorUtils.NORMAL.getIRCColor())));
		} else if (c.getChatType() == ChatType.TOWNYCHAT) {
			if (Bukkit.getServer().getPluginManager().getPlugin("mcMMO") != null) {
				if (Users.getProfile(player.getName()).getAdminChatMode()) {
					return;
				}
			}
			if (directedChat.containsKey(player)) {
				if (directedChat.get(player).equals(c.getTownyChannel())
						&& MonsterIRC.getHandleManager()
								.getPermissionsHandler()
								.hasNode(player, c.getTownyNode())) {
					result.append(Variables.ircformat
							.replace("{HCchannelColor}", "&f")
							.replace("{heroChatTag}", "")
							.replace(
									"{prefix}",
									StringUtils.getPrefix(player)
											.replace("�", "§")
											.replace("&", "§"))
							.replace(
									"{name}",
									StringUtils.getDisplayName(player
											.getDisplayName()))
							.replace("{suffix}", StringUtils.getSuffix(player))

							.replace("{groupPrefix}",
									StringUtils.getGroupPrefix(player))
							.replace("{groupSuffix}",
									StringUtils.getGroupSuffix(player))
							.replace(
									"{message}",
									" "
											+ message.replace("�", "§")
													.replace("&", "§"))
							.replace("{world}",
									StringUtils.getWorld(player.getName()))
							.replace("&", "§"));
					Variables.linesToIrc++;
					IRC.sendMessageToChannel(c, ColorUtils
							.formatGameMessage(result.toString().replace(
									ColorUtils.WHITE.getMinecraftColor(),
									ColorUtils.NORMAL.getIRCColor())));
				}
			}
		}
	}

	private WeakHashMap<Player, Channel> directedChat = new WeakHashMap<Player, Channel>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (MonsterIRC.getHookManager().getTownyChatHook() != null) {
			Player player = event.getPlayer();
			String split[] = event.getMessage().split("\\ ");
			String command = split[0].trim().toLowerCase().replace("/", "");
			Channel channel = MonsterIRC.getHookManager().getTownyChatHook()
					.getChannelsHandler().getChannel(player, command);
			if (channel != null) {
				if (directedChat.containsKey(player)) {
					boolean doReturn = false;
					if (directedChat.get(player).equals(channel)) {
						doReturn = true;
					}
					directedChat.remove(player);
					if (doReturn) {
						return;
					}
				}
				directedChat.put(player, channel);
			}
		}
	}
}