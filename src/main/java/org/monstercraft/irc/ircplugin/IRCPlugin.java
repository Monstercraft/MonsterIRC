package org.monstercraft.irc.ircplugin;

import java.io.File;
import java.io.IOException;
import java.util.EventListener;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.monstercraft.irc.MonsterIRC;
import org.monstercraft.irc.plugin.Configuration;

public abstract class IRCPlugin extends IRC implements EventListener, Runnable {

	private volatile boolean running = false;

	private int id = -1;

	/**
	 * Called before loop() is first called, after this plugin has been
	 * initialized with all method providers. Override to perform any
	 * initialization or prevent plugin start.
	 * 
	 * @return <tt>true</tt> if the plugin can start.
	 */
	public abstract boolean onStart();

	/**
	 * Perform actions upon stopping the plugin;
	 */
	public abstract void onFinish();

	/**
	 * For internal use only. Deactivates this plugin if the appropriate id is
	 * provided.
	 * 
	 * @param id
	 *            The id from pluginHandler.
	 */
	public final void deactivate(int id) {
		if (id != this.id) {
			throw new IllegalStateException("Invalid id!");
		}
		this.running = false;
	}

	/**
	 * For internal use only. Sets the pool id of this plugin.
	 * 
	 * @param id
	 *            The id from pluginHandler.
	 */
	public final void setID(int id) {
		if (this.id != -1) {
			throw new IllegalStateException("Already added to pool!");
		}
		this.id = id;
	}

	/**
	 * Returns whether or not the plugin is running and enabled.
	 * 
	 * @return <tt>true</tt> if active; otherwise <tt>false</tt>.
	 */
	public final boolean isActive() {
		return running;
	}

	/**
	 * Stops the current plugin;
	 */
	public void stop() {
		this.running = false;
	}

	public final void run() {
		boolean start = false;
		try {
			start = onStart();
		} catch (ThreadDeath ignored) {
		} catch (Throwable ex) {
		}
		if (start) {
			running = true;
			MonsterIRC.getEventManager().addListener(this);
			try {
				while (running) {
					int timeOut = -1;
					try {
						timeOut = 100;
					} catch (ThreadDeath td) {
						break;
					} catch (Exception ex) {
					}
					if (timeOut == -1) {
						break;
					}
					Thread.sleep(timeOut);
				}
				try {
					onFinish();
				} catch (ThreadDeath ignored) {
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} catch (Throwable t) {
				onFinish();
			}
			running = false;
		}
		MonsterIRC.getEventManager().removeListener(this);
		MonsterIRC.getHandleManager().getPluginHandler().stopPlugin(id);
		id = -1;
	}

	public void saveConfig(final FileConfiguration config, final File file) {
		try {
			config.save(file);
		} catch (IOException e) {
			debug(e);
		}
	}

	public FileConfiguration getConfig() {
		return new YamlConfiguration();
	}

	public File getCacheDirectory() {
		final File dir = new File(Configuration.Paths.PLUGINS, this.getClass()
				.getSimpleName());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public void registerBukkitListener(final Listener listener) {
		Bukkit.getServer()
				.getPluginManager()
				.registerEvents(
						listener,
						Bukkit.getServer().getPluginManager()
								.getPlugin("MonsterIRC"));
	}
}
