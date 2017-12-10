package cn.nukkit.api.plugin;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

import java.nio.file.Path;
import java.util.Optional;

public interface PluginDescription {
    /**
     * The ID for this plugin. This should be an alphanumeric name. Slashes are also allowed.
     * @return the ID for this plugin
     */
    String getName();

    ImmutableCollection<String> getApiVersions();

    /**
     * The path where the plugin is located on the file system.
     * @return the path of this plugin
     */
    Optional<Path> getPath();

    /**
     * The author of this plugin.
     * @return the plugin's author
     */
    ImmutableCollection<String> getAuthors();

    /**
     * The version of this plugin.
     * @return the version of this plugin
     */
    String getVersion();

    /**
     * The array of plugin IDs that this plugin requires in order to load.
     * @return the dependencies
     */
    ImmutableCollection<String> getDependencies();

    /**
     * The array of plugin IDs that this plugin optionally depends on.
     * @return the soft dependencies
     */
    ImmutableCollection<String> getSoftDependencies();

    /**
     * Plugin's website specified in the plugin.yml.
     * @return website url
     */
    Optional<String> getWebsite();

    /**
     * The plugin's logger prefix used in the console.
     * @return logger prefix
     */
    Optional<String> getLoggerPrefix();

    Optional<PluginLoadOrder> getLoadOrder();

    Optional<String> getDescription();

    ImmutableMap<String, PermissionDescription> getPermissionDescriptions();

    ImmutableMap<String, CommandDescription> getCommandDescriptions();

    ImmutableCollection<String> getPluginsToLoadBefore();
}
