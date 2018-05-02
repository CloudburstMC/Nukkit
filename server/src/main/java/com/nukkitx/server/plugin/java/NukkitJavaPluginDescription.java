package com.nukkitx.server.plugin.java;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.nukkitx.api.plugin.CommandDescription;
import com.nukkitx.api.plugin.PermissionDescription;
import com.nukkitx.api.plugin.PluginLoadOrder;
import com.nukkitx.server.plugin.NukkitPluginDescription;

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
