package org.monstercraft.irc.plugin.wrappers;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.util.IRCColor;

/**
 * This class creates an IRCCommand sender.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRCCommandSender implements ConsoleCommandSender {

	private String sender;

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

	public void sendMessage(String message) {
		IRC.sendNotice(sender, IRCColor.formatMCMessage(message));
	}

	/**
	 * Fetches the name.
	 * 
	 * @return The name.
	 */

	public String getName() {
		return sender;
	}

	/**
	 * Fetches the server.
	 * 
	 * @return The server.
	 */

	public Server getServer() {
		return Bukkit.getServer();
	}

	/**
	 * Ignored.
	 */

	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return null;
	}

	/**
	 * Ignored.
	 */

	public boolean hasPermission(String arg0) {
		return true;
	}

	/**
	 * Ignored.
	 */

	public boolean hasPermission(Permission arg0) {
		return true;
	}

	/**
	 * Ignored.
	 */

	public boolean isPermissionSet(String arg0) {
		return true;
	}

	/**
	 * Ignored.
	 */

	public boolean isPermissionSet(Permission arg0) {
		return true;
	}

	/**
	 * Ignored.
	 */

	public void recalculatePermissions() {
	}

	/**
	 * Ignored.
	 */

	public void removeAttachment(PermissionAttachment arg0) {
	}

	/**
	 * Ignored.
	 */

	public boolean isOp() {
		return true;
	}

	/**
	 * Ignored.
	 */

	public void setOp(boolean op) {
	}

	/**
	 * Ignored.
	 */

	public PermissionAttachment addAttachment(Plugin arg0) {
		return null;
	}

	/**
	 * Ignored.
	 */

	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return null;
	}

	/**
	 * Ignored.
	 */

	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2) {
		return null;
	}

	/**
	 * Ignored.
	 */

	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2, int arg3) {
		return null;
	}

	public void sendMessage(String[] message) {
	}

	public void abandonConversation(Conversation arg0) {
	}

	public void acceptConversationInput(String arg0) {

	}

	public boolean beginConversation(Conversation arg0) {
		return false;
	}

	public boolean isConversing() {
		return false;
	}

	public void sendRawMessage(String message) {
		IRC.sendNotice(sender, IRCColor.formatMCMessage(message));
	}

}
