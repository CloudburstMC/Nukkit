package cn.nukkit.utils;

/**
 * author: MagicDroidX
 * Nukkit
 */
public class TextFormat {

    public static final char ESCAPE = '\u00a7';

    public static final String BLACK = TextFormat.ESCAPE + "0";
    public static final String DARK_BLUE = TextFormat.ESCAPE + "1";
    public static final String DARK_GREEN = TextFormat.ESCAPE + "2";
    public static final String DARK_AQUA = TextFormat.ESCAPE + "3";
    public static final String DARK_RED = TextFormat.ESCAPE + "4";
    public static final String DARK_PURPLE = TextFormat.ESCAPE + "5";
    public static final String GOLD = TextFormat.ESCAPE + "6";
    public static final String GRAY = TextFormat.ESCAPE + "7";
    public static final String DARK_GRAY = TextFormat.ESCAPE + "8";
    public static final String BLUE = TextFormat.ESCAPE + "9";
    public static final String GREEN = TextFormat.ESCAPE + "a";
    public static final String AQUA = TextFormat.ESCAPE + "b";
    public static final String RED = TextFormat.ESCAPE + "c";
    public static final String LIGHT_PURPLE = TextFormat.ESCAPE + "d";
    public static final String YELLOW = TextFormat.ESCAPE + "e";
    public static final String WHITE = TextFormat.ESCAPE + "f";

    public static final String OBFUSCATED = TextFormat.ESCAPE + "k";
    public static final String BOLD = TextFormat.ESCAPE + "l";
    public static final String STRIKETHROUGH = TextFormat.ESCAPE + "m";
    public static final String UNDERLINE = TextFormat.ESCAPE + "n";
    public static final String ITALIC = TextFormat.ESCAPE + "o";
    public static final String RESET = TextFormat.ESCAPE + "r";

    public static String clean(String message) {
        return message.replaceAll(ESCAPE + "+[0123456789abcdefklmnor]", "");
    }

    public static String toANSI(String string) {
        string = string.replace(TextFormat.BOLD, "");
        string = string.replace(TextFormat.OBFUSCATED, (char) 0x1b + "[8m");
        string = string.replace(TextFormat.ITALIC, (char) 0x1b + "[3m");
        string = string.replace(TextFormat.UNDERLINE, (char) 0x1b + "[4m");
        string = string.replace(TextFormat.STRIKETHROUGH, (char) 0x1b + "[9m");
        string = string.replace(TextFormat.RESET, (char) 0x1b + "[0m");
        string = string.replace(TextFormat.BLACK, (char) 0x1b + "[0;30m");
        string = string.replace(TextFormat.DARK_BLUE, (char) 0x1b + "[0;34m");
        string = string.replace(TextFormat.DARK_GREEN, (char) 0x1b + "[0;32m");
        string = string.replace(TextFormat.DARK_AQUA, (char) 0x1b + "[0;36m");
        string = string.replace(TextFormat.DARK_RED, (char) 0x1b + "[0;31m");
        string = string.replace(TextFormat.DARK_PURPLE, (char) 0x1b + "[0;35m");
        string = string.replace(TextFormat.GOLD, (char) 0x1b + "[0;33m");
        string = string.replace(TextFormat.GRAY, (char) 0x1b + "[0;37m");
        string = string.replace(TextFormat.DARK_GRAY, (char) 0x1b + "[30;1m");
        string = string.replace(TextFormat.BLUE, (char) 0x1b + "[34;1m");
        string = string.replace(TextFormat.GREEN, (char) 0x1b + "[32;1m");
        string = string.replace(TextFormat.AQUA, (char) 0x1b + "[36;1m");
        string = string.replace(TextFormat.RED, (char) 0x1b + "[31;1m");
        string = string.replace(TextFormat.LIGHT_PURPLE, (char) 0x1b + "[35;1m");
        string = string.replace(TextFormat.YELLOW, (char) 0x1b + "[33;1m");
        string = string.replace(TextFormat.WHITE, (char) 0x1b + "[37;1m");
        return string;
    }

    public static String colorize(String textToColorize) {
        char[] b = textToColorize.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if ((b[i] == '&') && ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[(i + 1)]) > -1)) {
                b[i] = ESCAPE;
                b[(i + 1)] = Character.toLowerCase(b[(i + 1)]);
            }
        }
        return new String(b);
    }

}
