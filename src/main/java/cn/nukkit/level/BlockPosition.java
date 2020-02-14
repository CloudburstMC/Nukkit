package cn.nukkit.level;

import com.nukkitx.math.vector.Vector3i;

import static com.google.common.base.Preconditions.checkNotNull;

public class BlockPosition {
    private final Vector3i position;
    private final int layer;

    private BlockPosition(Vector3i position, int layer) {
        this.position = position;
        this.layer = layer;
    }

    public static BlockPosition from(Vector3i vector3i) {
        return from(vector3i, 0);
    }

    public static BlockPosition from(Vector3i vector3i, int layer) {
        checkNotNull(vector3i, "vector3i");
        return new BlockPosition(vector3i, layer);
    }

    public Vector3i getPosition() {
        return position;
    }

    public int getLayer() {
        return layer;
    }
}
