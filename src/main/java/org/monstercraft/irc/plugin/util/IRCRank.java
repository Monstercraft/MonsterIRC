package org.monstercraft.irc.plugin.util;

public enum IRCRank {
    USER("", 0, "User"), VOICE("+", 1, "Voice"), OWNER("~", 5, "Owner"), HALFOP(
            "%", 1, "Half-OP"), ADMIN("&", 4, "Admin"), OP("@", 3, "OP");

    private final String prefix;

    private final int rank;

    private final String name;

    IRCRank(final String prefix, final int rank, final String name) {
        this.prefix = prefix;
        this.rank = rank;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public int toInt() {
        return rank;
    }
}
