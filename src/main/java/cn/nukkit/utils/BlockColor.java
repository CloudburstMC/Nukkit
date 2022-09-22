package cn.nukkit.utils;

/**
 * Created by Snake1999 on 2016/1/10.
 * Package cn.nukkit.utils in project nukkit
 */
public class BlockColor  {

    public static final BlockColor TRANSPARENT_BLOCK_COLOR = new BlockColor(0x00, 0x00, 0x00, 0x00);
    public static final BlockColor VOID_BLOCK_COLOR = TRANSPARENT_BLOCK_COLOR;
    public static final BlockColor AIR_BLOCK_COLOR = TRANSPARENT_BLOCK_COLOR;

    public static final BlockColor GRASS_BLOCK_COLOR = new BlockColor(0x7f, 0xb2, 0x38);
    public static final BlockColor SAND_BLOCK_COLOR = new BlockColor(0xf7, 0xe9, 0xa3);
    public static final BlockColor CLOTH_BLOCK_COLOR = new BlockColor(0xc7, 0xc7, 0xc7);
    public static final BlockColor TNT_BLOCK_COLOR = new BlockColor(0xff, 0x00, 0x00);
    public static final BlockColor ICE_BLOCK_COLOR = new BlockColor(0xa0, 0xa0, 0xff);
    public static final BlockColor IRON_BLOCK_COLOR = new BlockColor(0xa7, 0xa7, 0xa7);
    public static final BlockColor FOLIAGE_BLOCK_COLOR = new BlockColor(0x00, 0x7c, 0x00);
    public static final BlockColor SNOW_BLOCK_COLOR = new BlockColor(0xff, 0xff, 0xff);
    public static final BlockColor CLAY_BLOCK_COLOR = new BlockColor(0xa4, 0xa8, 0xb8);
    public static final BlockColor DIRT_BLOCK_COLOR = new BlockColor(0x97, 0x6d, 0x4d);
    public static final BlockColor STONE_BLOCK_COLOR = new BlockColor(0x70, 0x70, 0x70);
    public static final BlockColor WATER_BLOCK_COLOR = new BlockColor(0x40, 0x40, 0xff);
    public static final BlockColor LAVA_BLOCK_COLOR = TNT_BLOCK_COLOR;
    public static final BlockColor WOOD_BLOCK_COLOR = new BlockColor(0x8f, 0x77, 0x48);
    public static final BlockColor QUARTZ_BLOCK_COLOR = new BlockColor(0xff, 0xfc, 0xf5);
    public static final BlockColor ADOBE_BLOCK_COLOR = new BlockColor(0xd8, 0x7f, 0x33);

    public static final BlockColor WHITE_BLOCK_COLOR = SNOW_BLOCK_COLOR;
    public static final BlockColor ORANGE_BLOCK_COLOR = ADOBE_BLOCK_COLOR;
    public static final BlockColor MAGENTA_BLOCK_COLOR = new BlockColor(0xb2, 0x4c, 0xd8);
    public static final BlockColor LIGHT_BLUE_BLOCK_COLOR = new BlockColor(0x66, 0x99, 0xd8);
    public static final BlockColor YELLOW_BLOCK_COLOR = new BlockColor(0xe5, 0xe5, 0x33);
    public static final BlockColor LIME_BLOCK_COLOR = new BlockColor(0x7f, 0xcc, 0x19);
    public static final BlockColor PINK_BLOCK_COLOR = new BlockColor(0xf2, 0x7f, 0xa5);
    public static final BlockColor GRAY_BLOCK_COLOR = new BlockColor(0x4c, 0x4c, 0x4c);
    public static final BlockColor LIGHT_GRAY_BLOCK_COLOR = new BlockColor(0x99, 0x99, 0x99);
    public static final BlockColor CYAN_BLOCK_COLOR = new BlockColor(0x4c, 0x7f, 0x99);
    public static final BlockColor PURPLE_BLOCK_COLOR = new BlockColor(0x7f, 0x3f, 0xb2);
    public static final BlockColor BLUE_BLOCK_COLOR = new BlockColor(0x33, 0x4c, 0xb2);
    public static final BlockColor BROWN_BLOCK_COLOR = new BlockColor(0x66, 0x4c, 0x33);
    public static final BlockColor GREEN_BLOCK_COLOR = new BlockColor(0x66, 0x7f, 0x33);
    public static final BlockColor RED_BLOCK_COLOR = new BlockColor(0x99, 0x33, 0x33);
    public static final BlockColor BLACK_BLOCK_COLOR = new BlockColor(0x19, 0x19, 0x19);

