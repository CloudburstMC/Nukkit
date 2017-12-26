package cn.nukkit.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the various type of game modes that {@link Player}s may
 * have
 */
public enum GameMode {
    /**
     * Survival mode is the "normal" gameplay type, with no special features.
     */
    SURVIVAL("survival", "s"),

    /**
     * Creative mode may fly, build instantly, become invulnerable and create
     * free items.
     */
    CREATIVE("creative", "c"),

    /**
     * Adventure mode cannot break blocks without the correct tools.
     */
    ADVENTURE("adventure", "a"),

    /**
     * Spectator mode cannot interact with the world in anyway and is
     * invisible to normal players. This grants the player the
     * ability to no-clip through the world.
     */
    SPECTATOR("spectator", "s");

    private static final Map<String, GameMode> modes = new HashMap<>();

    static {
        for (GameMode mode : values()) {
            for (String s : mode.aliases) {
                modes.put(s, mode);
            }
        }
    }

    private final String[] aliases;

    GameMode(String... aliases) {
        this.aliases = aliases;
    }

    /**
     * Gets the mode value associated with this GameMode
     *
     * @return An integer value of this gamemode
     */
    public int getId() {
        return ordinal();
    }

    public static GameMode of(String mode) {
        return modes.get(mode.toLowerCase());
    }
}
