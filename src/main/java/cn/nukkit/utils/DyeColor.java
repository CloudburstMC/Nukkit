package cn.nukkit.utils;

public enum DyeColor {
    BLACK(0, 15, "Black", "Ink Sac", BlockColor.BLACK_BLOCK_COLOR, new BlockColor(0x00, 0x00, 0x00)),
    RED(1, 14, "Red", BlockColor.RED_BLOCK_COLOR, new BlockColor(0xb0, 0x2e, 0x26)),
    GREEN(2, 13, "Green", BlockColor.GREEN_BLOCK_COLOR, new BlockColor(0x5e, 0x7c, 0x16)),
    BROWN(3, 12, "Brown", "Cocoa Beans", BlockColor.BROWN_BLOCK_COLOR, new BlockColor(0x83, 0x54, 0x32)),
    BLUE(4, 11, "Blue", "Lapis Lazuli", BlockColor.BLUE_BLOCK_COLOR, new BlockColor(0x3c, 0x44, 0xaa)),
    PURPLE(5, 10, "Purple", BlockColor.PURPLE_BLOCK_COLOR, new BlockColor(0x89, 0x32, 0xb8)),
    CYAN(6, 9, "Cyan", BlockColor.CYAN_BLOCK_COLOR, new BlockColor(0x16, 0x9c, 0x9c)),
    LIGHT_GRAY(7, 8, "Light Gray", BlockColor.LIGHT_GRAY_BLOCK_COLOR, new BlockColor(0x9d, 0x9d, 0x97)),
    GRAY(8, 7, "Gray", BlockColor.GRAY_BLOCK_COLOR, new BlockColor(0x47, 0x4f, 0x52)),
    PINK(9, 6, "Pink", BlockColor.PINK_BLOCK_COLOR, new BlockColor(0xf3, 0x8b, 0xaa)),
    LIME(10, 5, "Lime", BlockColor.LIME_BLOCK_COLOR, new BlockColor(0x80, 0xc7, 0x1f)),
    YELLOW(11, 4, "Yellow", BlockColor.YELLOW_BLOCK_COLOR, new BlockColor(0xfe, 0xd8, 0x3d)),
    LIGHT_BLUE(12, 3, "Light Blue", BlockColor.LIGHT_BLUE_BLOCK_COLOR, new BlockColor(0x3a, 0xb3, 0xda)),
    MAGENTA(13, 2, "Magenta", BlockColor.MAGENTA_BLOCK_COLOR, new BlockColor(0xc7, 0x4e, 0xbd)),
    ORANGE(14, 1, "Orange", BlockColor.ORANGE_BLOCK_COLOR, new BlockColor(0xf9, 0x80, 0x1d)),
    WHITE(15, 0, "White", "Bone Meal", BlockColor.WHITE_BLOCK_COLOR, new BlockColor(0xf0, 0xf0, 0xf0));

    private final int dyeColorMeta;
    private final int woolColorMeta;
    private final String colorName;
    private final String dyeName;
    private final BlockColor blockColor;
    private final BlockColor signColor;

    private final static DyeColor[] BY_WOOL_DATA;
    private final static DyeColor[] BY_DYE_DATA;

    DyeColor(int dyeColorMeta, int woolColorMeta, String colorName, BlockColor blockColor, BlockColor signColor) {
        this(dyeColorMeta, woolColorMeta, colorName, colorName + " Dye", blockColor, signColor);
    }

    DyeColor(int dyeColorMeta, int woolColorMeta, String colorName, String dyeName, BlockColor blockColor, BlockColor signColor) {
        this.dyeColorMeta = dyeColorMeta;
        this.woolColorMeta = woolColorMeta;
        this.colorName = colorName;
        this.blockColor = blockColor;
        this.signColor = signColor;
        this.dyeName = dyeName;
    }

    public BlockColor getColor() {
        return this.blockColor;
    }

    public BlockColor getSignColor() {
        return this.signColor;
    }

    public int getDyeData() {
        return this.dyeColorMeta;
    }

    public int getWoolData() {
        return this.woolColorMeta;
    }

    public String getName() {
        return this.colorName;
    }

    public String getDyeName() {
        return this.dyeName;
    }

    static {
        BY_DYE_DATA = values();
        BY_WOOL_DATA = values();

        for (DyeColor color : values()) {
            BY_WOOL_DATA[color.woolColorMeta & 0x0f] = color;
            BY_DYE_DATA[color.dyeColorMeta & 0x0f] = color;
        }
    }

    public static DyeColor getByDyeData(int dyeColorMeta) {
        return BY_DYE_DATA[dyeColorMeta & 0x0f];
    }

    public static DyeColor getByWoolData(int woolColorMeta) {
        return BY_WOOL_DATA[woolColorMeta & 0x0f];
    }
}
