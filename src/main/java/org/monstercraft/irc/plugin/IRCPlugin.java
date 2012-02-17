package org.monstercraft.irc.plugin;

import java.io.File;
import java.util.EventListener;

import org.bukkit.Bukkit;
import org.monstercraft.irc.plugin.event.EventManager;

public abstract class IRCPlugin implements EventListener, Runnable {

	private volatile boolean running = false;

	private int id = -1;
	private EventManager eventManager;

	/**
	 * Called before loop() is first called, after this script has been
	 * initialized with all method providers. Override to perform any
	 * initialization or prevent script start.
	 * 
	 * @return <tt>true</tt> if the script can start.
	 */
	public abstract boolean onEnable();

	/**
	 * The main loop. Called if you return true from onStart, then continuously
	 * until a negative integer is returned or the script stopped externally.
	 * When this script is paused this method will not be called until the
	 * script is resumed. Avoid causing execution to pause using sleep() within
	 * this method in favor of returning the number of milliseconds to sleep.
	 * This ensures that pausing and anti-randoms perform normally.
	 * 
	 * @return The number of milliseconds that the manager should sleep before
	 *         calling it again. Returning a negative number will deactivate the
	 *         script.
	 */
	public abstract int loop();

	/**
	 * Perform actions upon stopping the plugin;
	 */
	public abstract void onDisable();

	/**
	 * For internal use only. Deactivates this script if the appropriate id is
	 * provided.
	 * 
	 * @param id
	 *            The id from ScriptHandler.
	 */
	public final void deactivate(int id) {
		if (id != this.id) {
			throw new IllegalStateException("Invalid id!");
		}
		this.running = false;
	}

	/**
	 * For internal use only. Sets the pool id of this script.
	 * 
	 * @param id
	 *            The id from ScriptHandler.
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
		eventManager = new EventManager();
		boolean start = false;
		try {
			start = onEnable();
		} catch (ThreadDeath ignored) {
		} catch (Throwable ex) {
		}
		if (start) {
			running = true;
			getEventManager().addListener(this);
			try {
				while (running) {
					int timeOut = -1;
					try {
						timeOut = loop();
					} catch (ThreadDeath td) {
						break;
					} catch (Exception ex) {
					}
					if (timeOut == -1) {
						break;
					}
				}
				try {
					onDisable();
				} catch (ThreadDeath ignored) {
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} catch (Throwable t) {
				onDisable();
			}
			running = false;
		} else {
		}
		getEventManager().removeListener(this);
		id = -1;
	}

	public File getCacheDirectory() {
		final File dir = new File(getDataFile() + File.separator + "Plugins",
				getClass().getName());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	private static File getDataFile() {
		return Bukkit.getPluginManager().getPlugin("MonsterIRC")
				.getDataFolder();
	}

	protected EventManager getEventManager() {
		return eventManager;
	}
}
