package cn.nukkit.item;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
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

    @Deprecated
    @DeprecationDetails(
            since = "1.3.2.0-PN",
            reason = "Dye item now have they own ids, and their implementation extends ItemDye, " +
                    "so you may get 0 as meta result even though you have a colored dye.",
            replaceWith = "new ItemInkSac()"
    )
    public ItemDye() {
        this(0, 1);
    }

    @Deprecated
    @DeprecationDetails(
            since = "1.3.2.0-PN",
            reason = "Dye item now have they own ids, and their implementation extends ItemDye, " +
                    "so you may get 0 as meta result even though you have a colored dye.",
            replaceWith = "An item class specific for the item you want. Eg: ItemDyeGray, ItemDyeWhite, ItemBoneMeal, etc"
    )
    public ItemDye(Integer meta) {
        this(meta, 1);
    }

    @Deprecated
    @DeprecationDetails(
            since = "1.3.2.0-PN",
            reason = "Dye item now have they own ids, and their implementation extends ItemDye, " +
                    "so you may get 0 as meta result even though you have a colored dye.",
            replaceWith = "An item class specific for the item you want. Eg: ItemDyeGray, ItemDyeWhite, ItemBoneMeal, etc"
    )
    public ItemDye(DyeColor dyeColor) {
        this(dyeColor.getDyeData(), 1);
    }

    @Deprecated
    @DeprecationDetails(
            since = "1.3.2.0-PN",
            reason = "Dye item now have they own ids, and their implementation extends ItemDye, " +
                    "so you may get 0 as meta result even though you have a colored dye.",
            replaceWith = "An item class specific for the item you want. Eg: ItemDyeGray, ItemDyeWhite, ItemBoneMeal, etc"
    )
    public ItemDye(DyeColor dyeColor, int amount) {
        this(dyeColor.getDyeData(), amount);
    }

    @Deprecated
    @DeprecationDetails(
            since = "1.3.2.0-PN",
            reason = "Dye item now have they own ids, and their implementation extends ItemDye, " +
                    "so you may get 0 as meta result even though you have a colored dye.",
            replaceWith = "An item class specific for the item you want. Eg: ItemDyeGray, ItemDyeWhite, ItemBoneMeal, etc"
    )
    public ItemDye(Integer meta, int amount) {
        super(DYE, meta, amount, DyeColor.getByDyeData(meta).getDyeName());

        if (this.meta == DyeColor.BROWN.getDyeData()) {
            this.block = Block.get(BlockID.COCOA_BLOCK);
        }
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    protected ItemDye(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Deprecated
    @DeprecationDetails(
            since = "1.3.2.0-PN",
            reason = "Dye item now have they own ids, and their implementation extends ItemDye, " +
                    "so you may get 0 as result even though you have a colored dye.",
            replaceWith = "getDyeColor() or isFertilizer()"
    )
    @Override
    public int getDamage() {
        return super.getDamage();
    }

    @Since("1.3.2.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isFertilizer() {
        return getId() == DYE && getDyeColor().equals(DyeColor.WHITE);
    }
    
    @Since("1.3.2.0-PN")
    @PowerNukkitOnly
    public boolean isLapisLazuli() {
        return getId() == DYE && getDyeColor().equals(DyeColor.BLUE);
    }

    @Since("1.3.2.0-PN")
    @PowerNukkitOnly
    public boolean isCocoaBeans() {
        return getId() == DYE && getDyeColor().equals(DyeColor.BROWN);
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
