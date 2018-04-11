/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

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
