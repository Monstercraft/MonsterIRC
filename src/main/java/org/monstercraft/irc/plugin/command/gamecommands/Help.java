package org.monstercraft.irc.plugin.command.gamecommands;

import java.util.LinkedList;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;

public class Help extends GameCommand {

    public static void sendMenu(final ConsoleCommandSender sender) {
        sender.sendMessage("----- MonsterIRCs Commands ----");
        sender.sendMessage("irc connect - Connects the Bot to the IRC server.");
        sender.sendMessage("irc disconnect - Disconnects the Bot from the IRC server.");
        sender.sendMessage("irc join (channel) - Connects the Bot to the channel");
        sender.sendMessage("irc leave (channel) - Disconnects the bot from the channel.");
        sender.sendMessage("irc ban (user) - Kicks and Bans a user from the IRC channel if your bot has OP.");
        sender.sendMessage("irc mute (user) - Stops a IRC users chat from appearing ingame.");
        sender.sendMessage("irc unmute (user) - Allows a muted IRC users chat to appear ingame.");
        sender.sendMessage("irc nick (new nick) - Changes the IRC bots nickname in IRC for that session.");
        sender.sendMessage("irc say (message) - An alternate way to talk to people in IRC.");
        sender.sendMessage("irc reload - Reloads the configuration file.");
        sender.sendMessage("irc pm (user) (message) - PM a user in the IRC channel.");
        sender.sendMessage("irc r (message) - Reply to the last pm.");
        sender.sendMessage("irc names (channel) - List the users connected to the channel.");
        sender.sendMessage("irc whois (name) - Get the information of an IRC user.");
        sender.sendMessage("For more info on a certian command type"
                + ColorUtils.WHITE.getMinecraftColor() + "/irc help (command)");
    }

    public static void sendMenu(final Player sender) {
        sender.sendMessage(ColorUtils.DARK_BLUE.getMinecraftColor() + "----- ["
                + ColorUtils.WHITE.getMinecraftColor() + "MonsterIRC Help"
                + ColorUtils.DARK_BLUE.getMinecraftColor() + "]-----");
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new Connect())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor()
                    + "/irc connect ");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new Disconnect())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor()
                    + "/irc disconnect");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new ReloadConfig())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor()
                    + "/irc reload");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new Join())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor()
                    + "/irc join" + ColorUtils.DARK_GRAY.getMinecraftColor()
                    + " (channel)");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new Leave())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor()
                    + "/irc leave" + ColorUtils.DARK_GRAY.getMinecraftColor()
                    + " (channel)");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new Ban())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor()
                    + "/irc ban" + ColorUtils.DARK_GRAY.getMinecraftColor()
                    + " (user)");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new Mute())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor()
                    + "/irc mute" + ColorUtils.DARK_GRAY.getMinecraftColor()
                    + " (user)");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new Unmute())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor()
                    + "/irc unmute" + ColorUtils.DARK_GRAY.getMinecraftColor()
                    + " (user)");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new Nick())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor()
                    + "/irc nick" + ColorUtils.DARK_GRAY.getMinecraftColor()
                    + " (nick)");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new Say())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor()
                    + "/irc say" + ColorUtils.DARK_GRAY.getMinecraftColor()
                    + " (message)");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new PrivateMessage())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor() + "/irc pm"
                    + ColorUtils.DARK_GRAY.getMinecraftColor()
                    + " (user) (message)");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new PrivateMessage())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor() + "/irc r"
                    + ColorUtils.DARK_GRAY.getMinecraftColor() + " (message)");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new PrivateMessage())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor()
                    + "/irc raw" + ColorUtils.DARK_GRAY.getMinecraftColor()
                    + " (message)");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new PrivateMessage())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor()
                    + "/irc names" + ColorUtils.DARK_GRAY.getMinecraftColor()
                    + " (channel)");
        }
        if (MonsterIRC.getHandleManager().getPermissionsHandler()
                .hasCommandPerms(sender, new PrivateMessage())) {
            sender.sendMessage(ColorUtils.GREEN.getMinecraftColor()
                    + "/irc whois" + ColorUtils.DARK_GRAY.getMinecraftColor()
                    + " (name)");
        }
        sender.sendMessage(ColorUtils.YELLOW.getMinecraftColor()
                + "For more info on a certian command type"
                + ColorUtils.WHITE.getMinecraftColor() + "/irc help (command)");
    }

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split[0].contains("irc") && split[1].equalsIgnoreCase("help");
    }

    public boolean CommandHelp(final LinkedList<GameCommand> gameCommands,
            final CommandSender sender, final String[] split) {
        for (final GameCommand c : gameCommands) {
            if (split[2].equalsIgnoreCase(c.getCommandName())) {
                for (final String s : c.getHelp()) {
                    sender.sendMessage(s);
                }
                return true;
            }
        }
        sender.sendMessage("Invalid command name!");
        return true;
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] split) {
        if (sender instanceof Player) {
            if (MonsterIRC.getHandleManager().getPermissionsHandler() != null) {
                if (!MonsterIRC.getHandleManager().getPermissionsHandler()
                        .hasCommandPerms((Player) sender, this)) {
                    sender.sendMessage("[IRC] You don't have permission to preform that command.");
                    return true;
                } else {
                    Help.sendMenu((Player) sender);
                    return true;
                }
            }
            sender.sendMessage("[IRC]  No permissions loaded!");
            return true;
        } else {
            Help.sendMenu((ConsoleCommandSender) sender);
            return true;
        }
    }

    @Override
    public String getCommandName() {
        return "Help";
    }

    @Override
    public String[] getHelp() {
        return new String[] {
                ColorUtils.RED.getMinecraftColor() + "Command: "
                        + ColorUtils.WHITE.getMinecraftColor() + "Help",
                ColorUtils.RED.getMinecraftColor() + "Description: "
                        + ColorUtils.WHITE.getMinecraftColor()
                        + "All help related commands",
                ColorUtils.RED.getMinecraftColor() + "Usage: "
                        + ColorUtils.WHITE.getMinecraftColor() + "/help" };
    }

    @Override
    public String getPermission() {
        return "irc.help";
    }
}
