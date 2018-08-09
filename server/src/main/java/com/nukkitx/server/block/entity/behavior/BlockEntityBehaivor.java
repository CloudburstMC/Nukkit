package com.nukkitx.server.block.entity.behavior;

/**
 * @author CreeperFace
 */
public interface BlockEntityBehaivor {

    default boolean tick() {
        return false;
    }
}
