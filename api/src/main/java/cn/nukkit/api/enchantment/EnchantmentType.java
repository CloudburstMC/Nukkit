/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.api.enchantment;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public enum EnchantmentType {

    PROTECTION(0, "protection", 5),
    FIRE_PROTECTION(1, "protection_fire", 5),
    FALL_PROTECTION(2, "protection_fall", 5),
    EXPLOSION_PROTECTION(3, "protection_explosion", 5),
    PROJECTILE_PROTECTION(4, "protection, projectile", 5),
    THORNS(5, "thorns", 3),
    WATER_BREATHING(6, "water_breathing", 3),
    WATER_WALKER(7, "water_walker", 3),
    WATER_WORKER(8, "water_worker", 3),
    SHARPNESS(9, "sharpness", 5),
    SMITE(10, "smite", 5),
    DAMAGE_ARTHOPODS(11, "bane_of_arthoponds", 5),
    KNOCKBACK(12, "knockback", 2),
    FIRE_ASPECT(13, "fire_aspect", 2),
    LOOTING(14, "looting", 3),
    EFFICIENCY(15, "efficiency", 5),
    SILK_TOUCH(16, "silk_touch", 1),
    DURABILITY(17, "durability", 3),
    FORTUNE(18, "fortune", 3),
    POWER(19, "power", 5),
    PUNCH(20, "punch", 2),
    FLAME(21, "flame", 1),
    INFINITY(22, "infinity", 1),
    LUCK(23, "luck", 3),
    LURE(24, "lure", 3);

    private static final TIntObjectMap<EnchantmentType> BY_ID = new TIntObjectHashMap<>(25);
    private final int id;
    private final String name;
    private final int maxLevel;

    EnchantmentType(int id, String name, int maxLevel) {
        this.id = id;
        this.name = name;
        this.maxLevel = maxLevel;
    }

    public static EnchantmentType byId(int data) {
        EnchantmentType type = BY_ID.get(data);
        if (type == null) {
            throw new IllegalArgumentException("ID " + data + " is not a valid EnchantmentType");
        }
        return type;
    }

    public int id() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}
