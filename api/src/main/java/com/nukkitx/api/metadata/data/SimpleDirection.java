package com.nukkitx.api.metadata.data;

import com.nukkitx.api.util.data.BlockFace;

import javax.annotation.Nonnull;

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

    public static SimpleDirection fromBlockFace(@Nonnull BlockFace face) {
        int index = face.ordinal() - 2;

        if (index < 0 || index > values().length) {
            return null;
        }

        return values()[index];
    }
}