    public static final BlockColor GOLD_BLOCK_COLOR = new BlockColor(0xfa, 0xee, 0x4d);
    public static final BlockColor DIAMOND_BLOCK_COLOR = new BlockColor(0x5c, 0xdb, 0xd5);
    public static final BlockColor LAPIS_BLOCK_COLOR = new BlockColor(0x4a, 0x80, 0xff);
    public static final BlockColor EMERALD_BLOCK_COLOR = new BlockColor(0x00, 0xd9, 0x3a);
    public static final BlockColor OBSIDIAN_BLOCK_COLOR = new BlockColor(0x15, 0x14, 0x1f);
    public static final BlockColor SPRUCE_BLOCK_COLOR = new BlockColor(0x81, 0x56, 0x31);
    public static final BlockColor NETHERRACK_BLOCK_COLOR = new BlockColor(0x70, 0x02, 0x00);
    public static final BlockColor REDSTONE_BLOCK_COLOR = TNT_BLOCK_COLOR;

    public static final BlockColor WHITE_TERRACOTA_BLOCK_COLOR = new BlockColor(0xd1, 0xb1, 0xa1);
    public static final BlockColor ORANGE_TERRACOTA_BLOCK_COLOR = new BlockColor(0x9f, 0x52, 0x24);
    public static final BlockColor MAGENTA_TERRACOTA_BLOCK_COLOR = new BlockColor(0x95, 0x57, 0x6c);
    public static final BlockColor LIGHT_BLUE_TERRACOTA_BLOCK_COLOR = new BlockColor(0x70, 0x6c, 0x8a);
    public static final BlockColor YELLOW_TERRACOTA_BLOCK_COLOR = new BlockColor(0xba, 0x85, 0x24);
    public static final BlockColor LIME_TERRACOTA_BLOCK_COLOR = new BlockColor(0x67, 0x75, 0x35);
    public static final BlockColor PINK_TERRACOTA_BLOCK_COLOR = new BlockColor(0xa0, 0x4d, 0x4e);
    public static final BlockColor GRAY_TERRACOTA_BLOCK_COLOR = new BlockColor(0x39, 0x29, 0x23);
    public static final BlockColor LIGHT_GRAY_TERRACOTA_BLOCK_COLOR = new BlockColor(0x87, 0x6b, 0x62);
    public static final BlockColor CYAN_TERRACOTA_BLOCK_COLOR = new BlockColor(0x57, 0x5c, 0x5c);
    public static final BlockColor PURPLE_TERRACOTA_BLOCK_COLOR = new BlockColor(0x7a, 0x49, 0x58);
    public static final BlockColor BLUE_TERRACOTA_BLOCK_COLOR = new BlockColor(0x4c, 0x3e, 0x5c);
    public static final BlockColor BROWN_TERRACOTA_BLOCK_COLOR = new BlockColor(0x4c, 0x32, 0x23);
    public static final BlockColor GREEN_TERRACOTA_BLOCK_COLOR = new BlockColor(0x4c, 0x52, 0x2a);
    public static final BlockColor RED_TERRACOTA_BLOCK_COLOR = new BlockColor(0x8e, 0x3c, 0x2e);
    public static final BlockColor BLACK_TERRACOTA_BLOCK_COLOR = new BlockColor(0x25, 0x16, 0x10);

    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;

    public BlockColor(int red, int green, int blue, int alpha) {
        this.red = red & 0xff;
        this.green = green & 0xff;
        this.blue = blue & 0xff;
        this.alpha = alpha & 0xff;
    }

    public BlockColor(int red, int green, int blue) {
        this(red, green, blue, 0xff);
    }

    public BlockColor(int rgb) {
        this(rgb, false);
    }

    public BlockColor(int rgb, boolean hasAlpha) {
        this.red = (rgb >> 16) & 0xff;
        this.green = (rgb >> 8) & 0xff;
        this.blue = rgb & 0xff;
        this.alpha = hasAlpha ? (rgb >> 24) & 0xff : 0xff;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockColor)) {
            return false;
        }
        BlockColor other = (BlockColor) obj;
        return this.red == other.red && this.green == other.green &&
                this.blue == other.blue && this.alpha == other.alpha;
    }

    @Override
    public String toString() {
        return "BlockColor[r=" + this.red + ",g=" + this.green + ",b=" + this.blue + ",a=" + this.alpha + "]";
    }

    public int getRed() {
        return this.red;
    }

    public int getGreen() {
        return this.green;
    }

    public int getBlue() {
        return this.blue;
    }

    public int getAlpha() {
        return this.alpha;
    }

    public int getRGB() {
        return (this.red << 16 | this.green << 8 | this.blue) & 0xffffff;
    }

    public int getARGB() {
        return this.alpha << 24 | this.red << 16 | this.green << 8 | this.blue;
    }

    @Deprecated
    public static BlockColor getDyeColor(int dyeColorMeta) {
        return DyeColor.getByDyeData(dyeColorMeta).getColor();
    }
}
