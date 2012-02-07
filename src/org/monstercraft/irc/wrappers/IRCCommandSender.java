package org.monstercraft.irc.wrappers;

import java.util.Set;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.util.IRCColor;

public class IRCCommandSender implements CommandSender {

	private IRC plugin;
	private String sender;

	public IRCCommandSender(IRC plugin, String sender) {
		this.sender = sender;
		this.plugin = plugin;
	}

	public void sendMessage(String message) {
		IRC.getHandleManager().getIRCHandler()
				.sendNotice(IRCColor.formatMCMessage(message), sender);
	}

	public String getName() {
		return sender;
	}
	
	public Server getServer() {
		return plugin.getServer();
	}

	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return null;
	}

	public boolean hasPermission(String arg0) {
		return true;
	}

	public boolean hasPermission(Permission arg0) {
		return true;
	}

	public boolean isPermissionSet(String arg0) {
		return true;
	}

	public boolean isPermissionSet(Permission arg0) {
		return true;
	}

	public void recalculatePermissions() {
	}

	public void removeAttachment(PermissionAttachment arg0) {
	}

	public boolean isOp() {
		return true;
	}

	public void setOp(boolean op) {
	}
	
	public PermissionAttachment addAttachment(Plugin arg0) {
		return null;
	}

	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return null;
	}

	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2) {
		return null;
	}

	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2, int arg3) {
		return null;
	}

}
