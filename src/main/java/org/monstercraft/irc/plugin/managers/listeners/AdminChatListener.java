package org.monstercraft.irc.plugin.managers.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.util.ChatType;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.support.plugin.events.AdminChatEvent;

public class AdminChatListener implements Listener {

	@EventHandler
	public void onAdminChat(AdminChatEvent event) {
		for (IRCChannel c : Variables.channels) {
			if (c.getChatType() == ChatType.MTADMINCHAT) {
				MonsterIRCListener.handleMessage(event.getSender(), c,
						event.getMessage());
			}
		}
	}

}
