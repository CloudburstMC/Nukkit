package cn.nukkit.server;

import cn.nukkit.api.util.SemVer;
import cn.nukkit.server.util.ServerKiller;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.Native;
import io.netty.util.ResourceLeakDetector;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Log4j2
public class Bootstrap {
    public static final long START_TIME = System.currentTimeMillis();
    private static final SemVer REUSEPORT_MINIMUM = new SemVer(3, 9, 0);

    public static void main(String... args) {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        System.setProperty("java.net.preferIPv4Stack", "true"); // IPv4 over v6

        boolean ansiEnabled = true;
        boolean shortTitle = false;
        boolean canReusePort = false;
        Path path = Paths.get(System.getProperty("user.dir"));

        // Check for SO_REUSEPORT compatible kernel for multithreaded netty binding.
        Optional<SemVer> optionalVer = kernelVersion();
        if (optionalVer.isPresent()) {
            canReusePort = REUSEPORT_MINIMUM.isCompatiblePatch(optionalVer.get());
        }

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
        // Get arguments
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
            ansiEnabled = false;
        }

        Path dataPath;
        if (options.has("data-path")) {
            dataPath = Paths.get((String) options.valueOf("data-path"));
        } else {
            dataPath = path;
        }

        Path pluginPath;
        if (options.has("plugin-path")) {
            pluginPath = Paths.get((String) options.valueOf("plugin-path"));
        } else {
            pluginPath = dataPath.resolve("plugins");
        }

        log.debug("Using log level {}", logLevel);
        log.info("Nukkit is loading...");
        NukkitServer server = new NukkitServer(path, dataPath, pluginPath, ansiEnabled, shortTitle, canReusePort);
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

    private static Optional<SemVer> kernelVersion(){
        if (Epoll.isAvailable()) {
            String kernelVersion = Native.KERNEL_VERSION;
            int index = kernelVersion.indexOf('-');
            if (index > -1) {
                kernelVersion = kernelVersion.substring(0, index);
            }
            return Optional.of(SemVer.fromString(kernelVersion));
        } else {
            return Optional.empty();
        }
    }
}
