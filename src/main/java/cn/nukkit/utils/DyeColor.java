package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.MathHelper;

import java.util.Arrays;

public enum DyeColor {


    BLACK(0, 15, 16, "Black", "Ink Sack", BlockColor.BLACK_BLOCK_COLOR, new BlockColor(0x1D1D21)),
    RED(1, 14, 1, "Red", "Rose Red", BlockColor.RED_BLOCK_COLOR, new BlockColor(0xB02E26)),
    GREEN(2, 13, 2, "Green", "Cactus Green", BlockColor.GREEN_BLOCK_COLOR, new BlockColor(0x5E7C16)),
    BROWN(3, 12, 17, "Brown", "Cocoa Beans", BlockColor.BROWN_BLOCK_COLOR, new BlockColor(0x835432)),
    BLUE(4, 11, 18, "Blue", "Lapis Lazuli", BlockColor.BLUE_BLOCK_COLOR, new BlockColor(0x3C44AA)),
    PURPLE(5, 10, 5, "Purple", BlockColor.PURPLE_BLOCK_COLOR, new BlockColor(0x8932B8)),
    CYAN(6, 9, 6, "Cyan", BlockColor.CYAN_BLOCK_COLOR, new BlockColor(0x169C9C)),
    LIGHT_GRAY(7, 8, 7, "Light Gray", BlockColor.LIGHT_GRAY_BLOCK_COLOR, new BlockColor(0x9D9D97)),
    GRAY(8, 7, 8, "Gray", BlockColor.GRAY_BLOCK_COLOR, new BlockColor(0x474F52)),
    PINK(9, 6, 9, "Pink", BlockColor.PINK_BLOCK_COLOR, new BlockColor(0xF38BAA)),
    LIME(10, 5, 10, "Lime", BlockColor.LIME_BLOCK_COLOR, new BlockColor(0x80C71F)),
    YELLOW(11, 4, 11, "Yellow", "Dandelion Yellow", BlockColor.YELLOW_BLOCK_COLOR, new BlockColor(0xFED83D)),
    LIGHT_BLUE(12, 3, 12, "Light Blue", BlockColor.LIGHT_BLUE_BLOCK_COLOR, new BlockColor(0x3AB3DA)),
    MAGENTA(13, 2, 13, "Magenta", BlockColor.MAGENTA_BLOCK_COLOR, new BlockColor(0xC74EBD)),
    ORANGE(14, 1, 14, "Orange", BlockColor.ORANGE_BLOCK_COLOR, new BlockColor(0xFF9801)),
    WHITE(15, 0, 19, "White", "Bone Meal", BlockColor.WHITE_BLOCK_COLOR, new BlockColor(0xF0F0F0));


    private int dyeColorMeta;
    private int itemDyeMeta;
    private int woolColorMeta;
    private String colorName;
    private String dyeName;
    private BlockColor blockColor;
    private BlockColor leatherColor;


    private final static DyeColor[] BY_WOOL_DATA;
    private final static DyeColor[] BY_DYE_DATA;

    DyeColor(int dyeColorMeta, int woolColorMeta, int itemDyeMeta, String colorName, BlockColor blockColor) {
        this(dyeColorMeta, woolColorMeta, itemDyeMeta, colorName, blockColor, blockColor);
    }

    DyeColor(int dyeColorMeta, int woolColorMeta, int itemDyeMeta, String colorName, BlockColor blockColor, BlockColor leatherColor) {
        this(dyeColorMeta, woolColorMeta, itemDyeMeta, colorName, colorName + " Dye", blockColor, leatherColor);
    }

    DyeColor(int dyeColorMeta, int woolColorMeta, int itemDyeMeta, String colorName, String dyeName, BlockColor blockColor) {
        this(dyeColorMeta, woolColorMeta, itemDyeMeta, colorName, blockColor, blockColor);
    }

    DyeColor(int dyeColorMeta, int woolColorMeta, int itemDyeMeta, String colorName, String dyeName, BlockColor blockColor, BlockColor leatherColor) {
        this.dyeColorMeta = dyeColorMeta;
        this.woolColorMeta = woolColorMeta;
        this.itemDyeMeta = itemDyeMeta;
        this.colorName = colorName;
        this.blockColor = blockColor;
        this.dyeName = dyeName;
        this.leatherColor = leatherColor;
    }

    public BlockColor getColor() {
        return this.blockColor;
    }

    /**
     * The {@code minecraft:dye} meta from `0-15` that represents the source of a dye. Includes
     * ink_sac, bone_meal, cocoa_beans, and lapis_lazuli.
     */
    public int getDyeData() {
        return this.dyeColorMeta;
    }

    /**
     * The {@code minecraft:dye} meta that actually represents the item dye for that color.
     * Uses black_dye instead of ink_sac, white_dye instead of bone_meal, and so on.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getItemDyeMeta() {
        return itemDyeMeta;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockColor getLeatherColor() {
        return leatherColor;
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
        BY_WOOL_DATA = values();
        BY_DYE_DATA = new DyeColor[Arrays.stream(BY_WOOL_DATA).mapToInt(DyeColor::getItemDyeMeta).max().orElse(0) + 1];

        for (DyeColor dyeColor : BY_WOOL_DATA) {
            BY_DYE_DATA[dyeColor.dyeColorMeta] = dyeColor;
            BY_DYE_DATA[dyeColor.itemDyeMeta] = dyeColor;
        }

        for (DyeColor color : values()) {
            BY_WOOL_DATA[color.woolColorMeta & 0x0f] = color;
        }
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "When overflowed, instead of wrapping, the meta will be clamped, accepts the new dye metas")
    public static DyeColor getByDyeData(int dyeColorMeta) {
        return BY_DYE_DATA[MathHelper.clamp(dyeColorMeta, 0, BY_DYE_DATA.length - 1)];
    }

    public static DyeColor getByWoolData(int woolColorMeta) {
        return BY_WOOL_DATA[woolColorMeta & 0x0f];
    }
}
