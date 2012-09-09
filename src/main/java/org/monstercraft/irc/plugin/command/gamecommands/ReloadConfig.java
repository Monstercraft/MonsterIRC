package org.monstercraft.irc.plugin.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.command.GameCommand;
import org.monstercraft.irc.plugin.util.ColorUtils;

public class ReloadConfig extends GameCommand {

    @Override
    public boolean canExecute(final CommandSender sender, final String[] split) {
        return split[0].equalsIgnoreCase("irc")
                && split[1].equalsIgnoreCase("reload");
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
        sender.sendMessage("Attmpting to reload the configs and connection!");
        final Thread t = new Thread(connect);
        t.setPriority(Thread.MAX_PRIORITY);
        t.setDaemon(false);
        t.start();
        sender.sendMessage("Successfully reloaded the configs and connection!");
        return true;
    }

    private final Runnable connect = new Runnable() {
        public void run() {
            MonsterIRC.getSettingsManager().reload();
            MonsterIRC.getHandleManager().getPluginHandler().stopPlugins();
            MonsterIRC.getHandleManager().setIRCPluginHandler();
            MonsterIRC.getHandleManager().getIRCHandler()
                    .connect(IRC.getServer());
        }
    };

    @Override
    public String getPermission() {
        return "irc.reload";
    }

    @Override
    public String getCommandName() {
        return "reload";
    }

    @Override
    public String[] getHelp() {
        return new String[] {
                ColorUtils.RED.getMinecraftColor() + "Command: "
                        + ColorUtils.WHITE.getMinecraftColor() + "Reload",
                ColorUtils.RED.getMinecraftColor() + "Description: "
                        + ColorUtils.WHITE.getMinecraftColor()
                        + "Reloads and Reconnects MonsterIRC.",
                ColorUtils.RED.getMinecraftColor() + "Usage: "
                        + ColorUtils.WHITE.getMinecraftColor() + "/irc reload" };
    }

}
