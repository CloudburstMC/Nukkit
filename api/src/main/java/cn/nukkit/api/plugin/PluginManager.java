package cn.nukkit.api.plugin;

import java.util.Collection;
import java.util.Optional;

public interface PluginManager {
    Collection<Plugin> getAllPlugins();

    Optional<Plugin> getPlugin(String id);
}
