package cn.nukkit.math;

import lombok.ToString;

/**
 * An immutable representation of a chunk position.
 *
 * @author DaPorkchop_
 */
@ToString
public final class ChunkPos {
    public static final ChunkPos ZERO = new ChunkPos(0, 0);

    public final int x;
    public final int z;

    public ChunkPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ChunkPos add(int i) {
        return new ChunkPos(this.x + i, this.z + i);
    }

    public ChunkPos add(int x, int z) {
        return new ChunkPos(this.x + x, this.z + z);
    }

    public ChunkPos sub(int i) {
        return new ChunkPos(this.x - i, this.z - i);
    }

    public ChunkPos sub(int x, int z) {
        return new ChunkPos(this.x - x, this.z - z);
    }

    public ChunkPos mul(int i) {
        return new ChunkPos(this.x * i, this.z * i);
    }

    public ChunkPos mul(int x, int z) {
        return new ChunkPos(this.x * x, this.z * z);
    }

    public ChunkPos div(int i) {
        return new ChunkPos(this.x / i, this.z / i);
    }

    public ChunkPos div(int x, int z) {
        return new ChunkPos(this.x / x, this.z / z);
    }

    public double dist(ChunkPos pos) {
        return this.dist(pos.x, pos.z);
    }

    public double dist(int x, int z) {
        return Math.sqrt(this.distSq(x, z));
    }

    public double distSq(ChunkPos pos) {
        return this.distSq(pos.x, pos.z);
    }

    public double distSq(int x, int z) {
        double deltaX = this.x - x;
        double deltaZ = this.z - z;
        return deltaX * deltaX + deltaZ * deltaZ;
    }

    public Vector3i toBlockCoordinates() {
        return new Vector3i(this.x << 4, 0, this.z << 4);
    }

    @Override
    public int hashCode() {
        //constants from https://baritone.leijurv.com/src-html/baritone/api/utils/BetterBlockPos.html#line.082
        return (3241 * 3457689 + this.x) * 8734625 + this.z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ChunkPos) {
            return this.equals((ChunkPos) obj);
        } else {
            return false;
        }
    }

    public boolean equals(ChunkPos pos) {
        return pos == this || (this.x == pos.x && this.z == pos.z);
    }

    public boolean equals(int x, int z) {
        return this.x == x && this.z == z;
    }
}
