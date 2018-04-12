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

package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TopSnow implements Metadata {
    private final int layers;

    public static TopSnow of(int layer) {
        Preconditions.checkArgument(layer >= 0 && layer <= 7, "layers %s is not between 0 and 7", layer);
        return new TopSnow(layer);
    }

    public int getLayers() {
        return layers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopSnow topSnow = (TopSnow) o;
        return layers == topSnow.layers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(layers);
    }

    @Override
    public String toString() {
        return "TopSnow{" +
                "layers=" + layers +
                '}';
    }
}
