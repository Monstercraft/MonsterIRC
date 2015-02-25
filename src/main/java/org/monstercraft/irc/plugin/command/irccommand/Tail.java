package org.monstercraft.irc.plugin.command.irccommand;

import java.util.ArrayList;

import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Tail extends IRCCommand {

    @Override
    public boolean canExecute(final String sender, final String message,
            final IRCChannel channel) {
        return MonsterIRC.getHandleManager().getIRCHandler().isConnected()
                && IRC.isOp(channel, sender)
                && message.toLowerCase().startsWith(
                        Variables.commandPrefix + "tail");
    }

    @Override
    public boolean execute(final String sender, final String message,
            final IRCChannel channel) {
        int size = 25;
        if (message.length() > 6) {
            if (this.validNumber(message.substring(6))) {
                size = Integer.parseInt(message.substring(6));
            }
        }
        final ArrayList<String> records = MonsterIRC.getLogHandler()
                .getLastRecords(size);
        if (records.isEmpty()) {
            IRC.sendNotice(sender, "No records found!");
            return true;
        }
        for (final String s : records) {
            IRC.sendNotice(sender, s);
        }
        return true;
    }

    public boolean validNumber(final String in) {
        try {

            Integer.parseInt(in);
        } catch (final NumberFormatException ex) {
            return false;
        }
        return true;
    }
}
