package org.monstercraft.irc.util;

import org.bukkit.entity.Player;
import org.monstercraft.irc.command.Command;

import com.nijiko.permissions.PermissionHandler;

public class PermissionsManager {

	private PermissionHandler perms;

	public PermissionsManager(PermissionHandler perms) {
		this.perms = perms;
	}

	public boolean hasCommandPerms(Player p, Command c) {
		if (perms != null) {
			return perms.has(p, "irc.admin")
					|| perms.has(p, c.getPermissions());
		} else {
			return false;
		}
	}

}
