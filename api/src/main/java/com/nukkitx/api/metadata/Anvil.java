package com.nukkitx.api.metadata;

/**
 * @author CreeperFace
 */
public interface Anvil extends Metadata {

    Damage getDamage();

    public enum Damage {
        NEW,
        SLIGHTLY,
        VERY
    }
}
