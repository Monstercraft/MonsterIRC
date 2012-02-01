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
	 * @param port
	 * @param timeoutMs
	 * @return
	 */
	public static long ping(String host, int port, int timeoutMs) {
		long start = -1;
		long end = -1;
		long total = -1;
		Socket s = new Socket();
		try {
			InetAddress addr = InetAddress.getByName(host);
			SocketAddress sockaddr = new InetSocketAddress(addr, port);
			start = System.currentTimeMillis();
			s.connect(sockaddr, timeoutMs);
			end = System.currentTimeMillis();
		} catch (Exception e) {
			debug(e);
			start = -1;
			end = -1;
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