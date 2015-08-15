package cn.nukkit.level.format;

import cn.nukkit.Server;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class LevelProviderManager {
    protected static TreeMap<String, LevelProvider> providers = new TreeMap<>();

    public static void addProvider(Server server, LevelProvider provider) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        providers.put((String) provider.getClass().getMethod("getProviderName").invoke(provider.getClass()), provider);
    }

    public static LevelProvider getProvider(String path) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for (Map.Entry entry : providers.entrySet()) {
            LevelProvider provider = (LevelProvider) entry.getValue();
            if ((boolean) provider.getClass().getMethod("isValid", String.class).invoke(provider.getClass(), path)) {
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
