package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;

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
