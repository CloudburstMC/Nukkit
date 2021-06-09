package cn.nukkit;

import cn.nukkit.math.NukkitMath;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.ServerKiller;
import com.google.common.base.Preconditions;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.sentry.Sentry;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static cn.nukkit.utils.Utils.dynamic;

/*
 * `_   _       _    _    _ _
 * | \ | |     | |  | |  (_) |
 * |  \| |_   _| | _| | ___| |_
 * | . ` | | | | |/ / |/ / | __|
 * | |\  | |_| |   <|   <| | |_
 * |_| \_|\__,_|_|\_\_|\_\_|\__|
 */

/**
 * Nukkit启动类，包含{@code main}函数。<br>
 * The launcher class of Nukkit, including the {@code main} function.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@Log4j2
public class Nukkit {

    public final static Properties GIT_INFO = getGitInfo();
    public final static String VERSION = getVersion();
    public final static String GIT_COMMIT = getGitCommit();
    public final static String API_VERSION = dynamic("1.0.12");
    public final static String CODENAME = dynamic("PowerNukkit");
    @Deprecated
    public final static String MINECRAFT_VERSION = ProtocolInfo.MINECRAFT_VERSION;
    @Deprecated
    public final static String MINECRAFT_VERSION_NETWORK = ProtocolInfo.MINECRAFT_VERSION_NETWORK;

    public final static String PATH = System.getProperty("user.dir") + "/";
    public final static String DATA_PATH = System.getProperty("user.dir") + "/";
    public final static String PLUGIN_PATH = DATA_PATH + "plugins";
    public static final long START_TIME = System.currentTimeMillis();
    public static boolean ANSI = true;
    public static boolean TITLE = false;
    public static boolean shortTitle = requiresShortTitle();
    public static int DEBUG = 1;

    public static void main(String[] args) {
        AtomicBoolean disableSentry = new AtomicBoolean(false);
        Sentry.init(options -> {
            options.setDsn("https://a99f9e0c50424fff9f96feb2fd94c22f:6891b003c5874fa4bf407fe45035e3f1@o505263.ingest.sentry.io/5593371");
            options.setRelease(getVersion()+"-"+getGitCommit());
            options.setBeforeSend((event, hint)-> {
                if (disableSentry.get()) {
                    return null;
                }
                
                try {
                    Server sv = Server.getInstance();
                    event.setExtra("players", sv.getOnlinePlayers().size());
                    Map<Integer, cn.nukkit.level.Level> levels = sv.getLevels();
                    event.setExtra("levels", levels.size());
                    event.setExtra("chunks", levels.values().stream().mapToInt(l -> l.getChunks().size()).sum());
                    event.setExtra("tiles", levels.values().stream().mapToInt(l -> l.getBlockEntities().size()).sum());
                    event.setExtra("entities", levels.values().stream().mapToInt(l -> l.getEntities().length).sum());
                } catch (Exception e) {
                    log.debug("Failed to add player/level/chunk/tiles/entities information", e);
                }

                try {
                    Runtime runtime = Runtime.getRuntime();
                    double totalMB = NukkitMath.round(((double) runtime.totalMemory()) / 1024 / 1024, 2);
                    double usedMB = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
                    double maxMB = NukkitMath.round(((double) runtime.maxMemory()) / 1024 / 1024, 2);
                    double usage = usedMB / maxMB * 100;
                    
                    event.setExtra("memTotal", totalMB);
                    event.setExtra("memUsed", usedMB);
                    event.setExtra("memMax", maxMB);
                    event.setExtra("memUsage", usage);
                } catch (Exception e) {
                    log.debug("Failed to add memory information", e);
                }
                
                try {
                    event.setModules(
                            Server.getInstance().getPluginManager().getPlugins().entrySet().stream()
                                    .map(entry -> new SimpleEntry<>(
                                            entry.getKey(), 
                                            entry.getValue().getDescription().getVersion()
                                    )).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    );
                } catch (Exception e) {
                    log.debug("Failed to grab the list of enabled plugins", e);
                }
                return event;
            });
        });
        
        disableSentry.set(Boolean.parseBoolean(System.getProperty("disableSentry", "false")));
        Path propertiesPath = Paths.get(DATA_PATH, "server.properties");
        if (!disableSentry.get() && Files.isRegularFile(propertiesPath)) {
            Properties properties = new Properties();
            try (FileReader reader = new FileReader(propertiesPath.toFile())) {
                properties.load(reader);
                String value = properties.getProperty("disable-auto-bug-report", "false");
                if (value.equalsIgnoreCase("on") || value.equals("1")) {
                    value = "true";
                }
                disableSentry.set(Boolean.parseBoolean(value.toLowerCase(Locale.ENGLISH)));
            } catch (IOException e) {
                log.error("Failed to load server.properties to check disable-auto-bug-report.", e);
            }
        }
        
        // Force IPv4 since Nukkit is not compatible with IPv6
        System.setProperty("java.net.preferIPv4Stack" , "true");
        System.setProperty("log4j.skipJansi", "false");
        System.getProperties().putIfAbsent("io.netty.allocator.type", "unpooled"); // Disable memory pooling unless specified

        // Force Mapped ByteBuffers for LevelDB till fixed.
        System.setProperty("leveldb.mmap", "true");

        // Netty logger for debug info
        InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);

        // Define args
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        OptionSpec<Void> helpSpec = parser.accepts("help", "Shows this page").forHelp();
        OptionSpec<Void> ansiSpec = parser.accepts("disable-ansi", "Disables console coloring");
        OptionSpec<Void> titleSpec = parser.accepts("enable-title", "Enables title at the top of the window");
        OptionSpec<String> vSpec = parser.accepts("v", "Set verbosity of logging").withRequiredArg().ofType(String.class);
        OptionSpec<String> verbositySpec = parser.accepts("verbosity", "Set verbosity of logging").withRequiredArg().ofType(String.class);
        OptionSpec<String> languageSpec = parser.accepts("language", "Set a predefined language").withOptionalArg().ofType(String.class);

        // Parse arguments
        OptionSet options = parser.parse(args);

        if (options.has(helpSpec)) {
            try {
                // Display help page
                parser.printHelpOn(System.out);
            } catch (IOException e) {
                // ignore
            }
            return;
        }

        ANSI = !options.has(ansiSpec);
        TITLE = options.has(titleSpec);

        String verbosity = options.valueOf(vSpec);
        if (verbosity == null) {
            verbosity = options.valueOf(verbositySpec);
        }
        if (verbosity != null) {

            try {
                Level level = Level.valueOf(verbosity);
                setLogLevel(level);
            } catch (Exception e) {
                // ignore
            }
        }

        String language = options.valueOf(languageSpec);

        try {
            if (TITLE) {
                System.out.print((char) 0x1b + "]0;Nukkit is starting up..." + (char) 0x07);
            }
            new Server(PATH, DATA_PATH, PLUGIN_PATH, language);
        } catch (Throwable t) {
            log.catching(t);
        }

        if (TITLE) {
            System.out.print((char) 0x1b + "]0;Stopping Server..." + (char) 0x07);
        }
        log.info("Stopping other threads");

        for (Thread thread : java.lang.Thread.getAllStackTraces().keySet()) {
            if (!(thread instanceof InterruptibleThread)) {
                continue;
            }
            log.debug("Stopping {} thread", thread.getClass().getSimpleName());
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }

        ServerKiller killer = new ServerKiller(8);
        killer.start();

        if (TITLE) {
            System.out.print((char) 0x1b + "]0;Server Stopped" + (char) 0x07);
        }
        System.exit(0);
    }

    private static boolean requiresShortTitle() {
        //Shorter title for windows 8/2012
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("windows") &&(osName.contains("windows 8") || osName.contains("2012"));
    }

    private static Properties getGitInfo() {
        InputStream gitFileStream = Nukkit.class.getClassLoader().getResourceAsStream("git.properties");
        if (gitFileStream == null) {
            return null;
        }
        Properties properties = new Properties();
        try {
            properties.load(gitFileStream);
        } catch (IOException e) {
            return null;
        }
        return properties;
    }
    
    private static String getVersion() {
        InputStream resourceAsStream = Nukkit.class.getClassLoader().getResourceAsStream("VERSION.txt");
        if (resourceAsStream == null) {
            return "Unknown-PN-SNAPSHOT";
        }
        try (InputStream is = resourceAsStream;
             InputStreamReader reader = new InputStreamReader(is);
             BufferedReader buffered = new BufferedReader(reader)) {
            String line = buffered.readLine().trim();
            if ("${project.version}".equalsIgnoreCase(line)) {
                return "Unknown-PN-SNAPSHOT";
            } else {
                return line;
            }
        } catch (IOException e) {
            return "Unknown-PN-SNAPSHOT";
        }
    }

    private static String getGitCommit() {
        StringBuilder version = new StringBuilder();
        version.append("git-");
        String commitId;
        if (GIT_INFO == null || (commitId = GIT_INFO.getProperty("git.commit.id.abbrev")) == null) {
            return version.append("null").toString();
        }
        return version.append(commitId).toString();
    }

    public static void setLogLevel(Level level) {
        Preconditions.checkNotNull(level, "level");
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration log4jConfig = ctx.getConfiguration();
        LoggerConfig loggerConfig = log4jConfig.getLoggerConfig(org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

    public static Level getLogLevel() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration log4jConfig = ctx.getConfiguration();
        LoggerConfig loggerConfig = log4jConfig.getLoggerConfig(org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME);
        return loggerConfig.getLevel();
    }
}
