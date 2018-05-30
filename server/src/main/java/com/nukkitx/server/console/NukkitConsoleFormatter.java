package com.nukkitx.server.console;

import com.google.common.collect.ImmutableMap;
import com.nukkitx.api.message.TextFormat;
import org.fusesource.jansi.Ansi;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

public class NukkitConsoleFormatter implements Function<String, String> {
    private static final ImmutableMap<Pattern, String> REPLACEMENTS = ImmutableMap.<Pattern, String>builder().
            put(compile(TextFormat.BLACK), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString()).
            put(compile(TextFormat.DARK_BLUE), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString()).
            put(compile(TextFormat.DARK_GREEN), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString()).
            put(compile(TextFormat.DARK_AQUA), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString()).
            put(compile(TextFormat.DARK_RED), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString()).
            put(compile(TextFormat.DARK_PURPLE), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString()).
            put(compile(TextFormat.GOLD), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString()).
            put(compile(TextFormat.GRAY), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString()).
            put(compile(TextFormat.DARK_GRAY), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString()).
            put(compile(TextFormat.BLUE), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString()).
            put(compile(TextFormat.GREEN), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString()).
            put(compile(TextFormat.AQUA), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString()).
            put(compile(TextFormat.RED), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString()).
            put(compile(TextFormat.LIGHT_PURPLE), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString()).
            put(compile(TextFormat.YELLOW), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString()).
            put(compile(TextFormat.WHITE), Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString()).
            put(compile(TextFormat.OBFUSCATED), Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString()).
            put(compile(TextFormat.BOLD), Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString()).
            put(compile(TextFormat.STRIKETHROUGH), Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString()).
            put(compile(TextFormat.UNDERLINE), Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString()).
            put(compile(TextFormat.ITALIC), Ansi.ansi().a(Ansi.Attribute.ITALIC).toString()).
            put(compile(TextFormat.RESET), Ansi.ansi().a(Ansi.Attribute.RESET).toString())
            .build();
    private static final Set<Map.Entry<Pattern, String>> ENTRIES = REPLACEMENTS.entrySet();

    private static Pattern compile(TextFormat textFormat) {
        return Pattern.compile(textFormat.toString(), Pattern.LITERAL | Pattern.CASE_INSENSITIVE);
    }

    @Override
    public String apply(String text) {
        for (Map.Entry<Pattern, String> entry : ENTRIES) {
            text = entry.getKey().matcher(text).replaceAll(entry.getValue());
        }
        return Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + '\r' + text + Ansi.ansi().reset().toString();
    }
}
