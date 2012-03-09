package org.monstercraft.irc.ircplugin.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.tools.ToolProvider;

import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.Configuration;
import org.monstercraft.irc.plugin.Configuration.Variables;

public class JavaCompiler extends IRC {

	public static boolean run(final File source, final String classPath) {
		final javax.tools.JavaCompiler javac = ToolProvider
				.getSystemJavaCompiler();
		try {
			if (javac != null) {
				return compileNative(javac,
						new FileInputStream(source.getAbsoluteFile()),
						classPath) == 0;
			} else {
				return compileSystem(source, classPath);
			}
		} catch (final Exception e) {
			debug(e);
		}
		return false;
	}

	public static boolean isAvailable() {
		final javax.tools.JavaCompiler javac = ToolProvider
				.getSystemJavaCompiler();
		return !(javac == null && findJavac() == null);
	}

	private static int compileNative(final javax.tools.JavaCompiler javac,
			final InputStream source, final String classPath) {
		final FileOutputStream[] out = new FileOutputStream[2];
		for (int i = 0; i < 2; i++) {
			try {
				out[i] = new FileOutputStream(new File(
						Configuration.Paths.PLUGINS, "compile."
								+ Integer.toString(i) + ".txt"));
			} catch (FileNotFoundException e) {
				debug(e);
			}
		}
		return javac.run(source, out[0], out[1], "-cp", classPath);
	}

	private static boolean compileSystem(final File source,
			final String classPath) {
		String javac = findJavac();
		try {
			Process p = Runtime.getRuntime().exec(
					new String[] { javac, "", "-cp", classPath,
							source.getAbsolutePath() });
			String line;
			int errors = 0;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			while ((line = reader.readLine()) != null) {
				debug(line, Variables.debug);
				errors++;
			}
			return errors == 0;
		} catch (IOException e) {
			debug(e);
		}
		return false;
	}

	private static String findJavac() {
		try {
			boolean windows = false;
			final String os = System.getProperty("os.name");
			if (os.contains("Windows")) {
				windows = true;
			}
			if (windows) {
				String currentVersion = readProcess("REG QUERY \"HKLM\\SOFTWARE\\JavaSoft\\Java Development Kit\" /v CurrentVersion");
				currentVersion = currentVersion.substring(
						currentVersion.indexOf("REG_SZ") + 6).trim();
				String binPath = readProcess("REG QUERY \"HKLM\\SOFTWARE\\JavaSoft\\Java Development Kit\\"
						+ currentVersion + "\" /v JavaHome");
				binPath = binPath.substring(binPath.indexOf("REG_SZ") + 6)
						.trim() + "\\bin\\javac.exe";
				return new File(binPath).exists() ? binPath : null;
			} else {
				String whichQuery = readProcess("which javac");
				return whichQuery == null || whichQuery.length() == 0 ? null
						: whichQuery.trim();
			}
		} catch (Exception ignored) {
			return null;
		}
	}

	private static String readProcess(final String exec) throws IOException {
		final Process compiler = Runtime.getRuntime().exec(exec);
		final InputStream is = compiler.getInputStream();
		try {
			compiler.waitFor();
		} catch (final InterruptedException ignored) {
			return null;
		}
		final StringBuilder result = new StringBuilder(256);
		int r;
		while ((r = is.read()) != -1) {
			result.append((char) r);
		}
		return result.toString();
	}
}
