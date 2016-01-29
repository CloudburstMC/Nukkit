package cn.nukkit.item.enchantment;

import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentLevelTable {

    private static HashMap<Integer, Range[]> map = new HashMap<>();

    public static void init() {
        HashMap<Integer, ArrayList<Range>> rangeMap;
        //Armor
        map = new HashMap<Integer, Range[]>() {{
            put(Enchantment.TYPE_ARMOR_PROTECTION, new Range[]{
                    new Range(1, 21),
                    new Range(12, 32),
                    new Range(23, 43),
                    new Range(34, 54)
            });

            put(Enchantment.TYPE_ARMOR_FIRE_PROTECTION, new Range[]{
                    new Range(10, 22),
                    new Range(18, 30),
                    new Range(26, 38),
                    new Range(34, 46)
            });

            put(Enchantment.TYPE_ARMOR_FALL_PROTECTION, new Range[]{
                    new Range(5, 12),
                    new Range(11, 21),
                    new Range(17, 27),
                    new Range(23, 33)
            });

            put(Enchantment.TYPE_ARMOR_EXPLOSION_PROTECTION, new Range[]{
                    new Range(5, 17),
                    new Range(13, 25),
                    new Range(21, 33),
                    new Range(29, 41)
            });

            put(Enchantment.TYPE_ARMOR_PROJECTILE_PROTECTION, new Range[]{
                    new Range(3, 18),
                    new Range(9, 24),
                    new Range(15, 30),
                    new Range(21, 36)
            });

            put(Enchantment.TYPE_WATER_BREATHING, new Range[]{
                    new Range(10, 40),
                    new Range(20, 50),
                    new Range(30, 60)
            });

            put(Enchantment.TYPE_WATER_AFFINITY, new Range[]{
                    new Range(10, 41)
            });

            put(Enchantment.TYPE_ARMOR_THORNS, new Range[]{
                    new Range(10, 60),
                    new Range(30, 80),
                    new Range(50, 100)
            });

            //Weapon
            put(Enchantment.TYPE_WEAPON_SHARPNESS, new Range[]{
                    new Range(1, 21),
                    new Range(12, 32),
                    new Range(23, 43),
                    new Range(34, 54),
                    new Range(45, 65)
            });

            put(Enchantment.TYPE_WEAPON_SMITE, new Range[]{
                    new Range(5, 25),
                    new Range(13, 33),
                    new Range(21, 41),
                    new Range(29, 49),
                    new Range(37, 57)
            });

            put(Enchantment.TYPE_WEAPON_ARTHROPODS, new Range[]{
                    new Range(5, 25),
                    new Range(13, 33),
                    new Range(21, 41),
                    new Range(29, 49),
                    new Range(37, 57)
            });

            put(Enchantment.TYPE_WEAPON_KNOCKBACK, new Range[]{
                    new Range(5, 55),
                    new Range(25, 75)
            });

            put(Enchantment.TYPE_WEAPON_FIRE_ASPECT, new Range[]{
                    new Range(10, 60),
                    new Range(30, 80)
            });

            put(Enchantment.TYPE_WEAPON_LOOTING, new Range[]{
                    new Range(15, 65),
                    new Range(24, 74),
                    new Range(33, 83)
            });

            //Bow
            put(Enchantment.TYPE_BOW_POWER, new Range[]{
                    new Range(1, 16),
                    new Range(11, 26),
                    new Range(21, 36),
                    new Range(31, 46),
                    new Range(41, 56)
            });

            put(Enchantment.TYPE_BOW_KNOCKBACK, new Range[]{
                    new Range(12, 37),
                    new Range(32, 57)
            });

            put(Enchantment.TYPE_BOW_FLAME, new Range[]{
                    new Range(20, 50)
            });

            put(Enchantment.TYPE_BOW_INFINITY, new Range[]{
                    new Range(20, 50)
            });

            //Mining
            put(Enchantment.TYPE_MINING_EFFICIENCY, new Range[]{
                    new Range(1, 51),
                    new Range(11, 61),
                    new Range(21, 71),
                    new Range(31, 81),
                    new Range(41, 91)
            });

            put(Enchantment.TYPE_MINING_SILK_TOUCH, new Range[]{
                    new Range(15, 65)
            });

            put(Enchantment.TYPE_MINING_DURABILITY, new Range[]{
                    new Range(5, 55),
                    new Range(13, 63),
                    new Range(21, 71)
            });

            put(Enchantment.TYPE_MINING_FORTUNE, new Range[]{
                    new Range(15, 55),
                    new Range(24, 74),
                    new Range(33, 83)
            });

            //Fishing
            put(Enchantment.TYPE_FISHING_FORTUNE, new Range[]{
                    new Range(15, 65),
                    new Range(24, 74),
                    new Range(33, 83)
            });

            put(Enchantment.TYPE_FISHING_LURE, new Range[]{
                    new Range(15, 65),
                    new Range(24, 74),
                    new Range(33, 83)
            });
        }};

    }

    public static ArrayList<Enchantment> getPossibleEnchantments(Item item, int modifiedLevel) {
        Map<Integer, Enchantment> result = new HashMap<>();

        ArrayList<Integer> enchantmentIds = new ArrayList<>();

        if (item.getId() == Item.BOOK) {
            enchantmentIds.addAll(map.keySet());

        } else if (item.isArmor()) {
            enchantmentIds.add(Enchantment.TYPE_ARMOR_PROTECTION);
            enchantmentIds.add(Enchantment.TYPE_ARMOR_FIRE_PROTECTION);
            enchantmentIds.add(Enchantment.TYPE_ARMOR_EXPLOSION_PROTECTION);
            enchantmentIds.add(Enchantment.TYPE_ARMOR_PROJECTILE_PROTECTION);
            enchantmentIds.add(Enchantment.TYPE_ARMOR_THORNS);

            if (item.isBoots()) {
                enchantmentIds.add(Enchantment.TYPE_ARMOR_FALL_PROTECTION);
            }

            if (item.isHelmet()) {
                enchantmentIds.add(Enchantment.TYPE_WATER_BREATHING);
                enchantmentIds.add(Enchantment.TYPE_WATER_AFFINITY);
            }

        } else if (item.isSword()) {
            enchantmentIds.add(Enchantment.TYPE_WEAPON_SHARPNESS);
            enchantmentIds.add(Enchantment.TYPE_WEAPON_SMITE);
            enchantmentIds.add(Enchantment.TYPE_WEAPON_ARTHROPODS);
            enchantmentIds.add(Enchantment.TYPE_WEAPON_KNOCKBACK);
            enchantmentIds.add(Enchantment.TYPE_WEAPON_FIRE_ASPECT);
            enchantmentIds.add(Enchantment.TYPE_WEAPON_LOOTING);

        } else if (item.isTool()) {
            enchantmentIds.add(Enchantment.TYPE_MINING_EFFICIENCY);
            enchantmentIds.add(Enchantment.TYPE_MINING_SILK_TOUCH);
            enchantmentIds.add(Enchantment.TYPE_MINING_FORTUNE);

        } else if (item.getId() == Item.BOW) {
            enchantmentIds.add(Enchantment.TYPE_BOW_POWER);
            enchantmentIds.add(Enchantment.TYPE_BOW_KNOCKBACK);
            enchantmentIds.add(Enchantment.TYPE_BOW_FLAME);
            enchantmentIds.add(Enchantment.TYPE_BOW_INFINITY);

        } else if (item.getId() == Item.FISHING_ROD) {
            enchantmentIds.add(Enchantment.TYPE_FISHING_FORTUNE);
            enchantmentIds.add(Enchantment.TYPE_FISHING_LURE);

        }

        if (item.isTool() || item.isArmor()) {
            enchantmentIds.add(Enchantment.TYPE_MINING_DURABILITY);
        }

        for (int enchantmentId : enchantmentIds) {
            Enchantment enchantment = Enchantment.getEnchantment(enchantmentId);
            Range[] ranges = map.get(enchantmentId);
            int i = 0;
            for (Range range : ranges) {
                i++;
                if (range.isInRange(modifiedLevel)) {
                    result.put(enchantmentId, enchantment.setLevel(i));
                }
            }
        }

        return new ArrayList<>(result.values());
    }

    public static class Range {
        int minValue;
        int maxValue;

        public Range(int min, int max) {
            this.minValue = min;
            this.maxValue = max;
        }

        public boolean isInRange(int v) {
            return v >= minValue && v <= maxValue;
        }
    }
}
