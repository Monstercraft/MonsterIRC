package org.monstercraft.irc.ircplugin.service;

import javax.tools.ToolProvider;

import org.monstercraft.irc.plugin.Configuration;

import java.io.*;

public class JavaCompiler {
	private final static String JAVACARGS = "-g:none";

	public static boolean run(final File source) {
		final javax.tools.JavaCompiler javac = ToolProvider
				.getSystemJavaCompiler();
		try {
			if (javac != null) {
				return compileNative(javac, new FileInputStream(source)) == 0;
			} else {
				compileSystem(source);
				return true;
			}
		} catch (final IOException ignored) {
		}
		return false;
	}

	public static boolean isAvailable() {
		return !(ToolProvider.getSystemJavaCompiler() == null && findJavac() == null);
	}

	private static int compileNative(final javax.tools.JavaCompiler javac,
			final InputStream source) throws FileNotFoundException {
		final FileOutputStream[] out = new FileOutputStream[2];
		for (int i = 0; i < 2; i++) {
			out[i] = new FileOutputStream(new File(Configuration.Paths.PLUGINS,
					"compile." + Integer.toString(i) + ".txt"));
		}
		return javac.run(source, out[0], out[1], JAVACARGS);
	}

	private static void compileSystem(final File source) throws IOException {
		String javac = findJavac();
		if (javac == null) {
			throw new IOException();
		}
		Runtime.getRuntime().exec(
				new String[] { javac, JAVACARGS, source.getAbsolutePath() });
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
