package cn.nukkit.item;

import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Dye extends Item {

    public Dye() {
        this(0, 1);
    }

    public Dye(Integer meta) {
        this(meta, 1);
    }

    public Dye(Integer meta, int count) {
        super(DYE, meta, count, "Dye");
    }

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

    public static BlockColor getColor(int meta) {
        switch (meta & 0x0f) {
            case WHITE:
                return BlockColor.WHITE_BLOCK_COLOR;
            case ORANGE:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case MAGENTA:
                return BlockColor.MAGENTA_BLOCK_COLOR;
            case LIGHT_BLUE:
                return BlockColor.LIGHT_BLUE_BLOCK_COLOR;
            case YELLOW:
                return BlockColor.YELLOW_BLOCK_COLOR;
            case LIME:
                return BlockColor.LIME_BLOCK_COLOR;
            case PINK:
                return BlockColor.PINK_BLOCK_COLOR;
            case GRAY:
                return BlockColor.GRAY_BLOCK_COLOR;
            case SILVER:
                return BlockColor.SILVER_BLOCK_COLOR;
            case CYAN:
                return BlockColor.CYAN_BLOCK_COLOR;
            case PURPLE:
                return BlockColor.PURPLE_BLOCK_COLOR;
            case BLUE:
                return BlockColor.BLUE_BLOCK_COLOR;
            case BROWN:
                return BlockColor.BROWN_BLOCK_COLOR;
            case GREEN:
                return BlockColor.GREEN_BLOCK_COLOR;
            case RED:
                return BlockColor.RED_BLOCK_COLOR;
            case BLACK:
                return BlockColor.BLACK_BLOCK_COLOR;

            default:
                return BlockColor.WHITE_BLOCK_COLOR;
        }
    }

    public static String getColorName(int meta) {
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
        return names[meta & 0x0f];
    }
}
