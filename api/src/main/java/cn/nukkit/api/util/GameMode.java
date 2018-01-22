package cn.nukkit.api.util;

import cn.nukkit.api.Player;

import javax.annotation.Nonnull;
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
    SURVIVAL("%gameMode.survival", "s", "0", "survival"),

    /**
     * Creative mode may fly, build instantly, become invulnerable and create
     * free items.
     */
    CREATIVE("%gameMode.creative", "c", "1", "creative"),

    /**
     * Adventure mode cannot break blocks without the correct tools.
     */
    ADVENTURE("%gameMode.adventure", "a", "2", "adventure"),

    /**
     * Spectator mode cannot interact with the world in anyway and is
     * invisible to normal players. This grants the player the
     * ability to no-clip through the world.
     */
    SPECTATOR("%gameMode.spectator", "sp", "3", "spectator");

    private static final Map<String, GameMode> modes = new HashMap<>();
    private final String translation;
    private final String[] aliases;

    GameMode(String translation, String... aliases) {
        this.translation = translation;
        this.aliases = aliases;
        add();
    }

    @Nonnull
    public static GameMode parse(String gamemode) {
        return modes.getOrDefault(gamemode.toLowerCase(), GameMode.SURVIVAL);
    }

    @Nonnull
    public static GameMode parse(int gamemode) {
        GameMode mode = GameMode.values()[gamemode];
        return (mode == null ? GameMode.SURVIVAL : mode);
    }

    private void add() {
        for (String s : aliases) {
            modes.put(s, this);
        }
    }

    @Nonnull
    public String getTranslationString() {
        return translation;
    }

    /**
     * Gets the mode value associated with this GameMode
     *
     * @return An integer value of this gamemode
     */
    public int getId() {
        return ordinal();
    }
}
