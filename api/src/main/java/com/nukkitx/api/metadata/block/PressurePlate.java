package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;

/**
 * @author CreeperFace
 */
public enum PressurePlate implements Metadata {
    INACTIVE,
    ACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    @Override
    public String toString() {
        return "PressurePlate(" +
                "isActive=" + isActive() +
                ')';
    }
}
