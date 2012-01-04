package org.monstercraft.irc.handlers;

import java.util.List;

import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.Command;

import com.nijiko.permissions.PermissionHandler;

public class PermissionsHandler {

	private PermissionHandler perms;
	private IRC plugin;

	public PermissionsHandler(PermissionHandler perms, IRC plugin) {
		this.plugin = plugin;
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
	
	public boolean anyGroupsInList(Player p, List<String> list) {
		String[] groups = getGroups(p);
		for (int i = 0; i < groups.length; i++) {
			if (list.contains(groups[i]))
				return true;
		}
		return false;
	}
	
    public String[] getGroups(Player p) {
        if (perms != null) {
            try {
            String world = p.getWorld().getName();
            String name = p.getName();
            return perms.getGroups(world, name);
            } catch (Exception e) {
                plugin.log(e.getMessage());
            }
        }
        return new String[0];
    }

}
