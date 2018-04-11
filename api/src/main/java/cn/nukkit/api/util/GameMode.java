/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

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
    SURVIVAL("gameMode.survival", "s", "0", "survival"),

    /**
     * Creative mode may fly, build instantly, become invulnerable and create
     * free items.
     */
    CREATIVE("gameMode.creative", "c", "1", "creative"),

    /**
     * Adventure mode cannot break blocks without the correct tools.
     */
    ADVENTURE("gameMode.adventure", "a", "2", "adventure"),

    /**
     * Spectator mode cannot interact with the world in anyway and is
     * invisible to normal players. This grants the player the
     * ability to no-clip through the world.
     */
    SPECTATOR("gameMode.spectator", "sp", "3", "spectator");

    private static final Map<String, GameMode> ALIASES = new HashMap<>();

    static {
        for (GameMode gameMode : values()) {
            for (String s : gameMode.aliases) {
                ALIASES.put(s, gameMode);
            }
        }
    }

    private final String i18n;
    private final String[] aliases;

    GameMode(String i18n, String... aliases) {
        this.i18n = i18n;
        this.aliases = aliases;
    }

    @Nonnull
    public static GameMode parse(String gamemode) {
        return ALIASES.getOrDefault(gamemode.toLowerCase(), GameMode.SURVIVAL);
    }

    @Nonnull
    public static GameMode parse(int gamemode) {
        return (gamemode > 3 || gamemode < 0 ? GameMode.SURVIVAL : GameMode.values()[gamemode]);
    }

    @Nonnull
    public String getI18n() {
        return i18n;
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
