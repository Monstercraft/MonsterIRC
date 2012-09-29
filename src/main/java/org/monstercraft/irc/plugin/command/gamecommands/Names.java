package org.monstercraft.irc.plugin.command.gamecommands;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.monstercraft.irc.plugin.wrappers.IRCClient;

public class Names extends GameCommand {

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split[0].contains("irc") && split[1].equalsIgnoreCase("names");
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
                    + "Invalid command usage, type /irc help names for more help!");
            return true;
        }
        IRCChannel c;
        if ((c = IRC.getChannel(split[2])) != null) {
            final ArrayList<String> users = new ArrayList<String>();
            for (final IRCClient client : c.getUsers()) {
                users.add(client.getPrefix() + client.getNick());
            }
            Collections.sort(users);
            for (final String user : users) {
                sender.sendMessage(ChatColor.BOLD + "" + ChatColor.BLUE + user);
            }
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Channel not found!");
        return true;
    }

    @Override
    public String getPermission() {
        return "irc.names";
    }

    @Override
    public String[] getHelp() {
        return new String[] {
                ColorUtils.RED.getMinecraftColor() + "Command: "
                        + ColorUtils.WHITE.getMinecraftColor() + "Names",
                ColorUtils.RED.getMinecraftColor() + "Description: "
                        + ColorUtils.WHITE.getMinecraftColor()
                        + "Lists all of the users in an IRC channel.",
                ColorUtils.RED.getMinecraftColor() + "Usage: "
                        + ColorUtils.WHITE.getMinecraftColor()
                        + "/irc names (channel)" };
    }

    @Override
    public String getCommandName() {
        return "Names";
    }

}
