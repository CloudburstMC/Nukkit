package cn.nukkit.server.plugin.java;


import cn.nukkit.api.plugin.CommandDescription;
import cn.nukkit.api.plugin.InvalidPluginException;
import cn.nukkit.api.plugin.PermissionDescription;
import cn.nukkit.api.plugin.PluginLoadOrder;
import cn.nukkit.server.plugin.NukkitCommandDescription;
import cn.nukkit.server.plugin.NukkitPermissionDescription;
import cn.nukkit.server.plugin.NukkitPluginDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.*;

public class JavaPluginInformation {
    private String name;
    private String main;
    private String version;
    private Collection<String> api;
    private String load;
    private String author;
    private Collection<String> authors;
    private String website;
    private String description;
    private Collection<String> depend;
    private Collection<String> softdepend;
    private Map<String, JavaPermissionDescription> permissions;
    private Map<String, JavaCommandDescription> commands;
    private String prefix;
    private Collection<String> loadbefore;

    @Nonnull
    public NukkitPluginDescription generateDescription(Path path) throws Exception{
        if (name == null || main == null || api == null || version == null) {
            throw new InvalidPluginException("Plugin didn't have the one or more of required Plugin Descriptors: 'name', 'main', 'api', 'version'");
        }

        if (authors == null) {
            authors = new ArrayList<>();
        }

        if (author != null) {
            authors.add(author);
        }

        if (depend == null) {
            depend = new ArrayList<>();
        }

        if (softdepend == null) {
            softdepend = Collections.emptyList();
        }

        if (loadbefore == null) {
            loadbefore = Collections.emptyList();
        }

        ImmutableMap<String, PermissionDescription> permissions = ImmutableMap.of();
        if (this.permissions != null) {
            permissions = updatePermissionChildren(this.permissions);
        }

        PluginLoadOrder loadOrder = null;
        if (load != null) {
            loadOrder = PluginLoadOrder.parse(load);
        }

        Map<String, CommandDescription> commands = new HashMap<>();
        if (this.commands != null) {
            this.commands.forEach((command, description) -> {
                if (description.aliases == null) {
                    description.aliases = ImmutableList.of();
                }
                commands.put(command, new NukkitCommandDescription(description.description, ImmutableList.copyOf(description.aliases), description.usage, description.permission, description.permissionMessage));
            });
        }

        return new NukkitJavaPluginDescription(path, name, main, version, ImmutableList.copyOf(api), loadOrder, ImmutableList.copyOf(authors),
                website, description, prefix, ImmutableList.copyOf(depend), ImmutableList.copyOf(softdepend), permissions,
                ImmutableMap.copyOf(commands), ImmutableList.copyOf(loadbefore));
    }

    private ImmutableMap<String, PermissionDescription> updatePermissionChildren(Map<String, JavaPermissionDescription> permissionDescriptionMap) {
        Map<String, PermissionDescription> descriptionMap = new HashMap<>();
        permissionDescriptionMap.forEach((permission, description) -> descriptionMap.put(permission, new NukkitPermissionDescription(description.description, description.defaults,
                (description.children == null ? ImmutableMap.of() : updatePermissionChildren(description.children)))));
        return ImmutableMap.copyOf(descriptionMap);
    }

    private static class JavaCommandDescription {
        private String description;
        private Collection<String> aliases;
        private String usage;
        private String permission;
        @JsonProperty("permission-message")
        private String permissionMessage;
    }

    static class JavaPermissionDescription {
        private String description;
        @JsonProperty("default")
        private String defaults;
        private Map<String, JavaPermissionDescription> children;
    }
}
