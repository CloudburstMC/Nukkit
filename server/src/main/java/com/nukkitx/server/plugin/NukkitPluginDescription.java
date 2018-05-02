package com.nukkitx.server.plugin;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.nukkitx.api.plugin.CommandDescription;
import com.nukkitx.api.plugin.PermissionDescription;
import com.nukkitx.api.plugin.PluginDescription;
import com.nukkitx.api.plugin.PluginLoadOrder;
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
