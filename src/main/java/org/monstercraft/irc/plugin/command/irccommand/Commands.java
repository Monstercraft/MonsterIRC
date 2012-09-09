package org.monstercraft.irc.plugin.command.irccommand;

import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.IRCCommand;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Commands extends IRCCommand {

    @Override
    public boolean canExecute(final String sender, final String message,
            final IRCChannel channel) {
        return MonsterIRC.getHandleManager().getIRCHandler().isConnected()
                && message.startsWith(Variables.commandPrefix + "commands");
    }

    @Override
    public boolean execute(final String sender, final String message,
            final IRCChannel channel) {
        if (IRC.isOp(channel, sender)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("As an OP you can use ");
            for (final String string : channel.getOpCommands()) {
                sb.append("\"" + string + "\" ");
            }
            for (final String string : channel.getVoiceCommands()) {
                sb.append("\"" + string + "\" ");
            }
            for (final String string : channel.getUserCommands()) {
                sb.append("\"" + string + "\" ");
            }
            IRC.sendNotice(sender, sb.toString());
            return true;
        } else if (IRC.isVoice(channel, sender)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("As an Voice you can use ");
            for (final String string : channel.getVoiceCommands()) {
                sb.append("\"" + string + "\" ");
            }
            for (final String string : channel.getUserCommands()) {
                sb.append("\"" + string + "\" ");
            }
            IRC.sendNotice(sender, sb.toString());
            return true;
        } else {
            final StringBuilder sb = new StringBuilder();
            sb.append("As an User you can use ");
            for (final String string : channel.getUserCommands()) {
                sb.append("\"" + string + "\" ");
            }
            IRC.sendNotice(sender, sb.toString());
            return true;
        }
    }
}
