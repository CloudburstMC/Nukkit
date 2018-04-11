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

package cn.nukkit.api.level.data;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public enum Difficulty {
    PEACEFUL("options.difficulty.peaceful", "0", "p", "peaceful"),
    EASY("options.difficulty.easy", "1", "e", "easy"),
    NORMAL("options.difficulty.normal", "2", "n", "normal"),
    HARD("options.difficulty.hard", "3", "h", "hard");

    private static final Map<String, Difficulty> ALIASES = new HashMap<>();

    static {
        for (Difficulty difficulty : values()) {
            for (String alias : difficulty.aliases) {
                ALIASES.put(alias, difficulty);
            }
        }
    }

    private final String i18n;
    private final String[] aliases;

    Difficulty(String i18n, String... aliases) {
        this.i18n = i18n;
        this.aliases = aliases;
    }

    @Nonnull
    public static Difficulty parse(int difficulty) {
        return (difficulty > 3 || difficulty < 0 ? Difficulty.NORMAL : Difficulty.values()[difficulty]);
    }

    @Nonnull
    public static Difficulty parse(String difficulty) {
        return ALIASES.getOrDefault(difficulty, NORMAL);
    }

    public String getI18n() {
        return i18n;
    }
}
