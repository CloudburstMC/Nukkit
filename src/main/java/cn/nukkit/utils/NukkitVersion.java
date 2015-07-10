package cn.nukkit.utils;

/**
 * Created by Nukkit Team.
 */
public class NukkitVersion {

    public final static String NUKKIT_VERSION = "1.0";
    public final static byte MINECRAFTPE_PROTOCOL_VERSION = 120;
    public static String SYSTEM = null;

    static {
        SYSTEM = System.getProperty("os.name");
    }

}
