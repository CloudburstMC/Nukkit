package cn.nukkit.server.util;

/**
 * Created by Snake1999 on 2016/1/10.
 * Package cn.nukkit.server.utils in project nukkit
 */
public class BlockColor extends java.awt.Color {

    public static final BlockColor TRANSPARENT_BLOCK_COLOR = new BlockColor(0x00, 0x00, 0x00, 0x00);
    public static final BlockColor VOID_BLOCK_COLOR = new BlockColor(0x00, 0x00, 0x00, 0x00);

    public static final BlockColor AIR_BLOCK_COLOR = new BlockColor(0x00, 0x00, 0x00);
    public static final BlockColor GRASS_BLOCK_COLOR = new BlockColor(0x7f, 0xb2, 0x38);
    public static final BlockColor SAND_BLOCK_COLOR = new BlockColor(0xf1, 0xe9, 0xa3);
    public static final BlockColor CLOTH_BLOCK_COLOR = new BlockColor(0xa7, 0xa7, 0xa7);
    public static final BlockColor TNT_BLOCK_COLOR = new BlockColor(0xff, 0x00, 0x00);
    public static final BlockColor ICE_BLOCK_COLOR = new BlockColor(0xa0, 0xa0, 0xff);
    public static final BlockColor IRON_BLOCK_COLOR = new BlockColor(0xa7, 0xa7, 0xa7);
    public static final BlockColor FOLIAGE_BLOCK_COLOR = new BlockColor(0x00, 0x7c, 0x00);
    public static final BlockColor SNOW_BLOCK_COLOR = new BlockColor(0xff, 0xff, 0xff);
    public static final BlockColor CLAY_BLOCK_COLOR = new BlockColor(0xa4, 0xa8, 0xb8);
    public static final BlockColor DIRT_BLOCK_COLOR = new BlockColor(0xb7, 0x6a, 0x2f);
    public static final BlockColor STONE_BLOCK_COLOR = new BlockColor(0x70, 0x70, 0x70);
    public static final BlockColor WATER_BLOCK_COLOR = new BlockColor(0x40, 0x40, 0xff);
    public static final BlockColor LAVA_BLOCK_COLOR = TNT_BLOCK_COLOR;
    public static final BlockColor WOOD_BLOCK_COLOR = new BlockColor(0x68, 0x53, 0x32);
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
    public static final BlockColor NETHERRACK_BLOCK_COLOR = new BlockColor(0x70, 0x02, 0x00);
    public static final BlockColor REDSTONE_BLOCK_COLOR = TNT_BLOCK_COLOR;

    public BlockColor(float r, float g, float b, float a) {
        super(r, g, b, a);
    }

    public BlockColor(float r, float g, float b) {
        super(r, g, b);
    }

    public BlockColor(int rgba, boolean hasAlpha) {
        super(rgba, hasAlpha);
    }

    public BlockColor(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public BlockColor(int rgb) {
        super(rgb);
    }

    public BlockColor(int r, int g, int b) {
        super(r, g, b);
    }

    @Deprecated
    public static BlockColor getDyeColor(int dyeColorMeta) {
        return DyeColor.getByDyeData(dyeColorMeta).getColor();
    }
}
