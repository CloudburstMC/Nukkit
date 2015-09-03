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

}
