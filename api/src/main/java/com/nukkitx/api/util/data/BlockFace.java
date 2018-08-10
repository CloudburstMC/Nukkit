package com.nukkitx.api.util.data;

import com.flowpowered.math.vector.Vector3i;
import lombok.Getter;

public enum BlockFace {
    BOTTOM(1, new Vector3i(0, -1, 0)),
    TOP(0, new Vector3i(0, 1, 0)),
    NORTH(3, new Vector3i(0, 0, -1)),
    SOUTH(2, new Vector3i(0, 0, 1)),
    EAST(5, new Vector3i(-1, 0, 0)),
    WEST(4, new Vector3i(1, 0, 0));

    @Getter
    private final Vector3i offset;

    private final int opposite;

    BlockFace(int opposite, Vector3i offset) {
        this.offset = offset;
        this.opposite = opposite;
    }

    public BlockFace getOpposite() {
        return values()[this.opposite];
    }

    public static BlockFace getFace(int face) {
        if (face >= 0 && face < 6) {
            return values()[face];
        }
        return BOTTOM;
    }
}
