package org.monstercraft.irc.wrappers;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.util.ChatType;

import com.herocraftonline.dthielke.herochat.channels.Channel;

public class IRCChannel {

	private String channel;
	private String ingameChannel;
	private ChatType type;
	private boolean autoJoin;

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
	public IRCChannel(final boolean autoJoin, final String channel,
			final ChatType type) {
		this.channel = channel;
		this.type = type;
		this.autoJoin = autoJoin;
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
	public IRCChannel(final boolean autoJoin, final String channel,
			final String ingameChannel, final ChatType type) {
		this.channel = channel;
		this.ingameChannel = ingameChannel;
		this.type = type;
		this.autoJoin = autoJoin;
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
		return IRC.getHookManager().getHeroChatHook().getChannelManager()
				.getChannel(ingameChannel);
	}

	/**
	 * Fetches the ChatType.
	 * 
	 * @return The Chat type.
	 */
	public ChatType getChatType() {
		return type;
	}

}
