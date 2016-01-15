package cn.nukkit.utils;

/**
 * Created by Snake1999 on 2016/1/15.
 * Package cn.nukkit.utils in project nukkit.
 */
public final class Dyes {
    public static final int BLACK = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int BROWN = 3;
    public static final int BLUE = 4;
    public static final int PURPLE = 5;
    public static final int CYAN = 6;
    public static final int SILVER = 7;
    public static final int GRAY = 8;
    public static final int PINK = 9;
    public static final int LIME = 10;
    public static final int YELLOW = 11;
    public static final int LIGHT_BLUE = 12;
    public static final int MAGENTA = 13;
    public static final int ORANGE = 14;
    public static final int WHITE = 15;

    public static BlockColor getDyeColor(int dyeColorMeta) {
        switch (dyeColorMeta & 0x0f) {
            case Dyes.WHITE:
                return BlockColor.WHITE_BLOCK_COLOR;
            case Dyes.ORANGE:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case Dyes.MAGENTA:
                return BlockColor.MAGENTA_BLOCK_COLOR;
            case Dyes.LIGHT_BLUE:
                return BlockColor.LIGHT_BLUE_BLOCK_COLOR;
            case Dyes.YELLOW:
                return BlockColor.YELLOW_BLOCK_COLOR;
            case Dyes.LIME:
                return BlockColor.LIME_BLOCK_COLOR;
            case Dyes.PINK:
                return BlockColor.PINK_BLOCK_COLOR;
            case Dyes.GRAY:
                return BlockColor.GRAY_BLOCK_COLOR;
            case Dyes.SILVER:
                return BlockColor.SILVER_BLOCK_COLOR;
            case Dyes.CYAN:
                return BlockColor.CYAN_BLOCK_COLOR;
            case Dyes.PURPLE:
                return BlockColor.PURPLE_BLOCK_COLOR;
            case Dyes.BLUE:
                return BlockColor.BLUE_BLOCK_COLOR;
            case Dyes.BROWN:
                return BlockColor.BROWN_BLOCK_COLOR;
            case Dyes.GREEN:
                return BlockColor.GREEN_BLOCK_COLOR;
            case Dyes.RED:
                return BlockColor.RED_BLOCK_COLOR;
            case Dyes.BLACK:
                return BlockColor.BLACK_BLOCK_COLOR;

            default:
                return BlockColor.WHITE_BLOCK_COLOR;
        }
    }
    
    public static String getUnlocalizedColorName(int dyeColorMeta) {
        String[] names = new String[]{
                "Black",
                "Red",
                "Green",
                "Brown",
                "Blue",
                "Purple",
                "Cyan",
                "Silver",
                "Gray",
                "Pink",
                "Lime",
                "Yellow",
                "Light Blue",
                "Magenta",
                "Orange",
                "White"
        };
        return names[dyeColorMeta & 0x0f];
    }
}
