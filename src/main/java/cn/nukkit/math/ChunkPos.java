package cn.nukkit.math;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

    public ChunkPos(int x, int z)   {
        this.x = x;
        this.z = z;
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
            ChunkPos other = (ChunkPos) obj;

            return this.x == other.x && this.z == other.z;
        } else {
            return false;
        }
    }
}
