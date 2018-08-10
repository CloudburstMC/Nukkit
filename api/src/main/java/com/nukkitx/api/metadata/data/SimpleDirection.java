package com.nukkitx.api.metadata.data;

import com.nukkitx.api.util.data.BlockFace;

public enum SimpleDirection {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    public BlockFace toBlockFace() {
        return BlockFace.getFace(ordinal() + 2);
    }

    public static SimpleDirection fromIndex(int index) {
        return values()[index];
    }
}
