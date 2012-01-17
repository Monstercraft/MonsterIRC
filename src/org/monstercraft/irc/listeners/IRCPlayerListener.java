package org.monstercraft.irc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.util.ChatType;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;

/**
 * This class listens for chat ingame to pass to the IRC.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRCPlayerListener extends PlayerListener {
	private IRC plugin;

	/**
	 * Creates an instance of the IRCPlayerListener class.
	 * 
	 * @param plugin
	 *            The parent plugin.
	 */
	public IRCPlayerListener(final IRC plugin) {
		this.plugin = plugin;
	}

	@Override
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
										.sendMessage(result.toString(),
												c.getChannel());
							}
						}
					} else if (c.getChatType() == ChatType.HEROCHAT
							&& IRC.getHookManager().getHeroChatHook() != null) {
						if (IRC.getHookManager().getmcMMOHook() != null) {
							if (IRC.getHookManager().getmcMMOHook()
									.getPlayerProfile(player)
									.getAdminChatMode()) {
								continue;
							}
						}
						if (IRC.getHookManager().getHeroChatHook()
								.getChannelManager()
								.getActiveChannel(player.getName()).getName()
								.equals(c.getHeroChatChannel().getName())
								&& c.getHeroChatChannel().isEnabled()
								&& !IRC.getHookManager().getHeroChatHook()
										.getChannelManager().getMutelist()
										.contains(player.getName())
								&& !c.getHeroChatChannel().getMutelist()
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
									|| IRC.getHookManager().getHeroChatHook()
											.getChannelManager()
											.getActiveChannel(player.getName())
											.getVoicelist().isEmpty()) {
								StringBuffer result = new StringBuffer();
								result.append("<" + player.getName() + ">"
										+ " ");
								result.append(event.getMessage());
								IRC.getHandleManager()
										.getIRCHandler()
										.sendMessage(result.toString(),
												c.getChannel());
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
						IRC.getHandleManager().getIRCHandler()
								.sendMessage(result.toString(), c.getChannel());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
