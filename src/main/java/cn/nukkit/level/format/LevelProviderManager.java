package cn.nukkit.level.format;

import cn.nukkit.Server;
import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class LevelProviderManager {

    protected static final Map<String, Class<? extends LevelProvider>> providers = new HashMap<>();

    public static void addProvider(final Server server, final Class<? extends LevelProvider> clazz) {
        try {
            LevelProviderManager.providers.put((String) clazz.getMethod("getProviderName").invoke(null), clazz);
        } catch (final Exception e) {
            Server.getInstance().getLogger().logException(e);
        }
    }

    public static Class<? extends LevelProvider> getProvider(final String path) {
        for (final Class<? extends LevelProvider> provider : LevelProviderManager.providers.values()) {
            try {
                if ((boolean) provider.getMethod("isValid", String.class).invoke(null, path)) {
                    return provider;
                }
            } catch (final Exception e) {
                Server.getInstance().getLogger().logException(e);
            }
        }
        return null;
    }

    public static Class<? extends LevelProvider> getProviderByName(String name) {
        name = name.trim().toLowerCase();
        return LevelProviderManager.providers.getOrDefault(name, null);
    }

}
