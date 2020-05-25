package cn.nukkit.level;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum Difficulty {
    PEACEFUL("0", "peaceful", "p"),
    EASY("1", "easy", "e"),
    NORMAL("2", "normal", "n"),
    HARD("3", "hard", "h");

    private final Set<String> aliases;

    Difficulty(String... aliases) {
        this.aliases = new HashSet<>(Arrays.asList(aliases));
    }

    public static Difficulty fromString(String input) {
        input = input.toLowerCase();

        for (Difficulty d : values()) {
            if (d.aliases.contains(input)) {
                return d;
            }
        }

        return null;
    }
}
