package org.monstercraft.irc.plugin.wrappers;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.util.ColorUtils;

/**
 * This class creates an IRCCommand sender.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRCCommandSender implements ConsoleCommandSender {

    private final String sender;

    /**
     * Creates an instance of the IRCCommand sender.
     * 
     * @param plugin
     *            The plugin.
     * @param sender
     *            The command sender's name.
     */
    public IRCCommandSender(final String sender) {
        this.sender = sender;
    }

    /**
     * Sends a message.
     */

    @Override
    public void sendMessage(final String message) {
        IRC.sendNotice(sender, ColorUtils.formatGametoIRC(message));
    }

    /**
     * Fetches the name.
     * 
     * @return The name.
     */

    @Override
    public String getName() {
        return sender;
    }

    /**
     * Fetches the server.
     * 
     * @return The server.
     */

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    /**
     * Ignored.
     */

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    /**
     * Ignored.
     */

    @Override
    public boolean hasPermission(final String arg0) {
        return true;
    }

    /**
     * Ignored.
     */

    @Override
    public boolean hasPermission(final Permission arg0) {
        return true;
    }

    /**
     * Ignored.
     */

    @Override
    public boolean isPermissionSet(final String arg0) {
        return true;
    }

    /**
     * Ignored.
     */

    @Override
    public boolean isPermissionSet(final Permission arg0) {
        return true;
    }

    /**
     * Ignored.
     */

    @Override
    public void recalculatePermissions() {
    }

    /**
     * Ignored.
     */

    @Override
    public void removeAttachment(final PermissionAttachment arg0) {
    }

    /**
     * Ignored.
     */

    @Override
    public boolean isOp() {
        return true;
    }

    /**
     * Ignored.
     */

    @Override
    public void setOp(final boolean op) {
    }

    /**
     * Ignored.
     */

    @Override
    public PermissionAttachment addAttachment(final Plugin arg0) {
        return null;
    }

    /**
     * Ignored.
     */

    @Override
    public PermissionAttachment addAttachment(final Plugin arg0, final int arg1) {
        return null;
    }

    /**
     * Ignored.
     */

    @Override
    public PermissionAttachment addAttachment(final Plugin arg0,
            final String arg1, final boolean arg2) {
        return null;
    }

    /**
     * Ignored.
     */

    @Override
    public PermissionAttachment addAttachment(final Plugin arg0,
            final String arg1, final boolean arg2, final int arg3) {
        return null;
    }

    @Override
    public void sendMessage(final String[] message) {
    }

    @Override
    public void abandonConversation(final Conversation arg0) {
    }

    @Override
    public void acceptConversationInput(final String arg0) {

    }

    @Override
    public boolean beginConversation(final Conversation arg0) {
        return false;
    }

    @Override
    public boolean isConversing() {
        return false;
    }

    @Override
    public void sendRawMessage(final String message) {
        IRC.sendNotice(sender, ColorUtils.formatGametoIRC(message));
    }

    @Override
    public void abandonConversation(final Conversation arg0,
            final ConversationAbandonedEvent arg1) {
    }

}
