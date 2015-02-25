package org.monstercraft.irc.plugin;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.monstercraft.irc.ircplugin.IRC;
import org.monstercraft.irc.plugin.wrappers.IRCChannel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class holds all of the configuration data used within the plugin.
 *
 * @author fletch_to_99 <fletchto99@hotmail.com>
 *
 */
public class Configuration {

    public static class Paths {

        /**
         * The location in which to save the files.
         */
        public static final File ROOT = IRC.getIRCPlugin().getDataFolder();

        /**
         * The configuration file.
         */
        public static final File SETTINGS_FILE = new File(Paths.ROOT,
                "Settings.yml");

        /**
         * The locaton where the plugins for MonsterIRC are stored.
         */
        public static String PLUGINS = Paths.ROOT + File.separator + "Plugins";

        /**
         * The Channel Directory.
         */
        public static final String CHANNELS = Paths.ROOT + File.separator
                + "Channels" + File.separator;

    }

    public static class URLS {
        public static String UPDATE_URL = "http://dev.bukkit.org/server-mods/monsterirc/files.rss";
    }

    public static class Variables {

        public static int linesToIrc = 0;

        public static int linesToGame = 0;

        /**
         * The amount of times to attempt to connect to the server.
         */
        public static int tries = 5;

        /**
         * The option to enable colors.
         */
        public static boolean colors = true;

        /**
         * The option to part on disconnect.
         */
        public static boolean partOnDC = true;

        /**
         * Pass chat only when the bots name is said.
         */
        public static boolean passOnName = false;

        /**
         * The option to debug.
         */
        public static boolean debug = true;

        /**
         * The option to execute ingame commands as and IRC OP.
         */
        public static boolean ingamecommands = false;

        /**
         * The option to send the IDENTIFY command to nickserv.
         */
        public static boolean ident = false;

        /**
         * The port of the IRC server.
         */
        public static int port = 6667;

        /**
         * The IRC server to connect to.
         */
        public static String server = "irc.esper.net";

        /**
         * The users nickname.
         */
        public static String name = "default";

        /**
         * The password to identify with.
         */
        public static String password = "default";

        /**
         * The format for the game messages to be recieved in.
         */
        public static String mcformat = "<{groupPrefix}{prefix}{name}{suffix}{groupSuffix}>: {message}";

        /**
         * The format for the irc messages to be recieved in.
         */
        public static String ircformat = "<{groupPrefix}{prefix}{name}{suffix}{groupSuffix}>: {message}";

        /**
         * The prefix to be used when detecting an IRC command.
         */
        public static String commandPrefix = ".";

        /**
         * A list containing all of the muted users.
         */
        public static List<String> muted = new ArrayList<String>();

        /**
         * A Set of all the IRC channels.
         */
        public static Set<IRCChannel> channels = new HashSet<IRCChannel>();

        /**
         * The time to wait before disconnecting.
         */
        // public static int timeout = 2000;

        /**
         * The time to wait before disconnecting.
         */
        public static int limit = 2;

        /**
         * The commands to send on.
         */
        public static List<String> connectCommands = new ArrayList<String>();

        /**
         * The option to pass the console commands "say".
         */
        public static boolean passSay = false;

        /**
         * The password for the server.
         */
        public static String serverPass = "";

        /**
         * The last person to reply to.
         */
        public static Map<Player, String> reply = new HashMap<Player, String>();

        public static int commandsGame;

        public static int commandsIRC;
    }

    /**
     * Checks to see if the plugin is the latest version. Thanks to vault for letting me use their code.
     *
     * @param currentVersion
     *            The version that is currently running.
     * @return The latest version
     */
    public static String checkForUpdates(final Plugin plugin, final String site) {
        final String currentVersion = Configuration.getCurrentVerison(plugin);
        try {
            final URL url = new URL(site);
            final Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(url.openConnection().getInputStream());
            doc.getDocumentElement().normalize();
            final NodeList nodes = doc.getElementsByTagName("item");
            final Node firstNode = nodes.item(0);
            if (firstNode.getNodeType() == 1) {
                final Element firstElement = (Element) firstNode;
                final NodeList firstElementTagName = firstElement
                        .getElementsByTagName("title");
                final Element firstNameElement = (Element) firstElementTagName
                        .item(0);
                final NodeList firstNodes = firstNameElement.getChildNodes();
                return firstNodes.item(0).getNodeValue();
            }
        } catch (final Exception e) {
            IRC.debug(e);
        }
        return currentVersion;
    }

    public static void fixCase(final List<String> strings) {
        final ListIterator<String> iterator = strings.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().toLowerCase());
        }
    }

    public static String getClassPath() {
        String path = new File(Configuration.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath()).getAbsolutePath();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (final UnsupportedEncodingException ignored) {
        }
        return path;
    }

    public static String getCurrentVerison(final Plugin plugin) {
        return plugin.getDescription().getVersion();
    }

    /**
     * Pings the host.
     *
     * @param host
     *            The host to ping.
     * @param port
     *            The port the host is on.
     * @param timeoutMs
     *            The time in ms for the maximum ping response.
     * @return The time in ms the ping took.
     */
    public static long ping(final String host, final int port,
            final int timeoutMs) {
        long start = -1;
        long end = -1;
        long total = -1;
        final Socket s = new Socket();
        try {
            final InetAddress addr = InetAddress.getByName(host.trim());
            final InetSocketAddress sockaddr = new InetSocketAddress(addr, port);
            start = System.currentTimeMillis();
            s.connect(sockaddr, timeoutMs);
            end = System.currentTimeMillis();
        } catch (final SocketTimeoutException e) {
            IRC.log("The socket has timed out when attempting to connect!");
            IRC.log("Try running /irc reload in a few mins!");
            start = -1;
            end = -1;
            total = -1;
        } catch (final ConnectException e) {
            IRC.log("Your connection was refused by the IRC server!");
            IRC.log("Try running /irc reload in a few mins!");
            start = -1;
            end = -1;
            total = -1;
        } catch (final IOException e) {
            IRC.debug(e);
            start = -1;
            end = -1;
            total = -1;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (final Exception e) {
                    IRC.debug(e);
                }
            }
            if (start != -1 && end != -1) {
                total = end - start;
            }
        }
        return total;
    }

    public static boolean usingMultiverse() {
        final Plugin p = Bukkit.getServer().getPluginManager()
                .getPlugin("Multiverse-Core");
        if (p != null) {
            if (Bukkit.getServer().getPluginManager().isPluginEnabled(p)) {
                return true;
            }
        }
        return false;
    }

    static {
        IRC.getLogger().setLevel(Level.ALL);
        final ArrayList<String> dirs = new ArrayList<String>();
        dirs.add(Paths.PLUGINS);
        dirs.add(Paths.CHANNELS);
        for (final String name : dirs) {
            final File dir = new File(name);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }
}
