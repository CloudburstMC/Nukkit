package cn.nukkit;

import cn.nukkit.bootstrap.Bootstrap;
import cn.nukkit.bootstrap.Options;
import cn.nukkit.command.CommandReader;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.ServerKiller;

import java.util.Objects;

/**
 * Static values, information and version strings
 *
 * Do not change values in this class, only obtain values
 *
 * @since 1.0
 */
public class Nukkit {

    public final static String VERSION = "1.0dev";
    public final static String API_VERSION = "1.0.0";
    public final static String CODENAME = "蘋果(Apple)派(Pie)";
    public final static String MINECRAFT_VERSION = "v0.12.3 alpha";
    public final static String MINECRAFT_VERSION_NETWORK = "0.12.3";

    public final static Long START_TIME = System.currentTimeMillis();

    public static String PATH = System.getProperty("user.dir") + "/";
    public static String DATA_PATH = System.getProperty("user.dir") + "/";
    public static String PLUGIN_PATH = DATA_PATH + "plugins";
    public static boolean ANSI = true;
    public static boolean shortTitle = false;
    public static int DEBUG = 1;

    private static Options bootstrapOptions = null;
    private static Server serverInstance = null;
    private static MainLogger loggerInstance = null;

    /**
     * Obtain the instance of Server
     *
     * Old access from Server.getInstance has been moved to this place
     *
     * @see Server
     *
     * @return The instance of Server
     */
    public static Server getServerInstance(){
        return Nukkit.serverInstance;
    }

    /**
     * Obtain bootstrap options
     *
     * @return Bootstrap options
     */
    public static Options getOptions(){
        return Nukkit.bootstrapOptions;
    }

    /**
     * INTERNAL METHOD, DO NOT USE
     *
     * Add status flags
     *
     * @param flags The flags
     */
    public static void addFlags(int flags){
        Bootstrap.STATUS_CODE |= flags;
    }

    /**
     * Obtain status flags
     *
     * @return The flags
     */
    public static int getFlags(){
        return Bootstrap.STATUS_CODE;
    }

    /**
     * INTERNAL METHOD, DO NOT USE
     *
     * Initialize server instance, load levels, etc.
     *
     * @throws IllegalAccessException
     */
    public static void initializeNukkit() throws IllegalAccessException {
        if(Nukkit.serverInstance == null){
            Nukkit.loggerInstance = new MainLogger(DATA_PATH + "server.log");

            if (ANSI) {
                System.out.print((char) 0x1b + "]0;Starting Nukkit Server For Minecraft: PE" + (char) 0x07);
            }

            Nukkit.serverInstance = new Server();
            Nukkit.getServerInstance().initialize(
                    Nukkit.loggerInstance,
                    Nukkit.getOptions().ROOT_DIRECTORY,
                    Nukkit.getOptions().DATA_PATH,
                    Nukkit.getOptions().PLUGIN_PATH
            );
        }else {
            throw new IllegalAccessException("Nukkit already initialized");
        }
    }

    /**
     * INTERNAL METHOD, DO NOT USE
     *
     * Runs the server
     */
    public static void startupServer(){
        Objects.requireNonNull(Nukkit.getServerInstance()).start();
    }

    /**
     * INTERNAL METHOD, DO NOT USE
     *
     * Invoke while trying to stop the server
     */
    public static void interruptServer(){
        MainLogger logger = Nukkit.loggerInstance;

        if (ANSI) {
            System.out.print((char) 0x1b + "]0;Stopping Server..." + (char) 0x07);
        }
        logger.info("Stopping other threads");

        Thread.getAllStackTraces().keySet().stream().filter(thread -> thread instanceof InterruptibleThread).forEach(thread -> {
            logger.debug("Stopping " + thread.getClass().getSimpleName() + " thread");
            if (thread.isAlive()) {
                thread.interrupt();
            }
        });

        ServerKiller killer = new ServerKiller(8);
        killer.start();

        logger.shutdown();
        logger.interrupt();
        CommandReader.getInstance().removePromptLine();
    }

    /**
     * INTERNAL METHOD, DO NOT USE
     *
     * Set startup options
     *
     * @param options The startup options
     * @throws IllegalAccessException
     */
    public static void setOptions(Options options) throws IllegalAccessException {
        if(Nukkit.bootstrapOptions == null){
            Nukkit.bootstrapOptions = Objects.requireNonNull(options);

            Nukkit.PATH = options.ROOT_DIRECTORY;
            Nukkit.DATA_PATH = options.DATA_PATH;
            Nukkit.PLUGIN_PATH = options.PLUGIN_PATH;

            Nukkit.DEBUG = options.DEBUG_LEVEL;

            Nukkit.ANSI = options.ANSI;
            Nukkit.shortTitle = options.SHORT_TITLE;
        }else{
            throw new IllegalAccessException("Bootstrap options can only be set once");
        }
    }
}
