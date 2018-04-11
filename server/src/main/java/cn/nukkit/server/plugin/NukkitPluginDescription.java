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

package cn.nukkit.server.plugin;

import cn.nukkit.api.plugin.CommandDescription;
import cn.nukkit.api.plugin.PermissionDescription;
import cn.nukkit.api.plugin.PluginDescription;
import cn.nukkit.api.plugin.PluginLoadOrder;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;

import java.nio.file.Path;
import java.util.Optional;

@AllArgsConstructor
public class NukkitPluginDescription implements PluginDescription {
    protected final Path path;
    protected final String name;
    protected final String version;
    protected final ImmutableCollection<String> apiVersions;
    protected final PluginLoadOrder loadOrder;
    protected final ImmutableCollection<String> authors;
    protected final String website;
    protected final String description;
    protected final String loggerPrefix;
    protected final ImmutableCollection<String> dependencies;
    protected final ImmutableCollection<String> softDependencies;
    protected final ImmutableMap<String, PermissionDescription> permissionDescriptions;
    protected final ImmutableMap<String, CommandDescription> commandDescriptions;
    protected final ImmutableCollection<String> loadBeforePlugins;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ImmutableCollection<String> getApiVersions() {
        return apiVersions;
    }

    @Override
    public Optional<Path> getPath() {
        return Optional.ofNullable(path);
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Optional<PluginLoadOrder> getLoadOrder() {
        return Optional.ofNullable(loadOrder);
    }

    @Override
    public ImmutableCollection<String> getAuthors() {
        return authors;
    }

    @Override
    public Optional<String> getWebsite() {
        return Optional.ofNullable(website);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @Override
    public Optional<String> getLoggerPrefix() {
        return Optional.ofNullable(loggerPrefix);
    }

    @Override
    public ImmutableCollection<String> getDependencies() {
        return dependencies;
    }

    @Override
    public ImmutableCollection<String> getSoftDependencies() {
        return softDependencies;
    }

    @Override
    public ImmutableMap<String, PermissionDescription> getPermissionDescriptions() {
        return permissionDescriptions;
    }

    @Override
    public ImmutableMap<String, CommandDescription> getCommandDescriptions() {
        return commandDescriptions;
    }

    @Override
    public ImmutableCollection<String> getPluginsToLoadBefore() {
        return loadBeforePlugins;
    }
}
