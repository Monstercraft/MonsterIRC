package org.monstercraft.irc.plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

import org.monstercraft.irc.IRC;

public class Configuration {

	public Configuration() {
		try {
			String location = IRC.class.getProtectionDomain().getCodeSource()
					.getLocation().toURI().getPath();
			addClassPath(new File(location));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addClassPath(File file) throws Exception {
		Method method = URLClassLoader.class.getDeclaredMethod("addURL",
				new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke((URLClassLoader) ClassLoader.getSystemClassLoader(),
				new Object[] { file.toURI().toURL() });
	}

}
