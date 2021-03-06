package org.monstercraft.irc.plugin.wrappers;

import java.util.ArrayList;
import java.util.List;

import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.util.ChatType;
import org.monstercraft.irc.plugin.util.IRCRank;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Herochat;

/**
 * This class creates an IRC channel to join.
 *
 * @author fletch_to_99 <fletchto99@hotmail.com>
 *
 */
public class IRCChannel {

    private final String channel;
    private final String ChatChannel;
    private final ChatType type;
    private final boolean autoJoin;
    private final boolean defaultChannel;
    private final List<String> opCommands;
    private final List<String> voiceCommands;
    private final List<String> hopCommands;
    private final List<String> adminCommands;
    private final List<String> userCommands;
    private List<String> listenChatChannels;
    private final List<String> blockedEvents;
    private final String password;
    private final ArrayList<IRCClient> users = new ArrayList<IRCClient>();

    /**
     * Creates an IRCChannel to join. .
     *
     * @param password
     *            The password to the channel, if any;
     * @param showingameEvents
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
    public IRCChannel(final String password, final List<String> blockedEvents,
            final boolean autoJoin, final boolean defaultChannel,
            final String channel, final ChatType type,
            final List<String> opCommands, final List<String> hopCommands,
            final List<String> adminCommands, final List<String> voiceCommands,
            final List<String> userCommands) {
        this.blockedEvents = blockedEvents;
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
        ChatChannel = null;
    }

    public IRCChannel(final String password, final List<String> blockedEvents,
            final boolean autoJoin, final boolean defaultChannel,
            final String channel, final String ChatChannel,
            final ChatType type, final List<String> opCommands,
            final List<String> hopCommands, final List<String> adminCommands,
            final List<String> voiceCommands, final List<String> userCommands) {
        this.blockedEvents = blockedEvents;
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
    }

    /**
     * Creates an IRCChannel to join.
     *
     * @param password
     *            The password to the channel, if any;
     * @param showingameEvents
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
    public IRCChannel(final String password, final List<String> blockedEvents,
            final boolean autoJoin, final boolean defaultChannel,
            final String channel, final String ChatChannel,
            final List<String> listenChatChannels, final ChatType type,
            final List<String> opCommands, final List<String> hopCommands,
            final List<String> adminCommands, final List<String> voiceCommands,
            final List<String> userCommands) {
        this.blockedEvents = blockedEvents;
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
    }

    public void addUser(final String nick, IRCRank rank, String hostMask) {
        if (rank == null) {
            rank = IRCRank.USER;
        }
        if (hostMask == null) {
            hostMask = "";
        }
        if (this.getUser(nick) != null) {
            return;
        }
        final IRCClient client = new IRCClient(rank, nick, hostMask);
        users.add(client);
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
     * The option to show join and leave messages.
     *
     * @return True if the option to show ingame events is enabled; otherwise false.
     */
    public List<String> getBlockedEvents() {
        return blockedEvents;
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
     * Fetches the ChatType.
     *
     * @return The Chat type.
     */
    public ChatType getChatType() {
        return type;
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
     * Fetches the commands list for channel HOPS.
     *
     * @return The commands list for channel HOPS.
     */
    public List<String> getHopCommands() {
        return hopCommands;
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
     * Fetches the channel's password.
     *
     * @return the channel's password.
     */
    public String getPassword() {
        return password;
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

    public IRCClient getUser(final String nick) {
        for (final IRCClient user : users) {
            if (user.getNick().toLowerCase().equals(nick.toLowerCase())) {
                return user;
            }
        }
        return null;
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
     * The option to show join and leave messages.
     *
     * @return True if the option to show ingame events is enabled; otherwise false.
     */
    public ArrayList<IRCClient> getUsers() {
        return users;
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

    public boolean isHeroChatListenChannel(final String activeChannelName) {
        if (listenChatChannels != null) {
            for (final String chName : listenChatChannels) {
                if (chName.equalsIgnoreCase(activeChannelName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeUser(final String nick) {
        users.remove(this.getUser(nick));
    }

}
