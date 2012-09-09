package org.monstercraft.irc.plugin.command.irccommand;

import org.bukkit.Bukkit;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Announce extends IRCCommand {

    @Override
    public boolean canExecute(final String sender, final String message,
            final IRCChannel channel) {
        return MonsterIRC.getHandleManager().getIRCHandler().isConnected()
                && IRC.isVoicePlus(channel, sender)
                && message.toLowerCase().startsWith(
                        Variables.commandPrefix + "announce");
    }

    @Override
    public boolean execute(final String sender, final String message,
            final IRCChannel channel) {
        if (message.length() < 9) {
            IRC.sendNotice(sender, "No message! Please add a message.");
            return true;
        }
        Bukkit.getServer().broadcastMessage(
                ColorUtils.formatIRCtoGame(
                        "[IRC]<" + sender + ">: " + message.substring(10),
                        message.substring(10)));
        return true;
    }
}
