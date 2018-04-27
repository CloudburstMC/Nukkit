package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;

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
