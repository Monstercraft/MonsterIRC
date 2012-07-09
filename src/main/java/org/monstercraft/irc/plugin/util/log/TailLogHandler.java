package org.monstercraft.irc.plugin.util.log;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TailLogHandler extends Handler {

	private ArrayList<String> lastRecords = new ArrayList<String>();

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(LogRecord r) {
		lastRecords.add(r.getMessage());
	}

	public ArrayList<String> getLastRecords(int size) {
		ArrayList<String> temp = new ArrayList<String>(size);
		int iterations = lastRecords.size() - size;
		if (iterations < 0) {
			iterations = 0;
		}
		for (int i = iterations; i < lastRecords.size(); i++) {
			temp.add(lastRecords.get(i));
		}
		return temp;
	}

}
