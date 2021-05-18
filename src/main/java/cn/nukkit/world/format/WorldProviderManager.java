package cn.nukkit.world.format;

import cn.nukkit.Server;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class WorldProviderManager {
    protected static final Map<String, Class<? extends WorldProvider>> providers = new HashMap<>();

    public static void addProvider(Server server, Class<? extends WorldProvider> clazz) {
        try {
            providers.put((String) clazz.getMethod("getProviderName").invoke(null), clazz);
        } catch (Exception e) {
            Server.getInstance().getLogger().logException(e);
        }
    }

    public static Class<? extends WorldProvider> getProvider(String path) {
        for (Class<? extends WorldProvider> provider : providers.values()) {
            try {
                if ((boolean) provider.getMethod("isValid", String.class).invoke(null, path)) {
                    return provider;
                }
            } catch (Exception e) {
                Server.getInstance().getLogger().logException(e);
            }
        }
        return null;
    }

    public static Class<? extends WorldProvider> getProviderByName(String name) {
        name = name.trim().toLowerCase();
        return providers.getOrDefault(name, null);
    }

}
