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

package cn.nukkit.api.util;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Objects;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Ray {
    private final Vector3f position;
    private final Vector3f direction;

    public Ray(Vector3f position, Vector3f direction) {
        this.position = position;
        this.direction = direction;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getDirection() {
        return direction;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(position, direction);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Ray that = (Ray) o;
        return this.position.equals(that.position) && this.direction.equals(that.direction);
    }

    @Override
    public String toString() {
        return "Ray{" +
                "position=" + position +
                ", direction=" + direction +
                '}';
    }
}
