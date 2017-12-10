package cn.nukkit.api.util.data;

import com.flowpowered.math.vector.Vector3i;

public enum BlockFace {
    BOTTOM(new Vector3i(0, -1, 0)),
    TOP(new Vector3i(0, 1, 0)),
    NORTH(new Vector3i(0, 0, -1)),
    SOUTH(new Vector3i(0, 0, 1)),
    EAST(new Vector3i(-1, 0, 0)),
    WEST(new Vector3i(1, 0, 0));

    private Vector3i offset;

    BlockFace(Vector3i offset) {
        this.offset = offset;
    }

    public Vector3i getOffset() {
        return offset;
    }
}
