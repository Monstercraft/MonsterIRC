package org.monstercraft.irc.ircplugin.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import org.monstercraft.irc.ircplugin.event.events.IRCEvent;
import org.monstercraft.irc.ircplugin.event.listeners.IRCListener;

public class EventMulticaster implements EventListener {

	public static final long IRC_EVENT = 1;

	public static final long IRC_MESSAGE_EVENT = 2;

	private static final Object treeLock = new Object();

	/**
	 * Gets the default mask for an event listener.
	 */
	public static long getDefaultMask(EventListener el) {
		if (el instanceof EventMulticaster) {
			EventMulticaster em = (EventMulticaster) el;
			return em.enabledMask;
		}
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

	private long enabledMask;
	private final List<Long> listenerMasks = new ArrayList<Long>();

	private final List<EventListener> listeners = new ArrayList<EventListener>(
			5);

	private EventMulticaster parent;

	/**
	 * Adds the listener to the tree with a default mask.
	 */
	public void addListener(EventListener el) {
		long mask;
		if (el instanceof EventMulticaster) {
			final EventMulticaster em = (EventMulticaster) el;
			mask = em.enabledMask;
		} else {
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

			if (el instanceof EventMulticaster) {
				final EventMulticaster em = (EventMulticaster) el;
				addMulticaster(em);
				mask = em.enabledMask;
			} else {
				listeners.add(el);
			}
			listenerMasks.add(mask);
			cleanMasks();
		}
	}

	/**
	 * Ensures the multicaster tree is clean and adds it.
	 * <p/>
	 * Has to hold tree lock.
	 */
	private void addMulticaster(EventMulticaster em) {
		if (em.parent != null) {
			throw new IllegalArgumentException(
					"adding multicaster to multiple multicasters");
		}
		for (EventMulticaster cur = this; cur != null; cur = cur.parent) {
			if (cur == em) {
				throw new IllegalArgumentException(
						"adding multicaster's parent to itself");
			}
		}
		listeners.add(em);
		em.parent = this;
	}

	/**
	 * Walks up the tree as necessary reseting the masks to the minimum.
	 * <p/>
	 * Has to hold TreeLock.
	 */
	private void cleanMasks() {
		for (EventMulticaster cur = this; cur != null; cur = cur.parent) {
			int mask = 0;
			final int len = cur.listeners.size();
			for (int i = 0; i < len; i++) {
				final EventListener el = cur.listeners.get(i);
				long m = cur.listenerMasks.get(i);
				if (el instanceof EventMulticaster) {
					final EventMulticaster em = (EventMulticaster) el;
					if (em.enabledMask != m) {
						m = em.enabledMask;
						cur.listenerMasks.set(i, m);
					}
				}
				mask |= m;
			}
			if (mask == cur.enabledMask) {
				break;
			}
			cur.enabledMask = mask;
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
	public void fireEvent(EventObject e, long mask) {
		synchronized (EventMulticaster.treeLock) {
			final int len = listeners.size();
			for (int i = 0; i < len; i++) {
				long m = listenerMasks.get(i);
				if (m != 12288 && (m & mask) == 0) {
					continue;
				}
				EventListener el = listeners.get(i);
				if (e instanceof IRCEvent) {
					IRCEvent rse = (IRCEvent) e;
					rse.dispatch(el);
				}
			}
		}
	}

	/**
	 * Gets the masks enabled for this multicaster.
	 */
	public long getEnabledMask() {
		return enabledMask;
	}

	/**
	 * Returns an unmodifiable list of the backing list of listeners.
	 */
	public List<EventListener> getListeners() {
		return Collections.unmodifiableList(listeners);
	}

	/**
	 * Returns whether the mask is enabled on this multicaster.
	 */
	public final boolean isEnabled(long mask) {
		return (enabledMask & mask) != 0;
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
			if (el instanceof EventMulticaster) {
				final EventMulticaster em = (EventMulticaster) el;
				em.parent = null;
			}
			listenerMasks.remove(idx);
			cleanMasks();
		}
	}
}
