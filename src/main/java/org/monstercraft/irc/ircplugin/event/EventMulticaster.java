package org.monstercraft.irc.ircplugin.event;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import org.monstercraft.irc.ircplugin.event.events.IRCEvent;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;

public class EventMulticaster implements EventListener {

	public static final long IRC_EVENT = 0x100;

	public static final long IRC_MESSAGE_EVENT = 0x200;

	public static final long IRC_ACTION_EVENT = 0x300;

	public static final long IRC_PRIVATE_MESSAGE_EVENT = 0x400;

	public static final long IRC_KICK_EVENT = 0x500;

	public static final long IRC_CONNECT_EVENT = 0x600;

	public static final long IRC_DISCONNECT_EVENT = 0x700;

	public static final long IRC_PART_EVENT = 0x800;

	public static final long IRC_JOIN_EVENT = 0x900;

	public static final long IRC_MODE_EVENT = 0x1000;

	public static final long IRC_QUIT_EVENT = 0x1100;

	private static final Object treeLock = new Object();

	private final List<IRCListener> listeners = new ArrayList<IRCListener>(5);

	public static long getDefaultMask(EventListener el) {
		int mask = 0;
		if (el instanceof IRCListener) {
			mask |= EventMulticaster.IRC_EVENT;
		}

		return mask;
	}

	/**
	 * Gets the default mask for an event.
	 */
	public static long getDefaultMask(EventObject e) {
		long mask = 0;
		if (e instanceof IRCEvent) {
			final IRCEvent rse = (IRCEvent) e;
			mask |= rse.getMask();
		}
		return mask;
	}

	/**
	 * Adds the listener to the tree with a default mask.
	 */
	public void addListener(EventListener el) {
		long mask = 0;
		if (el instanceof IRCListener) {
			mask = EventMulticaster.getDefaultMask(el);
		}
		addListener(el, mask);
	}

	/**
	 * Adds the listener with the specified mask. If its an EventMulticaster the
	 * specified mask will be ignored.
	 */
	public void addListener(EventListener el, long mask) {
		synchronized (EventMulticaster.treeLock) {
			if (listeners.contains(el)) {
				return;
			}

			if (el instanceof IRCListener) {
				listeners.add((IRCListener) el);
			}
		}
	}

	/**
	 * Fires an event to all applicable listeners.
	 */
	public void fireEvent(EventObject e) {
		fireEvent(e, EventMulticaster.getDefaultMask(e));
	}

	/**
	 * Fires an event to all listeners, restricted by the mask.
	 */
	private void fireEvent(EventObject e, long mask) {
		synchronized (EventMulticaster.treeLock) {
			final int len = listeners.size();
			for (int i = 0; i < len; i++) {
				if (mask != 12288 && mask == 0) {
					continue;
				}
				IRCListener el = listeners.get(i);
				if (e instanceof IRCEvent) {
					IRCEvent rse = (IRCEvent) e;
					rse.dispatch(el);
				}
			}
		}
	}

	/**
	 * Removes a listener. Cleans up the masks.
	 */
	public void removeListener(EventListener el) {
		synchronized (EventMulticaster.treeLock) {
			final int idx = listeners.indexOf(el);
			if (idx == -1) {
				return;
			}
			el = listeners.remove(idx);
		}
	}
}
