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

package cn.nukkit.server.potion;

import cn.nukkit.api.util.Color;
import cn.nukkit.api.util.data.Effect;

public enum NukkitEffect {
    SPEED("potion.moveSpeed", Color.of(124, 175, 198), false),
    SLOWNESS("potion.moveSlowdown", Color.of(90, 108, 129), true),
    HASTE("potion.digSpeed", Color.of(217, 192, 67), false),
    MINING_FATIGUE("potion.digSlowDown", Color.of(74, 66, 23), true),
    STRENGTH("potion.damageBoost", Color.of(146, 36, 35), false),
    HEALING("potion.heal", Color.of(248, 36, 35), false),
    HARMING("potion.harm", Color.of(67, 10, 9), true),
    JUMP("potion.jump", Color.of(34, 255, 76), false),
    NAUSEA("potion.confusion", Color.of(85, 29, 74), true),
    REGENERATION("potion.moveSlowdown", Color.of(90, 108, 129), false),
    DAMAGE_RESISTANCE("potion.moveSlowdown", Color.of(90, 108, 129), false),
    FIRE_RESISTANCE("potion.moveSlowdown", Color.of(90, 108, 129), false),
    WATER_BREATHING("potion.moveSlowdown", Color.of(90, 108, 129), false),
    INVISIBILITY("potion.moveSlowdown", Color.of(90, 108, 129), false),
    BLINDNESS("potion.moveSlowdown", Color.of(90, 108, 129), true),
    NIGHT_VISION("potion.moveSlowdown", Color.of(90, 108, 129), false),
    HUNGER("potion.moveSlowdown", Color.of(90, 108, 129), true),
    WEAKNESS("potion.moveSlowdown", Color.of(90, 108, 129), true),
    POISON("potion.moveSlowdown", Color.of(90, 108, 129), true),
    WITHER("potion.moveSlowdown", Color.of(90, 108, 129), true),
    HEALTH_BOOST("potion.moveSlowdown", Color.of(90, 108, 129), false),
    ABSORPTION("potion.moveSlowdown", Color.of(90, 108, 129), false),
    SATURATION("potion.moveSlowdown", Color.of(90, 108, 129), false);

    private static final NukkitEffect[] VALUES = values();

    private final String i18n;
    private final Color color;
    private final boolean unhealthy;

    NukkitEffect(String i18n, Color color, boolean unhealthy) {
        this.i18n = i18n;
        this.color = color;
        this.unhealthy = unhealthy;
    }

    public boolean isUnhealthy() {
        return unhealthy;
    }

    public Color getColor() {
        return color;
    }

    public String getI18n() {
        return i18n;
    }

    public static NukkitEffect fromApi(Effect effect) {
        return VALUES[effect.ordinal()];
    }
}
