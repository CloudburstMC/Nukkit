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
    public final static String MINECRAFT_VERSION = "v0.11.1 alpha";
    public final static String MINECRAFT_VERSION_NETWORK = "0.11.0";
    public final static byte MINECRAFT_PROTOCOL_VERISON = 120;

    public final static String PATH = System.getProperty("user.dir") + "/";
    public final static String DATA_PATH = System.getProperty("user.dir") + "/";
    public final static String PLUGIN_PATH = DATA_PATH + "plugins";
    public final static Long START_TIME = System.currentTimeMillis();
    public static boolean ANSI = true;


    public static void main(String[] args) {

        //启动参数
        for (String arg : args) {
            if (arg.equals("disable-ansi")) ANSI = false;
        }

        MainLogger logger = new MainLogger(DATA_PATH + "server.log", false, ANSI); //todo: 检查是否启用了ansi，是否开启了调试模式


        Server server = new Server(logger, PATH, DATA_PATH, PLUGIN_PATH);

    }

}
