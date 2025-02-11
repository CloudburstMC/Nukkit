package cn.nukkit.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * All supported formatting values for chat and console.
 */
public enum TextFormat {
    /**
     * Represents black.
     */
    BLACK('0'),
    /**
     * Represents dark blue.
     */
    DARK_BLUE('1'),
    /**
     * Represents dark green.
     */
    DARK_GREEN('2'),
    /**
     * Represents dark blue (aqua).
     */
    DARK_AQUA('3'),
    /**
     * Represents dark red.
     */
    DARK_RED('4'),
    /**
     * Represents dark purple.
     */
    DARK_PURPLE('5'),
    /**
     * Represents gold.
     */
    GOLD('6'),
    /**
     * Represents gray.
     */
    GRAY('7'),
    /**
     * Represents dark gray.
     */
    DARK_GRAY('8'),
    /**
     * Represents blue.
     */
    BLUE('9'),
    /**
     * Represents green.
     */
    GREEN('a'),
    /**
     * Represents aqua.
     */
    AQUA('b'),
    /**
     * Represents red.
     */
    RED('c'),
    /**
     * Represents light purple.
     */
    LIGHT_PURPLE('d'),
    /**
     * Represents yellow.
     */
    YELLOW('e'),
    /**
     * Represents white.
     */
    WHITE('f'),
    /**
     * Represents minecoins gold.
     */
    MINECOIN_GOLD('g'),
    /**
     * Makes the text obfuscated.
     */
    OBFUSCATED('k', true),
    /**
     * Makes the text bold.
     */
    BOLD('l', true),
    /**
     * Makes a line appear through the text.
     */
    STRIKETHROUGH('m', true),
    /**
     * Makes the text appear underlined.
     */
    UNDERLINE('n', true),
    /**
     * Makes the text italic.
     */
    ITALIC('o', true),
    /**
     * Resets all previous chat colors or formats.
     */
    RESET('r'),

    MATERIAL_QUARTZ('h'),
    MATERIAL_IRON('i'),
    MATERIAL_NETHERITE('j'),
    MATERIAL_REDSTONE('m'),
    MATERIAL_COPPER('n'),
    MATERIAL_GOLD('p'),
    MATERIAL_EMERALD('q'),
    MATERIAL_DIAMOND('s'),
    MATERIAL_LAPIS('t'),
    MATERIAL_AMETHYST('u'),
    MATERIAL_RESIN('v');

    /**
     * The special character which prefixes all format codes. Use this if
     * you need to dynamically convert format codes from your custom format.
     */
    public static final char ESCAPE = '\u00A7';

    private static final Pattern CLEAN_PATTERN = Pattern.compile("(?i)" + ESCAPE + "[0-9A-V]");
    private final static Map<Character, TextFormat> BY_CHAR = new HashMap<>();

    static {
        for (TextFormat color : values()) {
            BY_CHAR.put(color.code, color);
        }
    }

    private final char code;
    private final boolean isFormat;
    private final String toString;

    TextFormat(char code) {
        this(code, false);
    }

    TextFormat(char code, boolean isFormat) {
        this.code = code;
        this.isFormat = isFormat;
        this.toString = new String(new char[]{ESCAPE, code});
    }

    /**
     * Gets the TextFormat represented by the specified format code.
     *
     * @param code Code to check
     * @return Associative  with the given code,
     * or null if it doesn't exist
     */
    public static TextFormat getByChar(char code) {
        return BY_CHAR.get(code);
    }

    /**
     * Gets the TextFormat represented by the specified format code.
     *
     * @param code Code to check
     * @return Associative  with the given code,
     * or null if it doesn't exist
     */
    public static TextFormat getByChar(String code) {
        if (code == null || code.length() <= 1) {
            return null;
        }

        return BY_CHAR.get(code.charAt(0));
    }

    /**
     * Cleans the given message of all format codes.
     *
     * @param input String to clean.
     * @return A copy of the input string, without any formatting.
     */
    public static String clean(final String input) {
        return clean(input, false);
    }

    /**
     * Cleans the given message of all format codes.
     *
     * @param input String to clean.
     * @param recursive Do recursively.
     * @return A copy of the input string, without any formatting.
     */
    public static String clean(final String input, final boolean recursive) {
        if (input == null) {
            return null;
        }

        String result = CLEAN_PATTERN.matcher(input).replaceAll("");

        if (recursive && CLEAN_PATTERN.matcher(result).find()) {
            return clean(result, true);
        }
        return result;
    }

    /**
     * Translates a string using an alternate format code character into a
     * string that uses the internal TextFormat.ESCAPE format code
     * character. The alternate format code character will only be replaced if
     * it is immediately followed by 0-9, A-G, a-g, K-O, k-o, R or r.
     *
     * @param altFormatChar   The alternate format code character to replace. Ex: &amp;amp;
     * @param textToTranslate Text containing the alternate format code character.
     * @return Text containing the TextFormat.ESCAPE format code character.
     */
    public static String colorize(char altFormatChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            int x = i + 1;
            if (b[i] == altFormatChar && "0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVv".indexOf(b[x]) > -1) {
                b[i] = TextFormat.ESCAPE;
                b[x] = Character.toLowerCase(b[x]);
            }
        }
        return new String(b);
    }

    /**
     * Translates a string, using an ampersand (&amp;) as an alternate format code
     * character, into a string that uses the internal TextFormat.ESCAPE format
     * code character. The alternate format code character will only be replaced if
     * it is immediately followed by 0-9, A-G, a-g, K-O, k-o, R or r.
     *
     * @param textToTranslate Text containing the alternate format code character.
     * @return Text containing the TextFormat.ESCAPE format code character.
     */
    public static String colorize(String textToTranslate) {
        return colorize('&', textToTranslate);
    }

    /**
     * Gets the chat color used at the end of the given input string.
     *
     * @param input Input string to retrieve the colors from.
     * @return Any remaining chat color to pass onto the next line.
     */
    public static String getLastColors(String input) {
        StringBuilder result = new StringBuilder();
        int length = input.length();

        // Search backwards from the end as it is faster
        for (int index = length - 1; index > -1; index--) {
            if (input.charAt(index) == ESCAPE && index < length - 1) {
                TextFormat color = getByChar(input.charAt(index + 1));

                if (color != null) {
                    result.insert(0, color.toString());

                    // Once we find a color or reset we can stop searching
                    if (color.isColor() || color.equals(RESET)) {
                        break;
                    }
                }
            }
        }

        return result.toString();
    }

    /**
     * Gets the char value associated with this color
     *
     * @return A char value of this color code
     */
    public char getChar() {
        return code;
    }

    @Override
    public String toString() {
        return toString;
    }

    /**
     * Checks if this code is a format code as opposed to a color code.
     */
    public boolean isFormat() {
        return isFormat;
    }

    /**
     * Checks if this code is a color code as opposed to a format code.
     */
    public boolean isColor() {
        return !isFormat && this != RESET;
    }
}