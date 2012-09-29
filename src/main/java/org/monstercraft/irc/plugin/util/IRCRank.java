package org.monstercraft.irc.plugin.util;

public enum IRCRank {
    USER("", 0),
    VOICE("+", 1),
    OWNER("~", 5),
    HALFOP("%", 1),
    ADMIN("&", 4),
    OP("@", 3);

    IRCRank(final String prefix, final int rank) {
        this.prefix = prefix;
        this.rank = rank;
    }

    public String getPrefix() {
        return prefix;
    }

    public int toInt() {
        return rank;
    }

    private final String prefix;

    private final int rank;
}
