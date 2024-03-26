package cn.nukkit.utils;

/**
 * Terracotta color enum
 */
public enum TerracottaColor {

    BLACK(0, 15, "Black", "Ink Sac", BlockColor.BLACK_TERRACOTA_BLOCK_COLOR),
    RED(1, 14, "Red", "Rose Red", BlockColor.RED_TERRACOTA_BLOCK_COLOR),
    GREEN(2, 13, "Green", "Cactus Green", BlockColor.GREEN_TERRACOTA_BLOCK_COLOR),
    BROWN(3, 12, "Brown", "Cocoa Beans", BlockColor.BROWN_TERRACOTA_BLOCK_COLOR),
    BLUE(4, 11, "Blue", "Lapis Lazuli", BlockColor.BLUE_TERRACOTA_BLOCK_COLOR),
    PURPLE(5, 10, "Purple", BlockColor.PURPLE_TERRACOTA_BLOCK_COLOR),
    CYAN(6, 9, "Cyan", BlockColor.CYAN_TERRACOTA_BLOCK_COLOR),
    LIGHT_GRAY(7, 8, "Light Gray", BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR),
    GRAY(8, 7, "Gray", BlockColor.GRAY_TERRACOTA_BLOCK_COLOR),
    PINK(9, 6, "Pink", BlockColor.PINK_TERRACOTA_BLOCK_COLOR),
    LIME(10, 5, "Lime", BlockColor.LIME_TERRACOTA_BLOCK_COLOR),
    YELLOW(11, 4, "Yellow", "Dandelion Yellow", BlockColor.YELLOW_TERRACOTA_BLOCK_COLOR),
    LIGHT_BLUE(12, 3, "Light Blue", BlockColor.LIGHT_BLUE_TERRACOTA_BLOCK_COLOR),
    MAGENTA(13, 2, "Magenta", BlockColor.MAGENTA_TERRACOTA_BLOCK_COLOR),
    ORANGE(14, 1, "Orange", BlockColor.ORANGE_TERRACOTA_BLOCK_COLOR),
    WHITE(15, 0, "White", "Bone Meal", BlockColor.WHITE_TERRACOTA_BLOCK_COLOR);


    private final int dyeColorMeta;
    private final int terracottaColorMeta;
    private final String colorName;
    private final String dyeName;
    private final BlockColor blockColor;


    private final static TerracottaColor[] BY_TERRACOTA_DATA;
    private final static TerracottaColor[] BY_DYE_DATA;

    TerracottaColor(int dyeColorMeta, int terracottaColorMeta, String colorName, BlockColor blockColor) {
        this(dyeColorMeta, terracottaColorMeta, colorName, colorName + " Dye", blockColor);
    }

    TerracottaColor(int dyeColorMeta, int terracottaColorMeta, String colorName, String dyeName, BlockColor blockColor) {
        this.dyeColorMeta = dyeColorMeta;
        this.terracottaColorMeta = terracottaColorMeta;
        this.colorName = colorName;
        this.blockColor = blockColor;
        this.dyeName = dyeName;
    }

    /**
     * Get as BlockColor
     *
     * @return BlockColor of the TerracottaColor
     */
    public BlockColor getColor() {
        return this.blockColor;
    }

    /**
     * Get as dye item meta value
     *
     * @return dye item meta value of the TerracottaColor
     */
    public int getDyeData() {
        return this.dyeColorMeta;
    }

    /**
     * Get as terracotta block meta value
     *
     * @return terracotta block meta value of the TerracottaColor
     */
    public int getTerracottaData() {
        return this.terracottaColorMeta;
    }

    /**
     * Get color name
     *
     * @return color name
     */
    public String getName() {
        return this.colorName;
    }

    /**
     * Get dye name
     *
     * @return dye name
     */
    public String getDyeName() {
        return this.dyeName;
    }

    static {
        BY_DYE_DATA = values();
        BY_TERRACOTA_DATA = values();

        for (TerracottaColor color : values()) {
            BY_TERRACOTA_DATA[color.terracottaColorMeta & 0x0f] = color;
            BY_DYE_DATA[color.dyeColorMeta & 0x0f] = color;
        }
    }

    /**
     * Get TerracottaColor by dye item meta value
     * @param dyeColorMeta dye item meta value
     * @return TerracottaColor
     */
    public static TerracottaColor getByDyeData(int dyeColorMeta) {
        return BY_DYE_DATA[dyeColorMeta & 0x0f];
    }

    /**
     * Get TerracottaColor by terracotta block meta value
     * @param terracottaColorMeta terracotta block meta value
     * @return TerracottaColor
     */
    public static TerracottaColor getByTerracottaData(int terracottaColorMeta) {
        return BY_TERRACOTA_DATA[terracottaColorMeta & 0x0f];
    }
}
