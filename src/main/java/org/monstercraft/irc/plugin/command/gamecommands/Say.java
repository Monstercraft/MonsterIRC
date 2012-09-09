package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;

public class Say extends GameCommand {

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split[0].equalsIgnoreCase("irc")
                && split[1].equalsIgnoreCase("say");
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
                sender.sendMessage("[IRC] PEX not detected, unable to run any IRC commands.");
                return true;
            }
        }
        if (split.length <= 2) {
            sender.sendMessage("Invalid usage!");
            sender.sendMessage("Proper usage: irc say -c:[irc channel] [message]");
            sender.sendMessage("or");
            sender.sendMessage("Proper usage: irc say [message]");
            return true;
        }
        String channel = null;
        int j = 2;
        if (split[2].startsWith("-c:")) {
            final String s = split[2].toString();
            channel = s.substring(3);
            j = 3;
        }
        final StringBuffer result = new StringBuffer();
        final StringBuffer result2 = new StringBuffer();
        result.append("<" + sender.getName() + "> ");
        for (int i = j; i < split.length; i++) {
            result.append(split[i]);
            result.append(" ");
            result2.append(split[i]);
            result2.append(" ");
        }

        for (final IRCChannel c : Variables.channels) {
            if (channel != null) {
                if (c.getChannel().equalsIgnoreCase(channel)) {
                    MonsterIRC.getHandleManager().getIRCHandler()
                            .sendMessage(c.getChannel(), result.toString());
                    IRC.sendMessageToGame(c, sender.getName(),
                            result2.toString());
                    break;
                }
            } else {
                if (c.isDefaultChannel()) {
                    MonsterIRC.getHandleManager().getIRCHandler()
                            .sendMessage(c.getChannel(), result.toString());
                    IRC.sendMessageToGame(c, sender.getName(),
                            result2.toString());
                }
            }
        }
        return true;
    }

    @Override
    public String getPermission() {
        return "irc.say";
    }

    @Override
    public String getCommandName() {
        return "Say";
    }

    @Override
    public String[] getHelp() {
        return new String[] {
                ColorUtils.RED.getMinecraftColor() + "Command: "
                        + ColorUtils.WHITE.getMinecraftColor() + "Say",
                ColorUtils.RED.getMinecraftColor() + "Description: "
                        + ColorUtils.WHITE.getMinecraftColor()
                        + "Sends a message to all default IRC channels.",
                ColorUtils.RED.getMinecraftColor() + "Usage: "
                        + ColorUtils.WHITE.getMinecraftColor()
                        + "/irc say (message)" };
    }

}
