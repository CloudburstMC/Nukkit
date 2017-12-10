package cn.nukkit.server;

import cn.nukkit.server.utils.ServerKiller;
import io.netty.util.ResourceLeakDetector;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
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
public class Bootstrap {
    private final static String PATH = System.getProperty("user.dir") + File.separator;
    public static final long START_TIME = System.currentTimeMillis();
    private static boolean ANSI = true;
    private static boolean shortTitle = false;

    public static void main(String... args) {
        // prefer IPv4
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        System.setProperty("java.net.preferIPv4Stack", "true");

        //Shorter title for windows 8/2012
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            if (osName.contains("windows 8") || osName.contains("2012")) {
                shortTitle = true;
            }
        }
        OptionParser parser = new OptionParser(){{
            accepts("loglevel").withRequiredArg().ofType(String.class);
            accepts("disable-ansi");
            accepts("data-path").withRequiredArg().ofType(String.class);
            accepts("plugin-path").withRequiredArg().ofType(String.class);
        }};

        OptionSet options = parser.parse(args);

        Level logLevel = Level.INFO;
        if (options.has("loglevel")) {
            logLevel = Level.toLevel((String) options.valueOf("loglevel"), Level.INFO);
        }

        LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(logLevel);
        ctx.updateLoggers();

        if (options.has("disable-ansi")) {
            ANSI = false;
        }

        Path dataPath;
        if (options.has("data-path")) {
            dataPath = Paths.get((String) options.valueOf("data-path"));
        } else {
            dataPath = Paths.get(PATH);
        }

        Path pluginPath;
        if (options.has("plugin-path")) {
            pluginPath = Paths.get((String) options.valueOf("plugin-path"));
        } else {
            pluginPath = dataPath.resolve("plugins");
        }

        log.debug("Using log level {}", logLevel);
        log.info("Nukkit is loading...");
        NukkitServer server = new NukkitServer(Paths.get(PATH), dataPath, pluginPath, ANSI, shortTitle);
        try {
            server.boot();
        } catch (Exception e) {
            log.throwing(e);
        }

        log.info("Stopping server...");
        log.debug("Stopping other threads");

        for (Thread thread : java.lang.Thread.getAllStackTraces().keySet()) {
            if (!(thread instanceof InterruptibleThread)) {
                continue;
            }
            log.debug("Stopping " + thread.getClass().getSimpleName() + " thread");
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }

        ServerKiller killer = new ServerKiller(8);
        killer.start();

        log.info("Goodbye!");
    }
}
