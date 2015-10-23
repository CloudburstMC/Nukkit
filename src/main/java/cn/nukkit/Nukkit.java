package cn.nukkit;

import cn.nukkit.command.CommandReader;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.ServerKiller;

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

    public static void main(String[] args) {

        //启动参数
        for (String arg : args) {
            if (arg.equals("disable-ansi")) ANSI = false;
        }

        MainLogger logger = new MainLogger(DATA_PATH + "server.log");

        try {
            if (ANSI) {
                System.out.print((char) 0x1b + "]0;Starting Nukkit Server For Minecraft: PE" + (char) 0x07);
            }
            Server server = new Server(logger, PATH, DATA_PATH, PLUGIN_PATH);
        } catch (Exception e) {
            e.printStackTrace();
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
        logger.interrupt();
        CommandReader.getInstance().removePromptLine();

        if (ANSI) {
            System.out.print((char) 0x1b + "]0;Server Stopped" + (char) 0x07);
        }
        System.exit(0);
    }


}
