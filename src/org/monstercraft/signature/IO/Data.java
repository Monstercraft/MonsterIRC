package org.monstercraft.signature.IO;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import org.monstercraft.signature.util.Variables;

public class Data {

	public static void updateSignature(final String name, final String clan,
			final int cash, final int power) throws UnknownHostException,
			IOException {
		try {
			final URL url = new URL(Variables.save);
			final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestProperty("User-Agent", "unhackable");
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			String content = "";
			String[] stats = { "name", "clan", "cash", "power" };
			Object[] data = { name, clan, cash, power };
			for (int i = 0; i < stats.length; i++) {
				content += stats[i] + "=" + data[i] + "&";
			}
			content = content.substring(0, content.length() - 1);
			DataOutputStream wr = new DataOutputStream(
					urlConn.getOutputStream());
			wr.writeBytes(content);
			wr.flush();
			wr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}