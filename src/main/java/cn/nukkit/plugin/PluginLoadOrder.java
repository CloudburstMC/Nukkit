package cn.nukkit.plugin;

/**
 * Created by iNevet.
 * Nukkit Project
 */
public enum PluginLoadOrder {
    /**
     * Indicates that the plugin will be loaded at startup
     */
    STARTUP,
    /**
     * Indicates that the plugin will be loaded after the first/default world
     * was created
     */
    POSTWORLD
}
