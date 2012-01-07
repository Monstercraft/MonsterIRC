package org.monstercraft.irc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.util.Variables;

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
			Player player = event.getPlayer();
			if (plugin.getHookManager().getmcMMOHook() != null) {
				if (plugin.getHookManager().getmcMMOHook()
						.getPlayerProfile(player).getAdminChatMode()) {
					return;
				}
			}
			if (Variables.all) {
				StringBuffer result = new StringBuffer();
				result.append("<" + player.getName() + ">" + " ");
				result.append(event.getMessage());
				plugin.getHandleManager().getIRCHandler()
						.sendMessage(result.toString(), Variables.channel);
			} else if (Variables.hc
					&& plugin.getHookManager().getHeroChatHook() != null) {
				if (plugin.getHookManager().getHeroChatHook()
						.getChannelManager().getActiveChannel(player.getName())
						.getName().equals(Variables.hcc)
						&& plugin.getHookManager().getHeroChatHook()
								.getChannelManager().getChannel(Variables.hcc)
								.isEnabled()
						&& !plugin.getHookManager().getHeroChatHook()
								.getChannelManager().getMutelist()
								.contains(player.getName())
						&& !plugin.getHookManager().getHeroChatHook()
								.getChannelManager().getChannel(Variables.hcc)
								.getMutelist().contains(player.getName())) {
					if (plugin
							.getHandleManager()
							.getPermissionsHandler()
							.anyGroupsInList(
									player,
									plugin.getHookManager().getHeroChatHook()
											.getChannelManager()
											.getActiveChannel(player.getName())
											.getVoicelist())
							|| plugin.getHookManager().getHeroChatHook()
									.getChannelManager()
									.getActiveChannel(player.getName())
									.getVoicelist().isEmpty()) {
						StringBuffer result = new StringBuffer();
						result.append("<" + player.getName() + ">" + " ");
						result.append(event.getMessage());
						plugin.getHandleManager()
								.getIRCHandler()
								.sendMessage(result.toString(),
										Variables.channel);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
