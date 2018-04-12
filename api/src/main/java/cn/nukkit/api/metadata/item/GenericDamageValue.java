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

package cn.nukkit.api.metadata.item;

import cn.nukkit.api.metadata.Metadata;

import java.util.Objects;

public final class GenericDamageValue implements Metadata {
    public static final GenericDamageValue ZERO = new GenericDamageValue((short) 0);
    private final short damage;

    public GenericDamageValue(short damage) {
        this.damage = damage;
    }

    public final short getDamage() {
        return damage;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(damage);
    }

    @Override
    public final String toString() {
        return "GenericDamageValue{" +
                "damage=" + damage +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericDamageValue damageValue = (GenericDamageValue) o;
        return damage == damageValue.damage;
    }
}
