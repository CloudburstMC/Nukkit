package com.nukkitx.api.util;

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
    private final float roll; // headYaw

    public Rotation(Vector2f rotation) {
        this(rotation.getX(), rotation.getY(), 0);
    }

    public Rotation(float pitch, float yaw) {
        this(pitch, yaw, 0);
    }

    public Rotation(Vector2f rotation, float roll) {
        this(rotation.getX(), rotation.getY(), roll);
    }

    public Rotation(float pitch, float yaw, float roll) {
        hashed = false;
        hashCode = 0;
        this.pitch = validate(pitch, "pitch");
        this.yaw = validate(yaw, "yaw");
        this.roll = validate(roll, "roll");
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

    public static Rotation from(float pitch, float headYaw, float yaw) {
        if (pitch == 0f && headYaw == 0f && yaw == 0f) {
            return ZERO;
        }
        return new Rotation(pitch, headYaw, yaw);
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
        return roll;
    }

    public float getRoll() {
        return roll;
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
        return new Vector3f(pitch, yaw, roll);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rotation rotation = (Rotation) o;

        return Float.compare(rotation.pitch, pitch) == 0 &&
                Float.compare(rotation.yaw, yaw) == 0 &&
                Float.compare(rotation.roll, roll) == 0;
    }

    @Override
    public int hashCode() {
        if (!hashed) {
            int result = pitch != 0.0F ? HashFunctions.hash(pitch) : 0;
            result = 31 * result + (yaw != 0.0F ? HashFunctions.hash(yaw) : 0);
            this.hashCode = 31 * result + (roll != 0.0F ? HashFunctions.hash(roll) : 0);
            this.hashed = true;
        }

        return hashCode;
    }

    @Override
    public String toString() {
        return "Rotation{" +
                "pitch=" + pitch +
                ", yaw=" + yaw +
                ", roll=" + roll +
                '}';
    }
}
