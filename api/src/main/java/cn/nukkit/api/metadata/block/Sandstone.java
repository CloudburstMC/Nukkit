package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;

public enum Sandstone implements Metadata {
    REGULAR,
    CHISELED,
    SMOOTH;

    @Override
    public String toString() {
        return "Sandstone(" +
                "type=" + name() +
                ')';
    }
}
