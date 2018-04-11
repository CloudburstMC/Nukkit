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

package cn.nukkit.server.plugin.java;

import cn.nukkit.api.plugin.CommandDescription;
import cn.nukkit.api.plugin.PermissionDescription;
import cn.nukkit.api.plugin.PluginLoadOrder;
import cn.nukkit.server.plugin.NukkitPluginDescription;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

import java.nio.file.Path;

public class NukkitJavaPluginDescription extends NukkitPluginDescription {
    private final String mainClass;

    public NukkitJavaPluginDescription(Path path, String name, String mainClass, String version, ImmutableCollection<String> apiVersions,
                                       PluginLoadOrder loadOrder, ImmutableCollection<String> authors, String website, String description,
                                       String loggerPrefix, ImmutableCollection<String> dependencies, ImmutableCollection<String> softDependencies,
                                       ImmutableMap<String, PermissionDescription> permissionDescriptions,
                                       ImmutableMap<String, CommandDescription> commandDescriptions, ImmutableCollection<String> loadBeforePlugins) {
        super(path, name, version, apiVersions, loadOrder, authors, website, description, loggerPrefix, dependencies, softDependencies, permissionDescriptions, commandDescriptions, loadBeforePlugins);
        this.mainClass = mainClass;
    }

    public String getMainClass() {
        return mainClass;
    }
}
