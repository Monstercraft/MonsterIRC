package org.monstercraft.irc.plugin.service;

import java.util.List;

import org.monstercraft.irc.plugin.IRCPlugin;

public interface PluginSource {

	List<PluginDefinition> list();

	IRCPlugin load(PluginDefinition def);

}
