package cn.nukkit.api.item.enchantment;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;

/**
 * @author CreeperFace
 */
public final class EnchantmentTypes {

    public static final IntEnchantment PROTECTION = new IntEnchantment(0, "protection", 5, EnchantmentTarget.ARMOR);
    public static final IntEnchantment FIRE_ROTECTION = new IntEnchantment(1, "protection_fire", 5, EnchantmentTarget.ARMOR);
    public static final IntEnchantment FALL_PROTECTION = new IntEnchantment(2, "protection_fall", 5, EnchantmentTarget.ARMOR);
    public static final IntEnchantment EXPLOSION_PROTECTION = new IntEnchantment(3, "protection_explosion", 5, EnchantmentTarget.ARMOR);
    public static final IntEnchantment PROJECTILE_PROTECTION = new IntEnchantment(4, "protection, projectile", 5, EnchantmentTarget.ARMOR);
    public static final IntEnchantment THORNS = new IntEnchantment(5, "thorns", 3, EnchantmentTarget.ARMOR);
    public static final IntEnchantment WATER_BREATHING = new IntEnchantment(6, "water_breathing", 3, EnchantmentTarget.ARMOR_HELMET);
    public static final IntEnchantment WATER_WALKER = new IntEnchantment(7, "water_walker", 3, EnchantmentTarget.ARMOR_BOOTS);
    public static final IntEnchantment WATER_WORKER = new IntEnchantment(8, "water_worker", 3, EnchantmentTarget.ARMOR_HELMET);
    public static final IntEnchantment SHARPNESS = new IntEnchantment(9, "sharpness", 5, EnchantmentTarget.WEAPON);
    public static final IntEnchantment SMITE = new IntEnchantment(10, "smite", 5, EnchantmentTarget.WEAPON);
    public static final IntEnchantment DAMAGE_ARTHOPODS = new IntEnchantment(11, "bane_of_arthoponds", 5, EnchantmentTarget.WEAPON);
    public static final IntEnchantment KNOCKBACK = new IntEnchantment(12, "knockback", 2, EnchantmentTarget.WEAPON);
    public static final IntEnchantment FIRE_ASPECT = new IntEnchantment(13, "fire_aspect", 2, EnchantmentTarget.WEAPON);
    public static final IntEnchantment LOOTING = new IntEnchantment(14, "looting", 3, EnchantmentTarget.WEAPON);
    public static final IntEnchantment EFFICIENCY = new IntEnchantment(15, "efficiency", 5, EnchantmentTarget.TOOL);
    public static final IntEnchantment SILK_TOUCH = new IntEnchantment(16, "silk_touch", 1, EnchantmentTarget.TOOL);
    public static final IntEnchantment DURABILITY = new IntEnchantment(17, "durability", 3, EnchantmentTarget.ALL);
    public static final IntEnchantment FORTUNE = new IntEnchantment(18, "fortune", 3, EnchantmentTarget.TOOL);
    public static final IntEnchantment POWER = new IntEnchantment(19, "power", 5, EnchantmentTarget.BOW);
    public static final IntEnchantment PUNCH = new IntEnchantment(20, "punch", 2, EnchantmentTarget.BOW);
    public static final IntEnchantment FLAME = new IntEnchantment(21, "flame", 1, EnchantmentTarget.BOW);
    public static final IntEnchantment INFINITY = new IntEnchantment(22, "infinity", 1, EnchantmentTarget.BOW);
    public static final IntEnchantment LUCK = new IntEnchantment(23, "luck", 3, EnchantmentTarget.FISHING_ROD);
    public static final IntEnchantment LURE = new IntEnchantment(24, "lure", 3, EnchantmentTarget.FISHING_ROD);

    private static final TIntObjectMap<IntEnchantment> BY_ID = new TIntObjectHashMap<>(25);

    @Getter
    public static class IntEnchantment implements EnchantmentType {

        final int id;
        final String name;
        final int maxLevel;
        final EnchantmentTarget target;

        public IntEnchantment(int id, String name, int maxLevel, EnchantmentTarget target) {
            this.id = id;
            this.name = name;
            this.maxLevel = maxLevel;
            this.target = target;
        }
    }
}
