package cn.nukkit.api.plugin;

import java.util.Collection;
import java.util.Optional;

public interface PluginManager {
    Collection<PluginContainer> getAllPlugins();

    Optional<PluginContainer> getPlugin(String id);
}
