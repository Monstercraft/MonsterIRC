package org.monstercraft.irc.plugin.handlers;

import java.util.List;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.Methods;

/**
 * This handles all of the plugins permissions.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRCPermissionsHandler extends IRC {

	private Permission perms = null;

	/**
	 * Creates an instance of the PermissionsHandler class.
	 * 
	 * @param perms
	 *            The Permissions hooks handler.
	 */
	public IRCPermissionsHandler(final Permission perms) {
		this.perms = perms;
	}

	/**
	 * Checks if the player has access to the command.
	 * 
	 * @param player
	 *            The player executing the command.
	 * @param command
	 *            The command being executed.
	 * @return True if the player has permission; otherwise false.
	 */
	public boolean hasCommandPerms(final Player player,
			final GameCommand command) {
		if (IRC.getHookManager().getPermissionsHook() != null) {
			if (perms != null) {
				return perms.has(player, "irc.admin")
						|| perms.has(player, command.getPermissions())
						|| player.isOp();
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Checks if a player is in a list.
	 * 
	 * @param player
	 *            The player to check.
	 * @param list
	 *            The list to check.
	 * @return True if the list contains their name; otherwise false.
	 */
	public boolean anyGroupsInList(final Player player, final List<String> list) {
		String[] groups = getGroups(player);
		for (int i = 0; i < groups.length; i++) {
			if (list.contains(groups[i]))
				return true;
		}
		return false;
	}

	/**
	 * Sets the players groups.
	 * 
	 * @param player
	 *            The player to set groups for.
	 * @return The groups the player is in.
	 */
	public String[] getGroups(final Player player) {
		if (perms != null) {
			try {
				String world = player.getWorld().getName();
				String name = player.getName();
				return perms.getPlayerGroups(world, name);
			} catch (Exception e) {
				Methods.log(e.getMessage());
			}
		}
		return new String[0];
	}

	/**
	 * Fetches the permissions.
	 * 
	 * @return The permission.
	 */
	public Permission getPermission() {
		return perms;
	}

}
