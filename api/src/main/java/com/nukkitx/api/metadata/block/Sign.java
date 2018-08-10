package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;

/**
 * @author CreeperFace
 */
public enum Sign implements Metadata {
    SOUTH,
    SOUTH_SOUTHWEST,
    SOUTHWEST,
    WEST_SOUTHWEST,
    WEST,
    WEST_NORTHWEST,
    NORTHWEST,
    NORTH_NORTHWEST,
    NORTH,
    NORTH_NORTHEAST,
    NORTHEAST,
    EAST_NORTHEAST,
    EAST,
    EAST_SOUTHEAST,
    SOUTHEAST,
    SOUTH_SOUTHEAST;

    @Override
    public String toString() {
        return "Sign(" +
                "facing=" + name() +
                ')';
    }
}
