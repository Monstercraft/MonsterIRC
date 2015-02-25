package org.monstercraft.irc.plugin.wrappers;

import java.util.ArrayList;

import org.monstercraft.irc.plugin.util.IRCRank;

public class IRCClient {
    private String nick;
    private final String hostMask;
    private final ArrayList<IRCRank> ranks = new ArrayList<IRCRank>();

    IRCClient(final IRCRank rank, final String nick, final String hostMask) {
        ranks.add(rank);
        this.nick = nick;
        this.hostMask = hostMask;
    }

    public void addRank(final IRCRank rank) {
        if (!ranks.contains(rank)) {
            ranks.add(rank);
        }
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

    public String getHostmask() {
        return hostMask;
    }

    public String getNick() {
        return nick;
    }

    public String getPrefix() {
        return this.getHighestRank().getPrefix();
    }

    public ArrayList<IRCRank> getRanks() {
        return ranks;
    }

    public void removeRank(final IRCRank rank) {
        if (ranks.contains(rank)) {
            ranks.remove(rank);
        }
    }

    @Override
    public String toString() {
        return this.getPrefix() + this.getNick();
    }

    public void updateNick(final String nick) {
        this.nick = nick;
    }
}