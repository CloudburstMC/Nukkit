package com.nukkitx.server.console;

import com.nukkitx.api.message.TextFormat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class NukkitConsoleFormatter implements Function<String, String> {
    private static final String RESET = "\u001B[39;0m";

    private static final Map<Pattern, String> REPLACEMENTS;
    static {
        Map<Pattern, String> replacements = new HashMap<>();
        replacements.put(compile(TextFormat.BLACK), "\u001B[0;30;22m");
        replacements.put(compile(TextFormat.DARK_BLUE), "\u001B[0;34;22m");
        replacements.put(compile(TextFormat.DARK_GREEN), "\u001B[0;32;22m");
        replacements.put(compile(TextFormat.DARK_AQUA), "\u001B[0;36;22m");
        replacements.put(compile(TextFormat.DARK_RED), "\u001B[0;31;22m");
        replacements.put(compile(TextFormat.DARK_PURPLE), "\u001B[0;35;22m");
        replacements.put(compile(TextFormat.GOLD), "\u001B[0;33;22m");
        replacements.put(compile(TextFormat.GRAY), "\u001B[0;37;22m");
        replacements.put(compile(TextFormat.DARK_GRAY), "\u001B[0;30;1m");
        replacements.put(compile(TextFormat.BLUE), "\u001B[0;34;1m");
        replacements.put(compile(TextFormat.GREEN), "\u001B[0;32;1m");
        replacements.put(compile(TextFormat.AQUA), "\u001B[0;36;1m");
        replacements.put(compile(TextFormat.RED), "\u001B[0;31;1m");
        replacements.put(compile(TextFormat.LIGHT_PURPLE), "\u001B[0;35;1m");
        replacements.put(compile(TextFormat.YELLOW), "\u001B[0;33;1m");
        replacements.put(compile(TextFormat.WHITE), "\u001B[0;37;1m");
        replacements.put(compile(TextFormat.BOLD), "\u001B[21m");
        replacements.put(compile(TextFormat.STRIKETHROUGH), "\u001B[9m");
        replacements.put(compile(TextFormat.UNDERLINE), "\u001B[4m");
        replacements.put(compile(TextFormat.ITALIC), "\u001B[3m");
        replacements.put(compile(TextFormat.RESET), RESET);
        REPLACEMENTS = Collections.unmodifiableMap(replacements);
    }

    private static Pattern compile(TextFormat textFormat) {
        return Pattern.compile(textFormat.toString(), Pattern.LITERAL | Pattern.CASE_INSENSITIVE);
    }

    @Override
    public String apply(String text) {
        for (Map.Entry<Pattern, String> entry : REPLACEMENTS.entrySet()) {
            text = entry.getKey().matcher(text).replaceAll(entry.getValue());
        }
        return text + RESET;
    }
}
