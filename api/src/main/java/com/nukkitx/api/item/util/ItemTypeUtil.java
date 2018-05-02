package com.nukkitx.api.item.util;

import com.google.common.base.Preconditions;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.ItemTypes;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@UtilityClass
public class ItemTypeUtil {

    public static boolean isRecord(@Nullable ItemType type) {
        if (type == null) return false;
        return type == ItemTypes.DISC_13 || type == ItemTypes.DISC_CAT || type == ItemTypes.DISC_BLOCKS || type == ItemTypes.DISC_CHIRP ||
                type == ItemTypes.DISC_FAR || type == ItemTypes.DISC_MALL || type == ItemTypes.DISC_MELLOHI || type == ItemTypes.DISC_STAL ||
                type == ItemTypes.DISC_STRAD || type == ItemTypes.DISC_WARD || type == ItemTypes.DISC_11 || type == ItemTypes.DISC_WAIT;
    }

    public static boolean isShovel(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.DIAMOND_SHOVEL || type == ItemTypes.GOLDEN_SHOVEL || type == ItemTypes.IRON_SHOVEL ||
                type == ItemTypes.STONE_SHOVEL || type == ItemTypes.WOODEN_SHOVEL;
    }

    public static boolean isPickaxe(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.DIAMOND_PICKAXE || type == ItemTypes.GOLDEN_PICKAXE || type == ItemTypes.IRON_PICKAXE ||
                type == ItemTypes.STONE_PICKAXE || type == ItemTypes.WOODEN_PICKAXE;
    }

    public static boolean isAxe(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.DIAMOND_AXE || type == ItemTypes.GOLDEN_AXE || type == ItemTypes.IRON_AXE ||
                type == ItemTypes.STONE_AXE || type == ItemTypes.WOODEN_AXE;
    }

    public static boolean isHoe(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.DIAMOND_HOE || type == ItemTypes.GOLDEN_HOE || type == ItemTypes.IRON_HOE ||
                type == ItemTypes.STONE_HOE || type == ItemTypes.WOODEN_HOE;
    }

    public static boolean isArmor(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return isHelmet(type) && isChestplate(type) && isLeggings(type) && isBoots(type);
    }

    public static boolean isHelmet(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.DIAMOND_HELMET || type == ItemTypes.GOLDEN_HELMET || type == ItemTypes.IRON_HELMET ||
                type == ItemTypes.CHAIN_HELMET || type == ItemTypes.LEATHER_CAP;
    }

    public static boolean isChestplate(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.DIAMOND_CHESTPLATE || type == ItemTypes.GOLDEN_CHESTPLATE || type == ItemTypes.IRON_CHESTPLATE ||
                type == ItemTypes.CHAIN_CHESTPLATE || type == ItemTypes.LEATHER_TUNIC;
    }

    public static boolean isLeggings(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.DIAMOND_LEGGINGS || type == ItemTypes.GOLDEN_LEGGINGS || type == ItemTypes.IRON_LEGGINGS ||
                type == ItemTypes.CHAIN_LEGGINGS || type == ItemTypes.LEATHER_PANTS;
    }

    public static boolean isBoots(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.DIAMOND_BOOTS || type == ItemTypes.GOLDEN_BOOTS || type == ItemTypes.IRON_BOOTS ||
                type == ItemTypes.CHAIN_BOOTS || type == ItemTypes.LEATHER_BOOTS;
    }

    public static boolean isWooden(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.WOODEN_AXE || type == ItemTypes.WOODEN_PICKAXE || type == ItemTypes.WOODEN_SHOVEL || type == ItemTypes.WOODEN_HOE ||
                type == ItemTypes.WOODEN_SWORD;
    }

    public static boolean isStone(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.STONE_AXE || type == ItemTypes.STONE_PICKAXE || type == ItemTypes.STONE_SHOVEL || type == ItemTypes.STONE_HOE ||
                type == ItemTypes.STONE_SWORD;
    }

    public static boolean isIron(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.IRON_AXE || type == ItemTypes.IRON_PICKAXE || type == ItemTypes.IRON_SHOVEL || type == ItemTypes.IRON_HOE ||
                type == ItemTypes.IRON_SWORD || type == ItemTypes.IRON_HELMET || type == ItemTypes.IRON_CHESTPLATE || type == ItemTypes.IRON_LEGGINGS ||
                type == ItemTypes.IRON_BOOTS || type == ItemTypes.IRON_HORSE_ARMOR;
    }

    public static boolean isDiamond(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.DIAMOND_AXE || type == ItemTypes.DIAMOND_PICKAXE || type == ItemTypes.DIAMOND_SHOVEL || type == ItemTypes.DIAMOND_HOE ||
                type == ItemTypes.DIAMOND_SWORD || type == ItemTypes.DIAMOND_HELMET || type == ItemTypes.DIAMOND_CHESTPLATE || type == ItemTypes.DIAMOND_LEGGINGS ||
                type == ItemTypes.DIAMOND_BOOTS || type == ItemTypes.DIAMOND_HORSE_ARMOR;
    }

    public static boolean isGolden(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.GOLDEN_AXE || type == ItemTypes.GOLDEN_PICKAXE || type == ItemTypes.GOLDEN_SHOVEL || type == ItemTypes.GOLDEN_HOE ||
                type == ItemTypes.GOLDEN_SWORD || type == ItemTypes.GOLDEN_HELMET || type == ItemTypes.GOLDEN_CHESTPLATE || type == ItemTypes.GOLDEN_LEGGINGS ||
                type == ItemTypes.GOLDEN_BOOTS || type == ItemTypes.GOLDEN_HORSE_ARMOR;
    }

    public static boolean isSword(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.DIAMOND_SWORD || type == ItemTypes.GOLDEN_SWORD || type == ItemTypes.IRON_SWORD || type == ItemTypes.STONE_SWORD ||
                type == ItemTypes.WOODEN_SWORD;
    }

    public static boolean isChain(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.CHAIN_HELMET || type == ItemTypes.CHAIN_CHESTPLATE || type == ItemTypes.CHAIN_LEGGINGS || type == ItemTypes.CHAIN_BOOTS;
    }

    public static boolean isLeather(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.LEATHER_CAP || type == ItemTypes.LEATHER_TUNIC || type == ItemTypes.LEATHER_PANTS || type == ItemTypes.LEATHER_BOOTS ||
                type == ItemTypes.LEATHER_HORSE_ARMOR;
    }
}
