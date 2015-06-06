package cn.nukkit;

import cn.nukkit.utils.MainLogger;

/**
 * author: MagicDroidX
 * Nukkit
 */

public class Nukkit {

    public final static String VERSION = "1.0dev-1";
    public final static String API_VERSION = "1.0.0";
    public final static String CODENAME = "蘋果(Apple)派(Pie)";
    public final static String MINECRAFT_VERSION = "v0.11.0 alpha";
    public final static String MINECRAFT_VERSION_NETWORK = "0.11.0";

    private final static String nukkit_PATH = System.getProperty("user.dir") + "/";
    private final static String nukkit_DATA = System.getProperty("user.dir") + "/";
    private final static String nukkit_PLUGIN_PATH = nukkit_DATA + "plugins";
    private final static Long nukkit_START_TIME = System.currentTimeMillis();
    public static boolean nukkit_ANSI = true;


    public static void main(String[] args) {

        //启动参数
        for (String arg : args) {
            if (arg.equals("disable-ansi")) nukkit_ANSI = false;
        }

        MainLogger logger = new MainLogger(nukkit_DATA + "server.log", false, nukkit_ANSI); //todo: 检查是否启用了ansi，是否开启了调试模式


        Server server = new Server(logger, nukkit_PATH, nukkit_DATA, nukkit_PLUGIN_PATH);

    }

}
