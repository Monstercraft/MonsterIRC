package org.monstercraft.irc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.util.Variables;

public class IRCPlayerListener extends PlayerListener {
	private IRC plugin;

	public IRCPlayerListener(IRC plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		try {
			Player player = event.getPlayer();
			if (plugin.mcmmo.mcMMOHook != null) {
				if (plugin.mcmmo.mcMMOHook.getPlayerProfile(player)
						.getAdminChatMode()) {
					return;
				}
			}
			if (Variables.all) {
				StringBuffer result = new StringBuffer();
				result.append("<" + player.getName() + ">" + " ");
				result.append(event.getMessage());
				plugin.IRC.sendMessage(result.toString());
			} else if (Variables.hc && plugin.herochat.HeroChatHook != null) {
				if (plugin.herochat.HeroChatHook.getChannelManager()
						.getActiveChannel(player.getName()).getName()
						.equals(Variables.hcc)
						&& plugin.herochat.HeroChatHook.getChannelManager()
								.getChannel(Variables.hcc).isEnabled()
						&& !plugin.herochat.HeroChatHook.getChannelManager()
								.getMutelist().contains(player.getName())
						&& !plugin.herochat.HeroChatHook.getChannelManager()
								.getChannel(Variables.hcc).getMutelist()
								.contains(player.getName())) {
					if (plugin.perms.anyGroupsInList(player,
							plugin.herochat.HeroChatHook.getChannelManager()
									.getActiveChannel(player.getName())
									.getVoicelist())
							|| plugin.herochat.HeroChatHook.getChannelManager()
									.getActiveChannel(player.getName())
									.getVoicelist().isEmpty()) {
						StringBuffer result = new StringBuffer();
						result.append("<" + player.getName() + ">" + " ");
						result.append(event.getMessage());
						plugin.IRC.sendMessage(result.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
