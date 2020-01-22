package cn.nukkit.registry;

import cn.nukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

class RegistryServiceProvider<T> {
    private final List<RegistryProvider<T>> providers = new ArrayList<>();
    private final Map<Plugin, RegistryProvider<T>> pluginProviders = new IdentityHashMap<>();

    RegistryServiceProvider(RegistryProvider<T> initalValue) {
        this.add(initalValue);
    }

    void add(RegistryProvider<T> provider) {
        checkArgument(!pluginProviders.containsKey(provider.getPlugin()), "plugin has already registered to this entity");
        this.providers.add(provider);
        this.pluginProviders.put(provider.getPlugin(), provider);
    }

    RegistryProvider<T> getProvider() {
        return providers.get(0);
    }

    RegistryProvider<T> getProvider(Plugin plugin) {
        return pluginProviders.get(plugin);
    }

    void bake() {
        providers.sort(null); // Sort using compareTo
    }
}
