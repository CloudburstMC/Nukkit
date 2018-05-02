package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;

public enum Flower implements Metadata {
    ROSE,
    BLUE_ORCHID,
    ALLIUM,
    HOUSTONIA,
    RED_TULIP,
    ORANGE_TULIP,
    WHITE_TULIP,
    PINK_TULIP,
    OXEYE_DAISY;

    @Override
    public String toString() {
        return "Flower(" +
                "type=" + name() +
                ')';
    }
}
