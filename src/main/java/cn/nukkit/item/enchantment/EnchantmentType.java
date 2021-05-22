package cn.nukkit.item.enchantment;

import cn.nukkit.api.Since;
import cn.nukkit.block.BlockCarvedPumpkin;
import cn.nukkit.item.*;

import javax.annotation.Nonnull;

/**
 * @author MagicDroidX (Nukkit Project)
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
    TRIDENT,
    @Since("1.4.0.0-PN") CROSSBOW;

    public boolean canEnchantItem(@Nonnull Item item) {
        if (this == ALL) {
            return true;

        } else if (this == BREAKABLE && item.getMaxDurability() >= 0) {
            return true;

        } else if (item instanceof ItemArmor) {
            if (this == ARMOR || this == WEARABLE) {
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
                    return item.isSword() && !(item instanceof ItemTrident);
                case DIGGER:
                    return item.isPickaxe() || item.isShovel() || item.isAxe() || item.isHoe();
                case BOW:
                    return item instanceof ItemBow;
                case FISHING_ROD:
                    return item instanceof ItemFishingRod;
                case WEARABLE:
                    return item instanceof ItemSkull || item.getBlock() instanceof BlockCarvedPumpkin;
                case TRIDENT:
                    return item instanceof ItemTrident;
                case CROSSBOW:
                    return item instanceof ItemCrossbow;
                default:
                    return false;
            }
        }
    }
}
