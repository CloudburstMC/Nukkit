package cn.nukkit.level.format;

import cn.nukkit.Server;

import java.util.Map;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class LevelProviderManager {
    protected static TreeMap<String, LevelProvider> providers = new TreeMap<>();

    public static void addProvider(Server server, LevelProvider provider) {
        providers.put(provider.getProviderName(), provider);
    }

    public static LevelProvider getProvider(String path) {
        for (Map.Entry entry : providers.entrySet()) {
            LevelProvider provider = (LevelProvider) entry.getValue();
            if (provider.isValid(path)) {
                return provider;
            }
        }
        return null;
    }

    public static LevelProvider getProviderByName(String name) {
        name = name.trim().toLowerCase();
        return providers.containsKey(name) ? providers.get(name) : null;
    }

}
