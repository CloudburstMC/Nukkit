package com.nukkitx.server.block.entity;

import com.nukkitx.api.level.Level;
import com.nukkitx.api.metadata.blockentity.BlockEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author CreeperFace
 */

@AllArgsConstructor
@Getter
public abstract class NukkitBlockEntity implements BlockEntity {

    private final BlockEntityType type;

    @Override
    public Level getLevel() {
        return null;
    }
}
