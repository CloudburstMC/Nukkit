package com.nukkitx.api.level.data;

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
