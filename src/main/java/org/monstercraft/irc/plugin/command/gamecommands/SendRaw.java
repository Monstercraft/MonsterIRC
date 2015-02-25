package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;

public class SendRaw extends GameCommand {

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split[0].equalsIgnoreCase("irc")
                && split[1].equalsIgnoreCase("raw");
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
        if (split.length <= 2) {
            sender.sendMessage("Invalid usage!");
            sender.sendMessage("Type /help raw for more help!");
            return true;
        }
        final StringBuffer result = new StringBuffer();
        for (int i = 2; i < split.length; i++) {
            result.append(split[i]);
            result.append(" ");
        }

        IRC.sendRawLine(result.toString());
        IRC.log(result.toString());
        sender.sendMessage(ColorUtils.BLUE.getMinecraftColor()
                + "Raw message sent!");
        return true;
    }

    @Override
    public String getCommandName() {
        return "Raw";
    }

    @Override
    public String[] getHelp() {
        return new String[] {
                ColorUtils.RED.getMinecraftColor() + "Command: "
                        + ColorUtils.WHITE.getMinecraftColor() + "Send Raw",
                ColorUtils.RED.getMinecraftColor() + "Description: "
                        + ColorUtils.WHITE.getMinecraftColor()
                        + "Sends a raw message to the IRC server",
                ColorUtils.RED.getMinecraftColor() + "Usage: "
                        + ColorUtils.WHITE.getMinecraftColor()
                        + "/irc raw (message)" };
    }

    @Override
    public String getPermission() {
        return "irc.raw";
    }

}
