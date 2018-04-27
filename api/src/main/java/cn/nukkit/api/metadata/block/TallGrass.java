package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;

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
