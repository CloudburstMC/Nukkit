package cn.nukkit.server;

import io.netty.util.ResourceLeakDetector;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
public class Bootstrap {
    public static final long START_TIME = System.currentTimeMillis();

    public static void main(String... args) {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

        boolean ansiEnabled = true;
        Path path = Paths.get(System.getProperty("user.dir"));

        OptionParser parser = new OptionParser(){{
            accepts("disable-ansi");
            accepts("data-path").withRequiredArg().ofType(String.class);
            accepts("plugin-path").withRequiredArg().ofType(String.class);
        }};
        // Get arguments
        OptionSet options = parser.parse(args);

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

        NukkitServer server;
        try {
            server = new NukkitServer(path, dataPath, pluginPath, ansiEnabled);
        } catch (Exception e) {
            throw new AssertionError("Could not initialize Server class", e);
        }

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

        //ServerKiller killer = new ServerKiller(8);
        //killer.start();

        log.info("Goodbye!");
    }
}
