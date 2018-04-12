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

package cn.nukkit.api.metadata;

import cn.nukkit.api.util.data.LogDirection;
import cn.nukkit.api.util.data.TreeSpecies;
import com.google.common.base.Preconditions;

import java.util.Objects;

public class Log extends Wood {
    private final LogDirection direction;

    private Log(TreeSpecies species, LogDirection direction) {
        super(species);
        this.direction = direction;
    }

    public static Log of(TreeSpecies species, LogDirection direction) {
        Preconditions.checkNotNull(species, "species");
        Preconditions.checkNotNull(direction, "direction");
        return new Log(species, direction);
    }

    public LogDirection getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Log log = (Log) o;
        return direction == log.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), direction);
    }

    @Override
    public String toString() {
        return "Log{" +
                "species=" + getSpecies() + ',' +
                "direction=" + direction +
                '}';
    }
}
