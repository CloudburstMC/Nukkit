package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;

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
