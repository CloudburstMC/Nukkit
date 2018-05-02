package com.nukkitx.api.message;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents all text formatting colors and styles available.
 */
public enum TextFormat {
    /**
     * Colors the next section of text black.
     */
    BLACK('0', false),
    /**
     * Colors the next section of text dark blue.
     */
    DARK_BLUE('1', false),
    /**
     * Colors the next section of text dark green.
     */
    DARK_GREEN('2', false),
    /**
     * Colors the next section of text dark blue.
     */
    DARK_AQUA('3', false),
    /**
     * Colors the next section of text dark red.
     */
    DARK_RED('4', false),
    /**
     * Colors the next section of text dark purple.
     */
    DARK_PURPLE('5', false),
    /**
     * Colors the next section of text gold.
     */
    GOLD('6', false),
    /**
     * Colors the next section of text gray.
     */
    GRAY('7', false),
    /**
     * Colors the next section of text dark gray.
     */
    DARK_GRAY('8', false),
    /**
     * Colors the next section of text blue.
     */
    BLUE('9', false),
    /**
     * Colors the next section of text green.
     */
    GREEN('a', false),
    /**
     * Colors the next section of text aqua.
     */
    AQUA('b', false),
    /**
     * Colors the next section of text red.
     */
    RED('c', false),
    /**
     * Colors the next section of text light purple.
     */
    LIGHT_PURPLE('d', false),
    /**
     * Colors the next section of text yellow.
     */
    YELLOW('e', false),
    /**
     * Colors the next section of text white.
     */
    WHITE('f', false),
    /**
     * Makes the following section of text obfuscated.
     */
    OBFUSCATED('k', true),
    /**
     * Makes the following section of text bold.
     */
    BOLD('l', true),
    /**
     * Strikes a line through the following line of text.
     */
    STRIKETHROUGH('m', true),
    /**
     * Underlines the following section of text.
     */
    UNDERLINE('n', true),
    /**
     * Italicizes the following section of text.
     */
    ITALIC('o', true),
    /**
     * Resets all previous chat formatting, including formatting and styling.
     */
    RESET('r', true);

    public static final char FORMAT_CHAR = '\u00a7';
    public static final char AMPERSAND_CHAR = '\u0026';

    private static final Pattern CHAT_COLOR_MATCHER = Pattern.compile("(?i)" + Character.toString(FORMAT_CHAR) + "([0-9A-FK-OR])");
    private static final Pattern AMPERSAND_MATCHER = Pattern.compile("(?i)" + Character.toString(AMPERSAND_CHAR) + "([0-9A-FK-OR])");

    private final char id;
    private final String precompiledToString;
    private final boolean isStyling;

    TextFormat(char id, boolean isStyling) {
        this.id = id;
        this.precompiledToString = new String(new char[] { FORMAT_CHAR, id });
        this.isStyling = isStyling;
    }

    public static String removeFormatting(String string) {
        Objects.requireNonNull(string, "string");
        String removedColor =  CHAT_COLOR_MATCHER.matcher(string).replaceAll("");
        return AMPERSAND_MATCHER.matcher(removedColor).replaceAll("");
    }

    public static String colorize(String string) {
        Objects.requireNonNull(string, "string");
        return AMPERSAND_MATCHER.matcher(string).replaceAll( FORMAT_CHAR + "$1");
    }

    public static String decolorize(String string) {
        Objects.requireNonNull(string, "string");
        return CHAT_COLOR_MATCHER.matcher(string).replaceAll(AMPERSAND_CHAR + "$1");
    }

    public char getId() {
        return id;
    }

    public boolean isStyling() {
        return isStyling;
    }

    @Override
    public String toString() {
        return precompiledToString;
    }
}

