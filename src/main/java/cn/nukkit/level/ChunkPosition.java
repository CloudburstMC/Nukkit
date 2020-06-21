package cn.nukkit.level;

import cn.nukkit.math.MathHelper;
import cn.nukkit.math.Vector3;

/**
 * Author: Adam Matthew
 * <p>
 * Nukkit Project
 */
public class ChunkPosition {

    public final int x;

    public final int y;

    public final int z;

    public ChunkPosition(final int i, final int j, final int k) {
        this.x = i;
        this.y = j;
        this.z = k;
    }

    public ChunkPosition(final Vector3 vec3d) {
        this(MathHelper.floor(vec3d.x), MathHelper.floor(vec3d.y), MathHelper.floor(vec3d.z));
    }

    @Override
    public int hashCode() {
        return this.x * 8976890 + this.y * 981131 + this.z;
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof ChunkPosition)) {
            return false;
        } else {
            final ChunkPosition chunkposition = (ChunkPosition) object;

            return chunkposition.x == this.x && chunkposition.y == this.y && chunkposition.z == this.z;
        }
    }

}
