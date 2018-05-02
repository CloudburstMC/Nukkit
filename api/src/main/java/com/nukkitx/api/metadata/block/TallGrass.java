package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;

public enum TallGrass implements Metadata {
    SHRUB,
    TALL_GRASS,
    FERN;

    @Override
    public String toString() {
        return "TallGrass(" +
                "type=" + name() +
                ')';
    }
}
