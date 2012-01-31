package org.monstercraft.irc.command.gamecommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;
import org.monstercraft.irc.command.GameCommand;
import org.monstercraft.irc.util.ChatType;
import org.monstercraft.irc.util.Variables;
import org.monstercraft.irc.wrappers.IRCChannel;

import com.gmail.nossr50.mcPermissions;

public class Say extends GameCommand {

	public Say(org.monstercraft.irc.IRC plugin) {
		super(plugin);
	}

	public boolean canExecute(CommandSender sender, String[] split) {
		return IRC.getHandleManager().getIRCHandler().isConnected()
				&& split[0].equalsIgnoreCase("irc")
				&& split[1].equalsIgnoreCase("say");
	}

	public boolean execute(CommandSender sender, String[] split) {
		if (sender instanceof Player) {
			if (!IRC.getHandleManager().getPermissionsHandler()
					.hasCommandPerms(((Player) sender), this)) {
				sender.sendMessage("[IRC] You don't have permission to preform that command.");
				return false;
			}
		}
		if (split.length <= 2) {
			sender.sendMessage("Invalid usage!");
			sender.sendMessage("Proper usage: irc say -c:[irc channel] [message]");
			sender.sendMessage("or");
			sender.sendMessage("Proper usage: irc say [message]");
			return false;
		}
		String channel = null;
		int j = 2;
		if (split[2].startsWith("-c:")) {
			String s = split[2].toString();
			channel = s.substring(3);
			j = 3;
		}
		StringBuffer result = new StringBuffer();
		StringBuffer result2 = new StringBuffer();
		result.append("<" + sender.getName() + "> ");
		for (int i = j; i < split.length; i++) {
			result.append(split[i]);
			result.append(" ");
			result2.append(split[i]);
			result2.append(" ");
		}

		for (IRCChannel c : Variables.channels) {
			if (channel != null) {
				if (c.getChannel().equalsIgnoreCase(channel)) {
					IRC.getHandleManager().getIRCHandler()
							.sendMessage(result.toString(), c.getChannel());
					handleMessage(c, sender.getName(), result2.toString());
					break;
				}
			} else {
				if (c.isDefaultChannel()) {
					IRC.getHandleManager().getIRCHandler()
							.sendMessage(result.toString(), c.getChannel());
					handleMessage(c, sender.getName(), result2.toString());
				}
			}
		}
		return false;
	}

	private void handleMessage(IRCChannel c, String name, String message) {
		if (c.getChatType() == ChatType.ADMINCHAT) {
			if (IRC.getHookManager().getmcMMOHook() != null) {
				String format = "§b" + "{" + "§f" + "[IRC] " + name + "§b"
						+ "} " + message;
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if (p.isOp() || mcPermissions.getInstance().adminChat(p))
						p.sendMessage(format);
				}
			}
		} else if (c.getChatType() == ChatType.HEROCHAT && !Variables.hc4) {
			c.getHeroChatChannel().announce(
					Variables.format.substring(0,
							Variables.format.indexOf("{name}"))
							+ name
							+ Variables.format.substring(
									Variables.format.indexOf("{name}") + 6,
									Variables.format.indexOf("{message}"))
							+ message
							+ Variables.format.substring(Variables.format
									.indexOf("{message}") + 9));
		} else if (c.getChatType() == ChatType.HEROCHAT
				&& IRC.getHookManager().getHeroChatHook() != null
				&& Variables.hc4) {
			c.getHeroChatFourChannel().sendMessage("<" + name + ">", message,
					c.getHeroChatFourChannel().getMsgFormat(), false);
		} else if (c.getChatType() == ChatType.ALL) {
			plugin.getServer().broadcastMessage(
					"[IRC]"
							+ Variables.format.substring(0,
									Variables.format.indexOf("{name}"))
							+ name
							+ Variables.format.substring(
									Variables.format.indexOf("{name}") + 6,
									Variables.format.indexOf("{message}"))
							+ message
							+ Variables.format.substring(Variables.format
									.indexOf("{message}") + 9));
		}
	}

	@Override
	public String getPermissions() {
		return "irc.say";
	}

}
