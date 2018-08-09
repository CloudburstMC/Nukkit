package com.nukkitx.server.block.entity;

import com.nukkitx.api.metadata.blockentity.NameableBlockEntity;
import lombok.Setter;

import java.util.Optional;

/**
 * @author CreeperFace
 */
public abstract class NukkitNameableBlockEntity extends NukkitBlockEntity implements NameableBlockEntity {

    @Setter
    private String customName;

    @Setter
    private String lock;

    public NukkitNameableBlockEntity(BlockEntityType type) {
        super(type);
    }

    public Optional<String> getCustomName() {
        return Optional.ofNullable(customName);
    }

    public Optional<String> getLock() {
        return Optional.ofNullable(lock);
    }
}
