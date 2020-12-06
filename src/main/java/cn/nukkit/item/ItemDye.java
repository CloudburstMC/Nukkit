package cn.nukkit.item;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * @author MagicDroidX (Nukkit Project)
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

    @Since("1.3.2.0-PN")
    @PowerNukkitOnly
    @Override
    public Item selfUpgrade() {
        if (getId() != DYE) {
            return super.selfUpgrade();
        }
        int newId;
        switch (getDamage()) {
            case 19: newId = WHITE_DYE; break;
            case 18: newId = BLUE_DYE; break;
            case 17: newId = BROWN_DYE; break;
            case 16: newId = BLACK_DYE; break;
            case 15: newId = BONE_MEAL; break;
            case 14: newId = ORANGE_DYE; break; 
            case 13: newId = MAGENTA_DYE; break; 
            case 12: newId = LIGHT_BLUE_DYE; break; 
            case 11: newId = YELLOW_DYE; break; 
            case 10: newId = LIME_DYE; break; 
            case 9: newId = PINK_DYE; break; 
            case 8: newId = GRAY_DYE; break; 
            case 7: newId = LIGHT_GRAY_DYE; break; 
            case 6: newId = CYAN_DYE; break; 
            case 5: newId = PURPLE_DYE; break; 
            case 4: newId = LAPIS_LAZULI; break; 
            case 3: newId = COCOA_BEANS; break; 
            case 2: newId = GREEN_DYE; break; 
            case 1: newId = RED_DYE; break; 
            case 0: newId = INK_SAC; break;
            default:
                return super.selfUpgrade();
        }
        return Item.get(newId, 0, getCount(), getCompoundTag());
    }
}
