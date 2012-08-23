package org.monstercraft.irc.plugin.handles;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.command.GameCommand;

/**
 * This handles all of the plugins permissions.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRCPermissionsHandler extends MonsterIRC {

	private final Permission perms;

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
		if (MonsterIRC.getHookManager().getPermissionsHook() != null) {
			if (perms != null) {
				return perms.has(player, "irc.admin")
						|| perms.has(player, command.getPermission())
						|| player.isOp();
			} else {
				return player.isOp();
			}
		} else {
			return player.isOp();
		}
	}

	public boolean hasNode(Player player, String node) {
		if ((perms.has(player, "irc.*") || perms.has(player, "irc.admin"))
				&& node.equalsIgnoreCase("irc.nochat")) {
			return false;
		}
		return perms.has(player, node);
	}
}
