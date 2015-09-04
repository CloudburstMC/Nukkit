package cn.nukkit.plugin;

/**
 * Created by iNevet.
 * Nukkit Project
 */
abstract public class PluginLoadOrder {
    /*
     * The plugin will be loaded at startup
     */
    public final static byte STARTUP = 0;

    /*
     * The plugin will be loaded after the first world has been loaded/created.
     */
    public final static byte POSTWORLD = 1;

    public static boolean isValid(String order) {
        order = order.toUpperCase();
        return order.equals("STARTUP") || order.equals("POSTWORLD");
    }

    public static byte getOrder(String order) {
        order = order.toUpperCase();
        switch (order) {
            case "STARTUP":
                return STARTUP;
            case "POSTWORLD":
                return POSTWORLD;
        }
        return POSTWORLD;
    }

}
