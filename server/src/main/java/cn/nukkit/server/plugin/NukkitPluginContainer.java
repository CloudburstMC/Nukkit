package cn.nukkit.server.plugin;

import cn.nukkit.api.command.PluginCommand;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.api.plugin.PluginContainer;
import com.google.common.collect.ImmutableCollection;

public class NukkitPluginContainer extends NukkitPluginDescription implements PluginContainer {
    private final Plugin plugin;
    private final ImmutableCollection<PluginCommand> pluginCommands;

    public NukkitPluginContainer(NukkitPluginDescription description, ImmutableCollection<PluginCommand> pluginCommands, Plugin plugin) {
        super(description.path, description.name, description.version, description.apiVersions, description.loadOrder,
                description.authors,description.website, description.description, description.loggerPrefix,
                description.dependencies, description.softDependencies, description.permissionDescriptions,
                description.commandDescriptions, description.loadBeforePlugins);
        this.pluginCommands = pluginCommands;
        this.plugin = plugin;
    }


    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public ImmutableCollection<PluginCommand> getPluginCommands() {
        return pluginCommands;
    }
}
