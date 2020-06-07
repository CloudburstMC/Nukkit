package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemDye extends Item {

    @Deprecated
    public static final int WHITE = DyeColor.WHITE.getDyeData();
    @Deprecated
    public static final int ORANGE = DyeColor.ORANGE.getDyeData();
    @Deprecated
    public static final int MAGENTA = DyeColor.MAGENTA.getDyeData();
    @Deprecated
    public static final int LIGHT_BLUE = DyeColor.LIGHT_BLUE.getDyeData();
    @Deprecated
    public static final int YELLOW = DyeColor.YELLOW.getDyeData();
    @Deprecated
    public static final int LIME = DyeColor.LIME.getDyeData();
    @Deprecated
    public static final int PINK = DyeColor.PINK.getDyeData();
    @Deprecated
    public static final int GRAY = DyeColor.GRAY.getDyeData();
    @Deprecated
    public static final int LIGHT_GRAY = DyeColor.LIGHT_GRAY.getDyeData();
    @Deprecated
    public static final int CYAN = DyeColor.CYAN.getDyeData();
    @Deprecated
    public static final int PURPLE = DyeColor.PURPLE.getDyeData();
    @Deprecated
    public static final int BLUE = DyeColor.BLUE.getDyeData();
    @Deprecated
    public static final int BROWN = DyeColor.BROWN.getDyeData();
    @Deprecated
    public static final int GREEN = DyeColor.GREEN.getDyeData();
    @Deprecated
    public static final int RED = DyeColor.RED.getDyeData();
    @Deprecated
    public static final int BLACK = DyeColor.BLACK.getDyeData();

    public ItemDye() {
        this(0, 1);
    }

    public ItemDye(Integer meta) {
        this(meta, 1);
    }

    public ItemDye(DyeColor dyeColor) {
        this(dyeColor.getDyeData(), 1);
    }

    public ItemDye(DyeColor dyeColor, int amount) {
        this(dyeColor.getDyeData(), amount);
    }

    public ItemDye(Integer meta, int amount) {
        super(DYE, meta, amount, DyeColor.getByDyeData(meta).getDyeName());

        if (this.meta == DyeColor.BROWN.getDyeData()) {
            this.block = Block.get(BlockID.COCOA_BLOCK);
        }
    }

    @Deprecated
    public static BlockColor getColor(int meta) {
        return DyeColor.getByDyeData(meta).getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByDyeData(meta);
    }

    @Deprecated
    public static String getColorName(int meta) {
        return DyeColor.getByDyeData(meta).getName();
    }
}
