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
        if (ranks.contains(IRCRank.OP)) {
            return IRCRank.OP;
        } else if (ranks.contains(IRCRank.OWNER)) {
            return IRCRank.OWNER;
        } else if (ranks.contains(IRCRank.ADMIN)) {
            return IRCRank.ADMIN;
        } else if (ranks.contains(IRCRank.HALFOP)) {
            return IRCRank.HALFOP;
        } else if (ranks.contains(IRCRank.VOICE)) {
            return IRCRank.VOICE;
        }
        return IRCRank.USER;
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