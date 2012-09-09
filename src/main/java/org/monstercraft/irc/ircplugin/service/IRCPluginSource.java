package org.monstercraft.irc.ircplugin.service;

import java.util.List;

import org.monstercraft.irc.ircplugin.IRCPlugin;

public interface IRCPluginSource {

    List<IRCPluginDefinition> list();

    IRCPlugin load(IRCPluginDefinition def) throws InstantiationException,
            IllegalAccessException;

}
