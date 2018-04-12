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
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;

import javax.annotation.concurrent.Immutable;

/**
 * This class represents a rotation. Bedrock Edition uses degrees to measure angles. This class is immutable.
 */
@Immutable
public final class Rotation {
    public static final Rotation ZERO = new Rotation(0f, 0f, 0f);

    private transient volatile boolean hashed;
    private transient volatile int hashCode;
    private final float pitch;
    private final float yaw;
    private final float headYaw;

    public Rotation(Vector2f rotation) {
        this(rotation.getX(), rotation.getY(), 0);
    }

    public Rotation(float pitch, float yaw) {
        this(pitch, yaw, 0);
    }

    public Rotation(Vector2f rotation, float headYaw) {
        this(rotation.getX(), rotation.getY(), headYaw);
    }

    public Rotation(float pitch, float yaw, float headYaw) {
        hashed = false;
        hashCode = 0;
        this.pitch = validate(pitch, "pitch");
        this.yaw = validate(yaw, "yaw");
        this.headYaw = validate(headYaw, "headYaw");
    }

    /**
     * Copies the value from a specified {@link Vector3f} instance into a {@link Rotation} instance.
     * @param vector3f the vector to use
     * @return the Rotation instance
     */
    public static Rotation fromVector3f(Vector3f vector3f) {
        Preconditions.checkNotNull(vector3f, "vector3f");
        return new Rotation(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    private static float validate(float val, String name) {
        Preconditions.checkArgument(Float.isFinite(val), "%s value (%s) is not finite", name, val);
        return val;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getHeadYaw() {
        return headYaw;
    }

    public Vector2f getBodyRotation() {
        return new Vector2f(pitch, yaw);
    }

    /**
     * Returns the values contained in this object as a {@link Vector3f}. The X value will be the pitch, the Y value
     * is the yaw, and the Z value will be the head yaw.
     * @return a {@link Vector3f} instance
     */
    public Vector3f toVector3f() {
        return new Vector3f(pitch, yaw, headYaw);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rotation rotation = (Rotation) o;

        return Float.compare(rotation.pitch, pitch) == 0 &&
                Float.compare(rotation.yaw, yaw) == 0 &&
                Float.compare(rotation.headYaw, headYaw) == 0;
    }

    @Override
    public int hashCode() {
        if (!hashed) {
            int result = pitch != 0.0F ? HashFunctions.hash(pitch) : 0;
            result = 31 * result + (yaw != 0.0F ? HashFunctions.hash(yaw) : 0);
            this.hashCode = 31 * result + (headYaw != 0.0F ? HashFunctions.hash(headYaw) : 0);
            this.hashed = true;
        }

        return hashCode;
    }

    @Override
    public String toString() {
        return "Rotation{" +
                "pitch=" + pitch +
                ", yaw=" + yaw +
                ", headYaw=" + headYaw +
                '}';
    }
}
