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

import com.flowpowered.math.HashFunctions;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Immutable
public class BoundingBox {
    private final Vector3f min;
    private final Vector3f max;
    @NonFinal private transient volatile boolean hashed;
    @NonFinal private transient volatile int hashCode;

    public BoundingBox(Vector3f min, Vector3f max) {
        Preconditions.checkNotNull(min, "min");
        Preconditions.checkNotNull(max, "max");
        this.min = min.min(max);
        this.max = max.max(min);
        hashed = false;
        hashCode = 0;
    }

    public boolean isWithin(Vector3i vector) {
        Preconditions.checkNotNull(vector, "vector");
        return isWithin(vector.toFloat());
    }

    public boolean isWithin(Vector3f vector) {
        Preconditions.checkNotNull(vector, "vector");
        return Float.compare(vector.getX(), min.getX()) >= 0 &&
                Float.compare(vector.getX(), max.getX()) <= 0 &&
                Float.compare(vector.getY(), min.getY()) >= 0 &&
                Float.compare(vector.getY(), max.getY()) <= 0 &&
                Float.compare(vector.getZ(), min.getZ()) >= 0 &&
                Float.compare(vector.getZ(), max.getZ()) <= 0;
    }

    public boolean intersectsWith(BoundingBox bb) {
        Preconditions.checkNotNull(bb, "boundingBox");
        return Float.compare(bb.min.getX(), max.getX()) <= 0 &&
                Float.compare(bb.max.getX(), min.getX()) >= 0 &&
                Float.compare(bb.min.getY(), max.getY()) <= 0 &&
                Float.compare(bb.max.getY(), min.getY()) >= 0 &&
                Float.compare(bb.min.getZ(), max.getZ()) <= 0 &&
                Float.compare(bb.max.getZ(), min.getZ()) >= 0;
    }

    public BoundingBox grow(float x, float y, float z) {
        return new BoundingBox(min.sub(x, y, z), max.add(x, y, z));
    }

    public BoundingBox grow(float val) {
        return grow(val, val, val);
    }

    public BoundingBox offset(float x, float y, float z) {
        return new BoundingBox(min.add(x, y, z), max.add(x, y, z));
    }

    public BoundingBox shrink(float x, float y, float z) {
        return new BoundingBox(min.add(x, y, z), max.sub(x, y, z));
    }

    public BoundingBox shrink(float val) {
        return shrink(val, val, val);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoundingBox that = (BoundingBox) o;
        return this.min.equals(that.min) && this.max.equals(that.max);
    }

    @Override
    public int hashCode() {
        if (!hashed) {
            int result = HashFunctions.hash(min);
            this.hashCode = 31 * result + HashFunctions.hash(max);
            this.hashed = true;
        }

        return hashCode;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "min=" + min +
                ", max=" + max +
                "}";
    }
}
