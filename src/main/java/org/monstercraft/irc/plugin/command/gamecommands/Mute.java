package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;

public class Mute extends GameCommand {

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split[0].contains("irc") && split[1].equalsIgnoreCase("mute");
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] split) {
        if (sender instanceof Player) {
            if (MonsterIRC.getHandleManager().getPermissionsHandler() != null) {
                if (!MonsterIRC.getHandleManager().getPermissionsHandler()
                        .hasCommandPerms(((Player) sender), this)) {
                    sender.sendMessage("[IRC] You don't have permission to preform that command.");
                    return true;
                }
            } else {
                sender.sendMessage("[IRC] Permissions not detected, unable to run any IRC commands.");
                return true;
            }
        }
        if (!Variables.muted.contains(split[2].toString().toLowerCase())) {
            Variables.muted.add(split[2].toString().toLowerCase());
            MonsterIRC.getSettingsManager().saveMuted();
            sender.sendMessage("Player " + split[2].toString()
                    + " has been muted from talking via IRC.");
        } else {
            sender.sendMessage("Player " + split[2].toString()
                    + "is already muted from talking via IRC.");
            return true;
        }
        return Variables.muted.contains(split[2].toString().toLowerCase());
    }

    @Override
    public String getPermission() {
        return "irc.mute";
    }

    @Override
    public String[] getHelp() {
        return new String[] {
                ColorUtils.RED.getMinecraftColor() + "Command: "
                        + ColorUtils.WHITE.getMinecraftColor() + "Mute",
                ColorUtils.RED.getMinecraftColor() + "Description: "
                        + ColorUtils.WHITE.getMinecraftColor()
                        + "Mutes a IRC users chat from passing to minecraft.",
                ColorUtils.RED.getMinecraftColor() + "Usage: "
                        + ColorUtils.WHITE.getMinecraftColor()
                        + "/irc mute (user)" };
    }

    @Override
    public String getCommandName() {
        return "Mute";
    }

}
