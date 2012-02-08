package org.monstercraft.irc.wrappers;

import java.util.List;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.util.ChatType;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Herochat;

public class IRCChannel {

	private String channel;
	private String heroChatChannel;
	private ChatType type;
	private boolean autoJoin;
	private boolean defaultChannel;
	private List<String> opCommands;
	private List<String> voiceCommands;
	private List<String> userCommands;

	/**
	 * 
	 * @param autoJoin
	 *            True if the bot should automatically join that channel;
	 *            otherwise false.
	 * @param channel
	 *            The IRC channel to join.
	 * @param type
	 *            The type of chat this will pass @see ChatType
	 */
	public IRCChannel(final boolean autoJoin, final boolean defaultChannel,
			final String channel, final ChatType type,
			final List<String> opCommands, final List<String> voiceCommands,
			final List<String> userCommands) {
		this.channel = channel;
		this.type = type;
		this.autoJoin = autoJoin;
		this.defaultChannel = defaultChannel;
	}

	/**
	 * 
	 * @param autoJoin
	 *            True if the bot should automatically join that channel;
	 *            otherwise false.
	 * @param channel
	 *            The IRC channel to join.
	 * @param ingameChannel
	 *            The ingame channel name.
	 * @param type
	 *            The type of chat this will pass @see ChatType
	 */
	public IRCChannel(final boolean autoJoin, final boolean defaultChannel,
			final String channel, final String heroChatChannel,
			final ChatType type, final List<String> opCommands,
			final List<String> voiceCommands, final List<String> userCommands) {
		this.channel = channel;
		this.heroChatChannel = heroChatChannel;
		this.type = type;
		this.autoJoin = autoJoin;
		this.defaultChannel = defaultChannel;
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
	 * Fetches the HeroChat channel to listen in.
	 * 
	 * @return The HeroChat channel to listen in.
	 */
	public Channel getHeroChatChannel() {
		return Herochat.getChannelManager().getChannel(heroChatChannel);
	}

	/**
	 * Fetches the HeroChat channel to listen in.
	 * 
	 * @return The HeroChat channel to listen in.
	 */
	public com.herocraftonline.dthielke.herochat.channels.Channel getHeroChatFourChannel() {
		return IRC.getHookManager().getHeroChatHook().getChannelManager()
				.getChannel(heroChatChannel);
	}

	/**
	 * Fetches the ChatType.
	 * 
	 * @return The Chat type.
	 */
	public ChatType getChatType() {
		return type;
	}

	public List<String> getOpCommands() {
		return opCommands;
	}

	public List<String> getVoiceCommands() {
		return voiceCommands;
	}

	public List<String> getUserCommands() {
		return userCommands;
	}

}
