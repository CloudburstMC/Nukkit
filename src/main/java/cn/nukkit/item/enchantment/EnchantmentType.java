package cn.nukkit.item.enchantment;

import cn.nukkit.block.BlockPumpkin;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemBow;
import cn.nukkit.item.ItemElytra;
import cn.nukkit.item.ItemFishingRod;
import cn.nukkit.item.ItemSkull;
import cn.nukkit.item.ItemTrident;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public enum EnchantmentType {
    ALL,
    ARMOR,
    ARMOR_HEAD,
    ARMOR_TORSO,
    ARMOR_LEGS,
    ARMOR_FEET,
    SWORD,
    DIGGER,
    FISHING_ROD,
    BREAKABLE,
    BOW,
    WEARABLE,
    TRIDENT;

    public boolean canEnchantItem(Item item) {
        if (this == ALL) {
            return true;

        } else if (this == BREAKABLE && item.getMaxDurability() >= 0) {
            return true;

        } else if (item instanceof ItemArmor) {
            if (this == ARMOR) {
                return true;
            }

            switch (this) {
                case ARMOR_HEAD:
                    return item.isHelmet();
                case ARMOR_TORSO:
                    return item.isChestplate();
                case ARMOR_LEGS:
                    return item.isLeggings();
                case ARMOR_FEET:
                    return item.isBoots();
                default:
                    return false;
            }

        } else {
            switch (this) {
                case SWORD:
                    return item.isSword();
                case DIGGER:
                    return item.isPickaxe() || item.isShovel() || item.isAxe();
                case BOW:
                    return item instanceof ItemBow;
                case FISHING_ROD:
                    return item instanceof ItemFishingRod;
                case WEARABLE:
                    return item instanceof ItemArmor || item instanceof ItemElytra || item instanceof ItemSkull || item.getBlock() instanceof BlockPumpkin;
                case TRIDENT:
                    return item instanceof ItemTrident;
                default:
                    return false;
            }
        }
    }
}
