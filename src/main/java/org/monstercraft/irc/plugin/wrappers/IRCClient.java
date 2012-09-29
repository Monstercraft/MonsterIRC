package org.monstercraft.irc.plugin.wrappers;

import java.util.ArrayList;

import org.monstercraft.irc.plugin.util.IRCRank;

public class IRCClient {
    private final String prefix;
    private String nick;
    private final String hostMask;
    private final ArrayList<IRCRank> ranks = new ArrayList<IRCRank>();

    IRCClient(final IRCRank rank, final String nick, final String hostMask) {
        ranks.add(rank);
        prefix = rank.getPrefix();
        this.nick = nick;
        this.hostMask = hostMask;
    }

    public String getHostmask() {
        return hostMask;
    }

    public String getPrefix() {
        return prefix;
    }

    public IRCRank getHighestRank() {
        IRCRank rank = IRCRank.USER;
        for (final IRCRank r : ranks) {
            if (r.toInt() > rank.toInt()) {
                rank = r;
            }
        }
        return rank;
    }

    public ArrayList<IRCRank> getRanks() {
        return ranks;
    }

    public String getNick() {
        return nick;
    }

    public void updateNick(final String nick) {
        this.nick = nick;
    }

    public void addRank(final IRCRank rank) {
        if (!ranks.contains(rank)) {
            ranks.add(rank);
        }
    }

    public void removeRank(final IRCRank rank) {
        if (ranks.contains(rank)) {
            ranks.remove(rank);
        }
    }

    @Override
    public String toString() {
        return getPrefix() + getNick();
    }
}