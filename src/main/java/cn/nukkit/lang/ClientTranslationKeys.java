package cn.nukkit.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * Nukkit message keys that the Bedrock client knows under another name.
 * <p>
 * A translation packet is resolved by the client, which does not know keys like
 * {@code commands.whitelist.enabled} and would show the key itself. The parameters keep
 * their order in every pair, so only the key changes.
 */
public final class ClientTranslationKeys {

    private static final Map<String, String> KEYS = new HashMap<>();

    static {
        KEYS.put("commands.whitelist.list", "commands.allowlist.list");
        KEYS.put("commands.whitelist.enabled", "commands.allowlist.enabled");
        KEYS.put("commands.whitelist.disabled", "commands.allowlist.disabled");
        KEYS.put("commands.whitelist.reloaded", "commands.allowlist.reloaded");
        KEYS.put("commands.whitelist.add.success", "commands.allowlist.add.success");
        KEYS.put("commands.whitelist.remove.success", "commands.allowlist.remove.success");
        KEYS.put("commands.xp.success.levels.minus", "commands.xp.success.negative.levels");
    }

    private ClientTranslationKeys() {
    }

    /**
     * @return the key the client knows for the given key, or the key itself when it has none
     */
    public static String get(String key) {
        return KEYS.getOrDefault(key, key);
    }
}
