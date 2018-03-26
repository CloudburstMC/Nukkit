package cn.nukkit.api.item.util;

import cn.nukkit.api.item.ItemType;
import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cn.nukkit.api.item.ItemTypes.*;

@UtilityClass
public class ItemTypeUtil {

    public static boolean isRecord(@Nullable ItemType type) {
        if (type == null) return false;
        return type == DISC_13 || type == DISC_CAT || type == DISC_BLOCKS || type == DISC_CHIRP ||
                type == DISC_FAR || type == DISC_MALL || type == DISC_MELLOHI || type == DISC_STAL ||
                type == DISC_STRAD || type == DISC_WARD || type == DISC_11 || type == DISC_WAIT;
    }

    public static boolean isShovel(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == DIAMOND_SHOVEL || type == GOLDEN_SHOVEL || type == IRON_SHOVEL ||
                type == STONE_SHOVEL || type == WOODEN_SHOVEL;
    }

    public static boolean isPickaxe(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == DIAMOND_PICKAXE || type == GOLDEN_PICKAXE || type == IRON_PICKAXE ||
                type == STONE_PICKAXE || type == WOODEN_PICKAXE;
    }

    public static boolean isAxe(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == DIAMOND_AXE || type == GOLDEN_AXE || type == IRON_AXE ||
                type == STONE_AXE || type == WOODEN_AXE;
    }

    public static boolean isHoe(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == DIAMOND_HOE || type == GOLDEN_HOE || type == IRON_HOE ||
                type == STONE_HOE || type == WOODEN_HOE;
    }

    public static boolean isArmor(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return isHelmet(type) && isChestplate(type) && isLeggings(type) && isBoots(type);
    }

    public static boolean isHelmet(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == DIAMOND_HELMET || type == GOLDEN_HELMET || type == IRON_HELMET ||
                type == CHAIN_HELMET || type == LEATHER_CAP;
    }

    public static boolean isChestplate(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == DIAMOND_CHESTPLATE || type == GOLDEN_CHESTPLATE || type == IRON_CHESTPLATE ||
                type == CHAIN_CHESTPLATE || type == LEATHER_TUNIC;
    }

    public static boolean isLeggings(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == DIAMOND_LEGGINGS || type == GOLDEN_LEGGINGS || type == IRON_LEGGINGS ||
                type == CHAIN_LEGGINGS || type == LEATHER_PANTS;
    }

    public static boolean isBoots(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == DIAMOND_BOOTS || type == GOLDEN_BOOTS || type == IRON_BOOTS ||
                type == CHAIN_BOOTS || type == LEATHER_BOOTS;
    }

    public static boolean isWooden(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == WOODEN_AXE || type == WOODEN_PICKAXE || type == WOODEN_SHOVEL || type == WOODEN_HOE ||
                type == WOODEN_SWORD;
    }

    public static boolean isStone(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == STONE_AXE || type == STONE_PICKAXE || type == STONE_SHOVEL || type == STONE_HOE ||
                type == STONE_SWORD;
    }

    public static boolean isIron(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == IRON_AXE || type == IRON_PICKAXE || type == IRON_SHOVEL || type == IRON_HOE ||
                type == IRON_SWORD || type == IRON_HELMET || type == IRON_CHESTPLATE || type == IRON_LEGGINGS ||
                type == IRON_BOOTS || type == IRON_HORSE_ARMOR;
    }

    public static boolean isDiamond(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == DIAMOND_AXE || type == DIAMOND_PICKAXE || type == DIAMOND_SHOVEL || type == DIAMOND_HOE ||
                type == DIAMOND_SWORD || type == DIAMOND_HELMET || type == DIAMOND_CHESTPLATE || type == DIAMOND_LEGGINGS ||
                type == DIAMOND_BOOTS || type == DIAMOND_HORSE_ARMOR;
    }

    public static boolean isGolden(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == GOLDEN_AXE || type == GOLDEN_PICKAXE || type == GOLDEN_SHOVEL || type == GOLDEN_HOE ||
                type == GOLDEN_SWORD || type == GOLDEN_HELMET || type == GOLDEN_CHESTPLATE || type == GOLDEN_LEGGINGS ||
                type == GOLDEN_BOOTS || type == GOLDEN_HORSE_ARMOR;
    }

    public static boolean isSword(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == DIAMOND_SWORD || type == GOLDEN_SWORD || type == IRON_SWORD || type == STONE_SWORD ||
                type == WOODEN_SWORD;
    }

    public static boolean isChain(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == CHAIN_HELMET || type == CHAIN_CHESTPLATE || type == CHAIN_LEGGINGS || type == CHAIN_BOOTS;
    }

    public static boolean isLeather(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == LEATHER_CAP || type == LEATHER_TUNIC || type == LEATHER_PANTS || type == LEATHER_BOOTS ||
                type == LEATHER_HORSE_ARMOR;
    }
}
