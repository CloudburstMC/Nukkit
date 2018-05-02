package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;

public enum Stone implements Metadata {
    REGULAR,
    GRANITE,
    POLISHED_GRANITE,
    DIORITE,
    POLISHED_DIORITE,
    ANDESITE,
    POLISHED_ANDESITE;

    @Override
    public String toString() {
        return "Stone(" +
                "type=" + name() +
                ')';
    }
}
