package com.nukkitx.api.metadata.item;

import com.nukkitx.api.metadata.Metadata;

/**
 * @author CreeperFace
 */
public enum Bucket implements Metadata {
    EMPTY,
    MILK,
    COD,
    SALMON,
    TROPICAL_FISH,
    PUFFERFISH,
    WATER,
    LAVA;

    @Override
    public final String toString() {
        return "Bucket(" +
                "content=" + name() +
                ')';
    }
}
