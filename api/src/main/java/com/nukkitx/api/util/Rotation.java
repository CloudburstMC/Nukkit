package com.nukkitx.api.util;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * This class represents a rotation. Bedrock Edition uses degrees to measure angles. This class is immutable.
 */
@Immutable
public final class Rotation {
    public static final Rotation ZERO = new Rotation(Vector3f.ZERO);

    private final Vector3f rotation;

    private Rotation(Vector3f rotation) {
        Preconditions.checkNotNull(rotation, "rotation");
        validate(rotation.getX(), "pitch");
        validate(rotation.getX(), "yaw");
        validate(rotation.getX(), "roll");
        this.rotation = rotation;
    }

    /**
     * Copies the value from a specified {@link Vector3f} instance into a {@link Rotation} instance.
     * @param rotation the vector to use
     * @return the Rotation instance
     */
    public static Rotation from(Vector3f rotation) {
        Preconditions.checkNotNull(rotation, "rotation");
        if (rotation == Vector3f.ZERO) {
            return ZERO;
        }
        return new Rotation(rotation);
    }

    public static Rotation from(float pitch, float yaw, float roll) {
        if (pitch == 0f && yaw == 0f && roll == 0f) {
            return ZERO;
        }
        return from(new Vector3f(pitch, yaw, roll));
    }

    public static Rotation from(float pitch, float yaw) {
        return from(pitch, yaw, 0f);
    }

    private static void validate(float val, String name) {
        Preconditions.checkArgument(Float.isFinite(val), "%s value (%s) is not finite", name, val);
    }

    public float getPitch() {
        return rotation.getX();
    }

    public float getYaw() {
        return rotation.getY();
    }

    public float getHeadYaw() {
        return rotation.getZ();
    }

    public float getRoll() {
        return rotation.getZ();
    }

    public Vector2f toVector2f() {
        return rotation.toVector2();
    }

    /**
     * Returns the values contained in this object as a {@link Vector3f}. The X value will be the pitch, the Y value
     * is the yaw, and the Z value will be the head yaw.
     * @return a {@link Vector3f} instance
     */
    public Vector3f toVector3f() {
        return rotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rotation that = (Rotation) o;

        return Objects.equals(this.rotation, that.rotation);
    }

    @Override
    public int hashCode() {
        return rotation.hashCode();
    }

    @Override
    public String toString() {
        return "Rotation(" +
                "pitch=" + rotation.getX() +
                ", yaw=" + rotation.getY() +
                ", roll=" + rotation.getZ() +
                ')';
    }
}
