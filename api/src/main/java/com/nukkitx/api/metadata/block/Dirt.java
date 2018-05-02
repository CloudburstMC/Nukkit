package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;

public enum Dirt implements Metadata {
    REGULAR,
    COARSE_DIRT,
    PODZOL;

    @Override
    public String toString() {
        return "Dirt(" +
                "type" + name() +
                ')';
    }
}
