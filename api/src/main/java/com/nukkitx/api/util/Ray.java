package com.nukkitx.api.util;

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
                "blockPosition=" + position +
                ", direction=" + direction +
                '}';
    }
}
