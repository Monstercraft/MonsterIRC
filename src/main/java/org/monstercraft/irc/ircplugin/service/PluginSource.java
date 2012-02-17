package org.monstercraft.irc.ircplugin.service;

import java.util.List;

import org.monstercraft.irc.ircplugin.IRCPlugin;

public interface PluginSource {

	List<PluginDefinition> list();

	IRCPlugin load(PluginDefinition def);

}
