package cn.nukkit.api.util;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.Objects;

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
            this.hashCode = Objects.hash(min, max);
            this.hashed = true;
        }

        return hashCode;
    }

    @Override
    public String toString() {
        return "BoundingBox(" +
                "min=" + min +
                ", max=" + max +
                ')';
    }
}
