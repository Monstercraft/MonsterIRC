package org.monstercraft.irc.ircplugin;

import java.io.File;
import java.util.EventListener;

import org.monstercraft.irc.IRC;
import org.monstercraft.irc.plugin.Configuration;
import org.monstercraft.irc.plugin.util.Methods;

public abstract class IRCPlugin extends Methods implements EventListener,
		Runnable {

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
	 * The main loop. Called if you return true from onStart, then continuously
	 * until a negative integer is returned or the plugin stopped externally.
	 * When this plugin is paused this method will not be called until the
	 * plugin is resumed. Avoid causing execution to pause using sleep() within
	 * this method in favor of returning the number of milliseconds to sleep.
	 * This ensures that pausing and anti-randoms perform normally.
	 * 
	 * @return The number of milliseconds that the manager should sleep before
	 *         calling it again. Returning a negative number will deactivate the
	 *         plugin.
	 */
	public abstract int loop();

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

	@Override
	public final void run() {
		boolean start = false;
		try {
			start = onStart();
		} catch (ThreadDeath ignored) {
		} catch (Throwable ex) {
		}
		if (start) {
			running = true;
			IRC.getEventManager().addListener(this);
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
		IRC.getEventManager().removeListener(this);
		IRC.getHandleManager().getPluginHandler().stopPlugin(id);
		id = -1;
	}

	public File getCacheDirectory() {
		final File dir = new File(Configuration.Paths.PLUGINS, getClass()
				.getName());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
}
