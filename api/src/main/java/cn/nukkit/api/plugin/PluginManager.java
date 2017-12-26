package cn.nukkit.api.plugin;

import java.util.Collection;

public interface PluginManager {


    Collection<PluginContainer> getAllPlugins();

    /**
     * Checks if the given plugin is loaded and returns it when applicable
     * <p>
     * Please note that the name of the plugin is case-sensitive
     *
     * @param name Name of the plugin to check
     * @return Plugin if it exists, otherwise null
     */
    Plugin getPlugin(String name);

    /**
     * Checks if the given plugin is enabled or not
     *
     * @param plugin Plugin to check
     * @return true if the plugin is enabled, otherwise false
     */
    boolean isPluginEnabled(Plugin plugin);

    /**
     * Disables all the loaded plugins
     */
    void disablePlugins();

    /**
     * Disables and removes all plugins
     */
    void clearPlugins();

    /**
     * Enables the specified plugin
     * <p>
     * Attempting to enable a plugin that is already enabled will have no
     * effect
     *
     * @param plugin Plugin to enable
     */
    void enablePlugin(Plugin plugin);
}
