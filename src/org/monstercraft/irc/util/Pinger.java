package org.monstercraft.irc.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.monstercraft.irc.IRC;

/**
 * This tests a connection to the host.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class Pinger extends IRC {

	/**
	 * Pings the host.
	 * 
	 * @param host
	 *            The host to ping
	 * @param port
	 *            The port the host is on.
	 * @param timeoutMs
	 *            The time in ms for the maximum ping response.
	 * @return The time in ms the ping took.
	 */
	public static int ping(String host, int port, int timeoutMs) {
		int start = -1;
		int end = -1;
		int total = -1;
		Socket s = new Socket();
		try {
			InetAddress addr = InetAddress.getByName(host);
			SocketAddress sockaddr = new InetSocketAddress(addr, port);
			start = (int) System.currentTimeMillis();
			s.connect(sockaddr, timeoutMs);
			end = (int) System.currentTimeMillis();
		} catch (Exception e) {
			debug(e);
			start = -1;
			end = -1;
			total = -1;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (Exception e) {
					debug(e);
				}
			}
			if ((start != -1) && (end != -1)) {
				total = end - start;
			}
		}
		return total;
	}
}