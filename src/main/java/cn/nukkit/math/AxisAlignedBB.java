package cn.nukkit.math;

import cn.nukkit.level.MovingObjectPosition;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

public interface AxisAlignedBB extends Cloneable {

    default AxisAlignedBB setBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.setMinX(minX);
        this.setMinY(minY);
        this.setMinZ(minZ);
        this.setMaxX(maxX);
        this.setMaxY(maxY);
        this.setMaxZ(maxZ);
        return this;
    }

    default AxisAlignedBB addCoord(Vector3f v) {
        return addCoord(v.getX(), v.getY(), v.getZ());
    }

    default AxisAlignedBB addCoord(float x, float y, float z) {
        float minX = this.getMinX();
        float minY = this.getMinY();
        float minZ = this.getMinZ();
        float maxX = this.getMaxX();
        float maxY = this.getMaxY();
        float maxZ = this.getMaxZ();

        if (x < 0) minX += x;
        if (x > 0) maxX += x;

        if (y < 0) minY += y;
        if (y > 0) maxY += y;

        if (z < 0) minZ += z;
        if (z > 0) maxZ += z;

        return new SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    default AxisAlignedBB grow(float x, float y, float z) {
        return new SimpleAxisAlignedBB(this.getMinX() - x, this.getMinY() - y, this.getMinZ() - z, this.getMaxX() + x, this.getMaxY() + y, this.getMaxZ() + z);
    }

    default AxisAlignedBB expand(float x, float y, float z) {
        this.setMinX(this.getMinX() - x);
        this.setMinY(this.getMinY() - y);
        this.setMinZ(this.getMinZ() - z);
        this.setMaxX(this.getMaxX() + x);
        this.setMaxY(this.getMaxY() + y);
        this.setMaxZ(this.getMaxZ() + z);

        return this;
    }

    default AxisAlignedBB offset(Vector3f v) {
        return offset(v.getX(), v.getY(), v.getZ());
    }

    default AxisAlignedBB offset(float x, float y, float z) {
        this.setMinX(this.getMinX() + x);
        this.setMinY(this.getMinY() + y);
        this.setMinZ(this.getMinZ() + z);
        this.setMaxX(this.getMaxX() + x);
        this.setMaxY(this.getMaxY() + y);
        this.setMaxZ(this.getMaxZ() + z);

        return this;
    }

    default AxisAlignedBB shrink(float x, float y, float z) {
        return new SimpleAxisAlignedBB(this.getMinX() + x, this.getMinY() + y, this.getMinZ() + z, this.getMaxX() - x, this.getMaxY() - y, this.getMaxZ() - z);
    }

    default AxisAlignedBB contract(float x, float y, float z) {
        this.setMinX(this.getMinX() + x);
        this.setMinY(this.getMinY() + y);
        this.setMinZ(this.getMinZ() + z);
        this.setMaxX(this.getMaxX() - x);
        this.setMaxY(this.getMaxY() - y);
        this.setMaxZ(this.getMaxZ() - z);

        return this;
    }

    default AxisAlignedBB setBB(AxisAlignedBB bb) {
        this.setMinX(bb.getMinX());
        this.setMinY(bb.getMinY());
        this.setMinZ(bb.getMinZ());
        this.setMaxX(bb.getMaxX());
        this.setMaxY(bb.getMaxY());
        this.setMaxZ(bb.getMaxZ());
        return this;
    }

    default AxisAlignedBB getOffsetBoundingBox(Vector3f v) {
        return this.getOffsetBoundingBox(v.getX(), v.getY(), v.getZ());
    }

    default AxisAlignedBB getOffsetBoundingBox(float x, float y, float z) {
        return new SimpleAxisAlignedBB(this.getMinX() + x, this.getMinY() + y, this.getMinZ() + z, this.getMaxX() + x, this.getMaxY() + y, this.getMaxZ() + z);
    }

    default float calculateXOffset(AxisAlignedBB bb, float x) {
        if (bb.getMaxY() <= this.getMinY() || bb.getMinY() >= this.getMaxY()) {
            return x;
        }
        if (bb.getMaxZ() <= this.getMinZ() || bb.getMinZ() >= this.getMaxZ()) {
            return x;
        }
        if (x > 0 && bb.getMaxX() <= this.getMinX()) {
            float x1 = this.getMinX() - bb.getMaxX();
            if (x1 < x) {
                x = x1;
            }
        }
        if (x < 0 && bb.getMinX() >= this.getMaxX()) {
            float x2 = this.getMaxX() - bb.getMinX();
            if (x2 > x) {
                x = x2;
            }
        }

        return x;
    }

    default float calculateYOffset(AxisAlignedBB bb, float y) {
        if (bb.getMaxX() <= this.getMinX() || bb.getMinX() >= this.getMaxX()) {
            return y;
        }
        if (bb.getMaxZ() <= this.getMinZ() || bb.getMinZ() >= this.getMaxZ()) {
            return y;
        }
        if (y > 0 && bb.getMaxY() <= this.getMinY()) {
            float y1 = this.getMinY() - bb.getMaxY();
            if (y1 < y) {
                y = y1;
            }
        }
        if (y < 0 && bb.getMinY() >= this.getMaxY()) {
            float y2 = this.getMaxY() - bb.getMinY();
            if (y2 > y) {
                y = y2;
            }
        }

        return y;
    }

    default float calculateZOffset(AxisAlignedBB bb, float z) {
        if (bb.getMaxX() <= this.getMinX() || bb.getMinX() >= this.getMaxX()) {
            return z;
        }
        if (bb.getMaxY() <= this.getMinY() || bb.getMinY() >= this.getMaxY()) {
            return z;
        }
        if (z > 0 && bb.getMaxZ() <= this.getMinZ()) {
            float z1 = this.getMinZ() - bb.getMaxZ();
            if (z1 < z) {
                z = z1;
            }
        }
        if (z < 0 && bb.getMinZ() >= this.getMaxZ()) {
            float z2 = this.getMaxZ() - bb.getMinZ();
            if (z2 > z) {
                z = z2;
            }
        }

        return z;
    }

    default boolean intersectsWith(AxisAlignedBB bb) {
        if (bb.getMaxY() > this.getMinY() && bb.getMinY() < this.getMaxY()) {
            if (bb.getMaxX() > this.getMinX() && bb.getMinX() < this.getMaxX()) {
                return bb.getMaxZ() > this.getMinZ() && bb.getMinZ() < this.getMaxZ();
            }
        }

        return false;
    }

    default boolean isVectorInside(Vector3f vector) {
        return vector.getX() >= this.getMinX() && vector.getX() <= this.getMaxX() && vector.getY() >= this.getMinY() && vector.getY() <= this.getMaxY() && vector.getZ() >= this.getMinZ() && vector.getZ() <= this.getMaxZ();

    }

    default float getAverageEdgeLength() {
        return (this.getMaxX() - this.getMinX() + this.getMaxY() - this.getMinY() + this.getMaxZ() - this.getMinZ()) / 3;
    }

    default boolean isVectorInYZ(Vector3f vector) {
        return vector.getY() >= this.getMinY() && vector.getY() <= this.getMaxY() && vector.getZ() >= this.getMinZ() && vector.getZ() <= this.getMaxZ();
    }

    default boolean isVectorInXZ(Vector3f vector) {
        return vector.getX() >= this.getMinX() && vector.getX() <= this.getMaxX() && vector.getZ() >= this.getMinZ() && vector.getZ() <= this.getMaxZ();
    }

    default boolean isVectorInXY(Vector3f vector) {
        return vector.getX() >= this.getMinX() && vector.getX() <= this.getMaxX() && vector.getY() >= this.getMinY() && vector.getY() <= this.getMaxY();
    }

    default MovingObjectPosition calculateIntercept(Vector3f pos1, Vector3f pos2) {
        Vector3f v1 = NukkitMath.getIntermediateWithXValue(pos1, pos2, this.getMinX());
        Vector3f v2 = NukkitMath.getIntermediateWithXValue(pos1, pos2, this.getMaxX());
        Vector3f v3 = NukkitMath.getIntermediateWithYValue(pos1, pos2, this.getMinY());
        Vector3f v4 = NukkitMath.getIntermediateWithYValue(pos1, pos2, this.getMaxY());
        Vector3f v5 = NukkitMath.getIntermediateWithZValue(pos1, pos2, this.getMinZ());
        Vector3f v6 = NukkitMath.getIntermediateWithZValue(pos1, pos2, this.getMaxZ());

        if (v1 != null && !this.isVectorInYZ(v1)) {
            v1 = null;
        }

        if (v2 != null && !this.isVectorInYZ(v2)) {
            v2 = null;
        }

        if (v3 != null && !this.isVectorInXZ(v3)) {
            v3 = null;
        }

        if (v4 != null && !this.isVectorInXZ(v4)) {
            v4 = null;
        }

        if (v5 != null && !this.isVectorInXY(v5)) {
            v5 = null;
        }

        if (v6 != null && !this.isVectorInXY(v6)) {
            v6 = null;
        }

        Vector3f vector = null;

        //if (v1 != null && (vector == null || pos1.distanceSquared(v1) < pos1.distanceSquared(vector))) {
        if (v1 != null) {
            vector = v1;
        }

        if (v2 != null && (vector == null || pos1.distanceSquared(v2) < pos1.distanceSquared(vector))) {
            vector = v2;
        }

        if (v3 != null && (vector == null || pos1.distanceSquared(v3) < pos1.distanceSquared(vector))) {
            vector = v3;
        }

        if (v4 != null && (vector == null || pos1.distanceSquared(v4) < pos1.distanceSquared(vector))) {
            vector = v4;
        }

        if (v5 != null && (vector == null || pos1.distanceSquared(v5) < pos1.distanceSquared(vector))) {
            vector = v5;
        }

        if (v6 != null && (vector == null || pos1.distanceSquared(v6) < pos1.distanceSquared(vector))) {
            vector = v6;
        }

        if (vector == null) {
            return null;
        }

        int face = -1;

        if (vector == v1) {
            face = 4;
        } else if (vector == v2) {
            face = 5;
        } else if (vector == v3) {
            face = 0;
        } else if (vector == v4) {
            face = 1;
        } else if (vector == v5) {
            face = 2;
        } else if (vector == v6) {
            face = 3;
        }

        return MovingObjectPosition.fromBlock(Vector3i.ZERO, face, vector);
    }

    float getMinX();

    default void setMinX(float minX) {
        throw new UnsupportedOperationException("Not mutable");
    }

    float getMinY();

    default void setMinY(float minY) {
        throw new UnsupportedOperationException("Not mutable");
    }

    float getMinZ();

    default void setMinZ(float minZ) {
        throw new UnsupportedOperationException("Not mutable");
    }

    float getMaxX();

    default void setMaxX(float maxX) {
        throw new UnsupportedOperationException("Not mutable");
    }

    float getMaxY();

    default void setMaxY(float maxY) {
        throw new UnsupportedOperationException("Not mutable");
    }

    float getMaxZ();

    default void setMaxZ(float maxZ) {
        throw new UnsupportedOperationException("Not mutable");
    }

    AxisAlignedBB clone();

    default void forEach(BBConsumer action) {
        int minX = NukkitMath.floorDouble(this.getMinX());
        int minY = NukkitMath.floorDouble(this.getMinY());
        int minZ = NukkitMath.floorDouble(this.getMinZ());

        int maxX = NukkitMath.floorDouble(this.getMaxX());
        int maxY = NukkitMath.floorDouble(this.getMaxY());
        int maxZ = NukkitMath.floorDouble(this.getMaxZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    action.accept(x, y, z);
                }
            }
        }
    }


    interface BBConsumer<T> {

        void accept(int x, int y, int z);

        default T get() {
            return null;
        }
    }
}
