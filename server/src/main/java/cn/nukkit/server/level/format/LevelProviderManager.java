package cn.nukkit.server.level.format;

import cn.nukkit.server.NukkitServer;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public abstract class LevelProviderManager {
    protected static final Map<String, Class<? extends LevelProvider>> providers = new HashMap<>();

    public static void addProvider(NukkitServer server, Class<? extends LevelProvider> clazz) {
        try {
            providers.put((String) clazz.getMethod("getProviderName").invoke(null), clazz);
        } catch (Exception e) {
            log.throwing(e);
        }
    }

    public static Class<? extends LevelProvider> getProvider(String path) {
        for (Class<? extends LevelProvider> provider : providers.values()) {
            try {
                if ((boolean) provider.getMethod("isValid", String.class).invoke(null, path)) {
                    return provider;
                }
            } catch (Exception e) {
                log.throwing(e);
            }
        }
        return null;
    }

    public static Class<? extends LevelProvider> getProviderByName(String name) {
        name = name.trim().toLowerCase();
        return providers.getOrDefault(name, null);
    }

}
