package org.monstercraft.irc.plugin.command.gamecommands;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Join extends GameCommand {

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split[0].contains("irc") && split[1].equalsIgnoreCase("join");
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
                sender.sendMessage("[IRC] PEX not detected, unable to run any IRC commands.");
                return true;
            }
        }
        if (split.length < 3) {
            sender.sendMessage("[IRC] Please specify a channel to join!");
            return false;
        }
        if (split[2] == null) {
            sender.sendMessage("[IRC] Please specify a channel to join!");
            return false;
        }
        for (final IRCChannel c : Variables.channels) {
            if (c.getChannel().equalsIgnoreCase(split[2])) {
                try {
                    MonsterIRC.getHandleManager().getIRCHandler().join(c);
                } catch (final IOException e) {
                    sender.sendMessage("Error joining the channel!");
                    return true;
                }
                sender.sendMessage("[IRC] Successfully joined the channel!");
                return true;
            }
        }
        sender.sendMessage("[IRC] Could not join that channel!");
        return true;
    }

    @Override
    public String getPermission() {
        return "irc.join";
    }

    @Override
    public String[] getHelp() {
        return new String[] {
                ColorUtils.RED.getMinecraftColor() + "Command: "
                        + ColorUtils.WHITE.getMinecraftColor() + "Disconnect",
                ColorUtils.RED.getMinecraftColor() + "Description: "
                        + ColorUtils.WHITE.getMinecraftColor()
                        + "Disconnects the bot from IRC channel.",
                ColorUtils.RED.getMinecraftColor() + "Usage: "
                        + ColorUtils.WHITE.getMinecraftColor() + "/connect" };
    }

    @Override
    public String getCommandName() {
        return "Join";
    }

}
