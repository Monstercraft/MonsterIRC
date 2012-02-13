package org.monstercraft.irc.wrappers;

import java.util.ArrayList;
import java.util.List;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.util.ChatType;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Herochat;

public class IRCChannel {

	private IRCServer server;
	private String channel;
	private String heroChatChannel;
	private ChatType type;
	private boolean autoJoin;
	private boolean defaultChannel;
	private List<String> opCommands;
	private List<String> voiceCommands;
	private List<String> userCommands;
	private List<String> ops;
	private List<String> voices;
	private String password;
	private boolean showJoinLeave;

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
	public IRCChannel(final IRCServer server, final String password,
			final boolean showJoinLeave, final boolean autoJoin,
			final boolean defaultChannel, final String channel,
			final ChatType type, final List<String> opCommands,
			final List<String> voiceCommands, final List<String> userCommands) {
		this.server = server;
		this.showJoinLeave = showJoinLeave;
		this.password = password;
		this.channel = channel;
		this.type = type;
		this.autoJoin = autoJoin;
		this.defaultChannel = defaultChannel;
		this.opCommands = opCommands;
		this.voiceCommands = voiceCommands;
		this.userCommands = userCommands;
		this.ops = new ArrayList<String>();
		this.voices = new ArrayList<String>();
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
	public IRCChannel(final IRCServer server, final String password,
			final boolean showJoinLeave, final boolean autoJoin,
			final boolean defaultChannel, final String channel,
			final String heroChatChannel, final ChatType type,
			final List<String> opCommands, final List<String> voiceCommands,
			final List<String> userCommands) {
		this.server = server;
		this.showJoinLeave = showJoinLeave;
		this.password = password;
		this.channel = channel;
		this.heroChatChannel = heroChatChannel;
		this.type = type;
		this.autoJoin = autoJoin;
		this.defaultChannel = defaultChannel;
		this.opCommands = opCommands;
		this.voiceCommands = voiceCommands;
		this.userCommands = userCommands;
		this.ops = new ArrayList<String>();
		this.voices = new ArrayList<String>();
	}

	public IRCServer getServer() {
		return server;
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

	public String getPassword() {
		return password;
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

	public List<String> getOpList() {
		return ops;
	}

	public List<String> getVoiceList() {
		return voices;
	}

	public boolean showJoinLeave() {
		return showJoinLeave;
	}

	public void join() {
		IRC.getHandleManager().getIRCHandler().join(this);
	}

	public void leave() {
		IRC.getHandleManager().getIRCHandler().leave(this);
	}

}