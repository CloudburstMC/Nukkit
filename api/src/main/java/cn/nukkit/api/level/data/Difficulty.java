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

    private final String i18n;

    Difficulty(String i18n, String... aliases) {
        this.i18n = i18n;
        add(aliases);
    }

    @Nonnull
    public static Difficulty parse(int difficulty) {
        return (difficulty > 3 || difficulty < 0 ? Difficulty.NORMAL : Difficulty.values()[difficulty]);
    }

    @Nonnull
    public static Difficulty parse(String difficulty) {
        return ALIASES.getOrDefault(difficulty, NORMAL);
    }

    private void add(String[] aliases) {
        for (String alias : aliases) {
            ALIASES.put(alias, this);
        }
    }

    public String getI18n() {
        return i18n;
    }
}
