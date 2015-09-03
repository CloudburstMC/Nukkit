package cn.nukkit;

import cn.nukkit.command.CommandReader;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.ServerKiller;

/**
 * author: MagicDroidX
 * Nukkit
 */

public class Nukkit {

    public final static String VERSION = "1.0dev";
    public final static String API_VERSION = "1.0.0";
    public final static String CODENAME = "蘋果(Apple)派(Pie)";
    public final static String MINECRAFT_VERSION = "v0.12.1 alpha";
    public final static String MINECRAFT_VERSION_NETWORK = "0.12.1";

    public final static String PATH = System.getProperty("user.dir") + "/";
    public final static String DATA_PATH = System.getProperty("user.dir") + "/";
    public final static String PLUGIN_PATH = DATA_PATH + "plugins";
    public final static Long START_TIME = System.currentTimeMillis();
    public static boolean ANSI = true;
    public static int DEBUG = 1;

    public static void main(String[] args) throws InterruptedException {

        //启动参数
        for (String arg : args) {
            if (arg.equals("disable-ansi")) ANSI = false;
        }

        MainLogger logger = new MainLogger(DATA_PATH + "server.log");

        ThreadManager.init();
        try {
            Server server = new Server(logger, PATH, DATA_PATH, PLUGIN_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("Stopping other threads");

        for (Thread thread : ThreadManager.getInstance().getAll()) {
            logger.debug("Stopping " + thread.getClass().getSimpleName() + " thread");
            thread.quit();
        }

        ServerKiller killer = new ServerKiller(8);
        killer.start();

        logger.shutdown();
        logger.interrupt();
        CommandReader.getInstance().removePromptLine();

        System.exit(0);
    }


}
