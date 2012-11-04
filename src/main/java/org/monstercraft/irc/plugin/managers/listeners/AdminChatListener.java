package org.monstercraft.irc.plugin.managers.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.util.ChatType;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.util.StringUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.support.plugin.events.AdminChatEvent;

public class AdminChatListener implements Listener {

    @EventHandler
    public void onAdminChat(final AdminChatEvent event) {
        for (final IRCChannel c : Variables.channels) {
            if (c.getChatType() == ChatType.MTADMINCHAT) {
                final StringBuffer result = new StringBuffer();
                final String player = event.getSender();
                final String message = event.getMessage();
                result.append(Variables.ircformat
                        .replace("{HCchannelColor}", "")
                        .replace("{heroChatTag}", "")
                        .replace("{heroChatName}", "")
                        .replace("{prefix}", StringUtils.getPrefix(player)

                        )
                        .replace("{name}", StringUtils.getDisplayName(player))
                        .replace("{suffix}", StringUtils.getSuffix(player))

                        .replace("{groupPrefix}",
                                StringUtils.getGroupPrefix(player))
                        .replace("{groupSuffix}",
                                StringUtils.getGroupSuffix(player))
                        .replace("{message}", " " + message)
                        .replace("{mvWorld}", "").replace("{mvColor}", "")
                        .replace("{world}", ""));
                Variables.linesToIrc++;
                IRC.sendMessageToChannel(c,
                        ColorUtils.formatGametoIRC(result.toString()));
            }
        }
    }

}
