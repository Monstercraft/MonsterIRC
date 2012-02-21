package org.monstercraft.irc.plugin.wrappers;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
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
	@Override
	public void sendMessage(String message) {
		IRC.sendNotice(sender, IRCColor.formatMCMessage(message));
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
	public boolean hasPermission(String arg0) {
		return true;
	}

	/**
	 * Ignored.
	 */
	@Override
	public boolean hasPermission(Permission arg0) {
		return true;
	}

	/**
	 * Ignored.
	 */
	@Override
	public boolean isPermissionSet(String arg0) {
		return true;
	}

	/**
	 * Ignored.
	 */
	@Override
	public boolean isPermissionSet(Permission arg0) {
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
	public void removeAttachment(PermissionAttachment arg0) {
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
	public void setOp(boolean op) {
	}

	/**
	 * Ignored.
	 */
	@Override
	public PermissionAttachment addAttachment(Plugin arg0) {
		return null;
	}

	/**
	 * Ignored.
	 */
	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return null;
	}

	/**
	 * Ignored.
	 */
	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2) {
		return null;
	}

	/**
	 * Ignored.
	 */
	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2, int arg3) {
		return null;
	}

}
