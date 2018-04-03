package cn.nukkit;

import cn.nukkit.command.CommandReader;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.LogLevel;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.ServerKiller;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
public class Nukkit {

    public final static String VERSION = "1.0dev";
    public final static String API_VERSION = "1.0.6";
    public final static String CODENAME = "蘋果(Apple)派(Pie)";
    @Deprecated
    public final static String MINECRAFT_VERSION = ProtocolInfo.MINECRAFT_VERSION;
    @Deprecated
    public final static String MINECRAFT_VERSION_NETWORK = ProtocolInfo.MINECRAFT_VERSION_NETWORK;

    public final static String PATH = System.getProperty("user.dir") + "/";
    public final static String DATA_PATH = System.getProperty("user.dir") + "/";
    public final static String PLUGIN_PATH = DATA_PATH + "plugins";
    public static final long START_TIME = System.currentTimeMillis();
    public static boolean ANSI = true;
    public static boolean shortTitle = false;
    public static int DEBUG = 1;

    public static void main(String[] args) {

        //Shorter title for windows 8/2012
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            if (osName.contains("windows 8") || osName.contains("2012")) {
                shortTitle = true;
            }
        }

        LogLevel logLevel = LogLevel.DEFAULT_LEVEL;
        int index = -1;
        boolean skip = false;
        //启动参数
        for (String arg : args) {
            index++;
            if (skip) {
                skip = false;
                continue;
            }

            switch (arg) {
                case "disable-ansi":
                    ANSI = false;
                    break;

                case "--verbosity":
                case "-v":
                    skip = true;
                    try {
                        String levelName = args[index + 1];
                        Set<String> levelNames = Arrays.stream(LogLevel.values()).map(level -> level.name().toLowerCase()).collect(Collectors.toSet());
                        if (!levelNames.contains(levelName.toLowerCase())) {
                            System.out.printf("'%s' is not a valid log level, using the default\n", levelName);
                            continue;
                        }
                        logLevel = Arrays.stream(LogLevel.values()).filter(level -> level.name().equalsIgnoreCase(levelName)).findAny().orElse(LogLevel.DEFAULT_LEVEL);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("You must enter the requested log level, using the default\n");
                    }

            }
        }

        MainLogger logger = new MainLogger(DATA_PATH + "server.log", logLevel);
        System.out.printf("Using log level '%s'\n", logLevel);

        try {
            if (ANSI) {
                System.out.print((char) 0x1b + "]0;Starting Nukkit Server For Minecraft: PE" + (char) 0x07);
            }
            new Server(logger, PATH, DATA_PATH, PLUGIN_PATH);
        } catch (Exception e) {
            logger.logException(e);
        }

        if (ANSI) {
            System.out.print((char) 0x1b + "]0;Stopping Server..." + (char) 0x07);
        }
        logger.info("Stopping other threads");

        for (Thread thread : java.lang.Thread.getAllStackTraces().keySet()) {
            if (!(thread instanceof InterruptibleThread)) {
                continue;
            }
            logger.debug("Stopping " + thread.getClass().getSimpleName() + " thread");
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }

        ServerKiller killer = new ServerKiller(8);
        killer.start();

        logger.shutdown();
        CommandReader.getInstance().removePromptLine();

        if (ANSI) {
            System.out.print((char) 0x1b + "]0;Server Stopped" + (char) 0x07);
        }
        System.exit(0);
    }


}
