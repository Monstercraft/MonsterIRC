package org.monstercraft.irc.plugin.command;

import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

/**
 * This class is the Abstract IRC command.
 *
 * @author fletch_to_99 <fletchto99@hotmail.com>
 *
 */
public abstract class IRCCommand extends MonsterIRC {

    /**
     * Checks if the command can be executed by the certian user.
     *
     * @param sender
     *            The command sender.
     * @param message
     *            The commands arguments in the form of a string.
     * @param channel
     *            The channel the sender is in.
     * @return True if the user is able to execute the command; otherwise false.
     */
    public abstract boolean canExecute(final String sender,
            final String message, final IRCChannel channel);

    /**
     * The action to perfrom when executing the command.
     *
     * @param sender
     *            The command sender.
     * @param message
     *            The arguments of the command in the form of a string.
     * @param channel
     *            The channel the sender is in.
     * @return True if the command executed successfully; otherwise false.
     */
    public abstract boolean execute(final String sender, final String message,
            final IRCChannel channel);
}