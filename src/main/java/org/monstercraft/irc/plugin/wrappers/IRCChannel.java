package org.monstercraft.irc.plugin.wrappers;

import java.util.ArrayList;
import java.util.List;

import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.util.ChatType;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Herochat;

/**
 * This class creates an IRC channel to join.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRCChannel {

	private String channel;
	private String ChatChannel;
	private ChatType type;
	private boolean autoJoin;
	private boolean defaultChannel;
	private List<String> opCommands;
	private List<String> voiceCommands;
	private List<String> hopCommands;
	private List<String> adminCommands;
	private List<String> userCommands;
	private List<String> ops;
	private List<String> admins;
	private List<String> hops;
	private List<String> voices;
	private List<String> listenChatChannels;
	private String password;
	private boolean showJoinLeave;
	private boolean passToIRC;
	private boolean passToGame;

	/**
	 * Creates an IRCChannel to join. .
	 * 
	 * @param password
	 *            The password to the channel, if any;
	 * @param showJoinLeave
	 *            The option to show leave/join messages.
	 * @param autoJoin
	 *            The option to automatically connect to the server.
	 * @param defaultChannel
	 *            Weither the channel is defaulted with the irc say command.
	 * @param channel
	 *            The channel to join.
	 * @param type
	 *            The channel type.
	 * @param opCommands
	 *            The list of commands IRC ops can use.
	 * @param voiceCommands
	 *            The list of commands IRC voices can use.
	 * @param userCommands
	 *            The list of commands IRC users can use.
	 */
	public IRCChannel(final String password, final boolean showJoinLeave,
			final boolean autoJoin, final boolean defaultChannel,
			final String channel, final ChatType type,
			final List<String> opCommands, final List<String> hopCommands,
			final List<String> adminCommands, final List<String> voiceCommands,
			final List<String> userCommands, final boolean passToGame,
			final boolean passToIRC) {
		this.showJoinLeave = showJoinLeave;
		this.password = password;
		this.channel = channel;
		this.type = type;
		this.autoJoin = autoJoin;
		this.defaultChannel = defaultChannel;
		this.opCommands = opCommands;
		this.hopCommands = hopCommands;
		this.adminCommands = hopCommands;
		this.voiceCommands = voiceCommands;
		this.userCommands = userCommands;
		this.ops = new ArrayList<String>();
		this.hops = new ArrayList<String>();
		this.admins = new ArrayList<String>();
		this.voices = new ArrayList<String>();
		this.ChatChannel = null;
		this.passToGame = passToGame;
		this.passToIRC = passToIRC;
	}

	/**
	 * Creates an IRCChannel to join.
	 * 
	 * @param password
	 *            The password to the channel, if any;
	 * @param showJoinLeave
	 *            The option to show leave/join messages.
	 * @param autoJoin
	 *            The option to automatically connect to the server.
	 * @param defaultChannel
	 *            Weither the channel is defaulted with the irc say command.
	 * @param channel
	 *            The channel to join.
	 * @param heroChatChannel
	 *            The herochat channel to pass chat to.
	 * @param type
	 *            The channel type.
	 * @param opCommands
	 *            The list of commands IRC ops can use.
	 * @param voiceCommands
	 *            The list of commands IRC voices can use.
	 * @param userCommands
	 *            The list of commands IRC users can use.
	 */
	public IRCChannel(final String password, final boolean showJoinLeave,
			final boolean autoJoin, final boolean defaultChannel,
			final String channel, final String ChatChannel,
			final List<String> listenChatChannels, final ChatType type,
			final List<String> opCommands, final List<String> hopCommands,
			final List<String> adminCommands, final List<String> voiceCommands,
			final List<String> userCommands, final boolean passToGame,
			final boolean passToIRC) {
		this.showJoinLeave = showJoinLeave;
		this.password = password;
		this.channel = channel;
		this.ChatChannel = ChatChannel;
		this.listenChatChannels = listenChatChannels;
		this.type = type;
		this.autoJoin = autoJoin;
		this.defaultChannel = defaultChannel;
		this.opCommands = opCommands;
		this.hopCommands = hopCommands;
		this.adminCommands = hopCommands;
		this.voiceCommands = voiceCommands;
		this.userCommands = userCommands;
		this.ops = new ArrayList<String>();
		this.voices = new ArrayList<String>();
		this.hops = new ArrayList<String>();
		this.admins = new ArrayList<String>();
		this.passToGame = passToGame;
		this.passToIRC = passToIRC;
	}

	public IRCChannel(final String password, final boolean showJoinLeave,
			final boolean autoJoin, final boolean defaultChannel,
			final String channel, final String ChatChannel,
			final ChatType type, final List<String> opCommands,
			final List<String> hopCommands, final List<String> adminCommands,
			final List<String> voiceCommands, final List<String> userCommands,
			final boolean passToGame, final boolean passToIRC) {
		this.showJoinLeave = showJoinLeave;
		this.password = password;
		this.channel = channel;
		this.ChatChannel = ChatChannel;
		this.type = type;
		this.autoJoin = autoJoin;
		this.defaultChannel = defaultChannel;
		this.opCommands = opCommands;
		this.hopCommands = hopCommands;
		this.adminCommands = hopCommands;
		this.voiceCommands = voiceCommands;
		this.userCommands = userCommands;
		this.ops = new ArrayList<String>();
		this.voices = new ArrayList<String>();
		this.hops = new ArrayList<String>();
		this.admins = new ArrayList<String>();
		this.passToGame = passToGame;
		this.passToIRC = passToIRC;
	}

	/**
	 * Checks if the bot should aut-join the channel.
	 * 
	 * @return True if the bot should auto-join the channel; otherwise false.
	 */
	public boolean isAutoJoin() {
		return autoJoin;
	}

	/**
	 * Checks if the bot should aut-join the channel.
	 * 
	 * @return True if the bot should auto-join the channel; otherwise false.
	 */
	public boolean isDefaultChannel() {
		return defaultChannel;
	}

	/**
	 * Fetches the IRC channel name.
	 * 
	 * @return The IRC channel's name.
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * Fetches the channel's password.
	 * 
	 * @return the channel's password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Fetches the HeroChat channel to listen in.
	 * 
	 * @return The HeroChat channel to listen in.
	 */
	public Channel getHeroChatChannel() {
		return Herochat.getChannelManager().getChannel(ChatChannel);
	}

	/**
	 * Fetches the HeroChat channel to listen in.
	 * 
	 * @return The HeroChat channel to listen in.
	 */
	public com.herocraftonline.dthielke.herochat.channels.Channel getHeroChatFourChannel() {
		return MonsterIRC.getHookManager().getHeroChatHook()
				.getChannelManager().getChannel(ChatChannel);
	}

	/**
	 * Fetches the TownyChat channel to listen in.
	 * 
	 * @return The TownyChat channel to listen in.
	 */
	public com.palmergames.bukkit.TownyChat.channels.Channel getTownyChannel() {
		return MonsterIRC.getHookManager().getTownyChatHook()
				.getChannelsHandler().getChannel(ChatChannel);
	}

	/**
	 * Fetches the ChatType.
	 * 
	 * @return The Chat type.
	 */
	public ChatType getChatType() {
		return type;
	}

	/**
	 * Fetches the commands list for channel OPS.
	 * 
	 * @return The commands list for channel OPS.
	 */
	public List<String> getOpCommands() {
		return opCommands;
	}

	/**
	 * Fetches the commands list for channel HOPS.
	 * 
	 * @return The commands list for channel HOPS.
	 */
	public List<String> getHopCommands() {
		return hopCommands;
	}

	/**
	 * Fetches the commands list for channel Admins.
	 * 
	 * @return The commands list for channel Admins.
	 */
	public List<String> getAdminCommands() {
		return adminCommands;
	}

	/**
	 * Fetches the commands list for channel voices.
	 * 
	 * @return The commands list for channel voices.
	 */
	public List<String> getVoiceCommands() {
		return voiceCommands;
	}

	/**
	 * Fetches the commands list for normal users.
	 * 
	 * @return The commands list for normal users.
	 */
	public List<String> getUserCommands() {
		return userCommands;
	}

	/**
	 * Fetches the OPS in this channel.
	 * 
	 * @return The OPS in this channel.
	 */
	public List<String> getOpList() {
		return ops;
	}

	/**
	 * Fetches the OPS in this channel.
	 * 
	 * @return The OPS in this channel.
	 */
	public List<String> getHOpList() {
		return hops;
	}

	/**
	 * Fetches the OPS in this channel.
	 * 
	 * @return The OPS in this channel.
	 */
	public List<String> getAdminList() {
		return admins;
	}

	/**
	 * Fetches the voices in this channel.
	 * 
	 * @return The voices in this channel.
	 */
	public List<String> getVoiceList() {
		return voices;
	}

	/**
	 * The option to show join and leave messages.
	 * 
	 * @return True if the option to show join and leave messages is enabled;
	 *         otherwise false.
	 */
	public boolean showJoinLeave() {
		return showJoinLeave;
	}

	public boolean isHeroChatListenChannel(String activeChannelName) {
		if (listenChatChannels != null) {
			for (String chName : listenChatChannels) {
				if (chName.equalsIgnoreCase(activeChannelName)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean passToGame() {
		return passToGame;
	}

	public boolean passToIRC() {
		return passToIRC;
	}

}
