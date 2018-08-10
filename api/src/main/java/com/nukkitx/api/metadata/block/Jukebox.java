package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;

/**
 * @author CreeperFace
 */
public enum Jukebox implements Metadata {
    EMPTY,
    DISC;

    public boolean containsDisc() {
        return this == DISC;
    }

    @Override
    public String toString() {
        return "Jukebox(" +
                "contains disc=" + containsDisc() +
                ')';
    }
}
