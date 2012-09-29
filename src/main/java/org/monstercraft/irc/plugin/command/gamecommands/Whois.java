package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCClient;

public class Whois extends GameCommand {

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split[0].contains("irc") && split[1].equalsIgnoreCase("whois");
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] split) {
        if (sender instanceof Player) {
            if (MonsterIRC.getHandleManager().getPermissionsHandler() != null) {
                if (!MonsterIRC.getHandleManager().getPermissionsHandler()
                        .hasCommandPerms((Player) sender, this)) {
                    sender.sendMessage("[IRC] You don't have permission to preform that command.");
                    return true;
                }
            } else {
                sender.sendMessage("[IRC] Permissions not detected, unable to run any IRC commands.");
                return true;
            }
        }
        if (split.length != 3) {
            sender.sendMessage(ChatColor.RED
                    + "Invalid command usage, type /irc help whois for more info.");
            return true;
        }
        for (final IRCChannel c : MonsterIRC.getChannels()) {
            IRCClient client;
            if ((client = c.getUser(split[2])) != null) {
                if (client.getNick() != null) {
                    sender.sendMessage(ChatColor.GREEN + "Nickname: "
                            + ChatColor.WHITE + client.getNick());
                }
                if (client.getHighestRank() != null) {
                    sender.sendMessage(ChatColor.GREEN + "Highest Rank: "
                            + ChatColor.WHITE
                            + client.getHighestRank().getName());
                }
                if (client.getHighestRank() != null) {
                    sender.sendMessage(ChatColor.GREEN
                            + "Hostmask: "
                            + ChatColor.WHITE
                            + (client.getHostmask().equalsIgnoreCase("") ? "Not found"
                                    : client.getHostmask()));
                }
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "User not found!");
        return true;
    }

    @Override
    public String getPermission() {
        return "irc.whois";
    }

    @Override
    public String[] getHelp() {
        return new String[] {
                ColorUtils.RED.getMinecraftColor() + "Command: "
                        + ColorUtils.WHITE.getMinecraftColor() + "Whois",
                ColorUtils.RED.getMinecraftColor() + "Description: "
                        + ColorUtils.WHITE.getMinecraftColor()
                        + "Get information on an IRC user",
                ColorUtils.RED.getMinecraftColor() + "Usage: "
                        + ColorUtils.WHITE.getMinecraftColor()
                        + "/whois (name)" };
    }

    @Override
    public String getCommandName() {
        return "Whois";
    }

}
