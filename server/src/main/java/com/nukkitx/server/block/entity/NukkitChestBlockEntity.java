package com.nukkitx.server.block.entity;

import com.nukkitx.api.metadata.blockentity.ChestBlockEntity;
import com.nukkitx.server.container.NukkitContainer;
import lombok.Getter;

public class NukkitChestBlockEntity extends NukkitNameableBlockEntity implements ChestBlockEntity {

    @Getter
    private final NukkitContainer container;

    public NukkitChestBlockEntity(NukkitContainer container) {
        super(BlockEntityType.CHEST);
        this.container = container;
    }

    @Override
    public boolean isLargeChest() {
        return false;
    }

    @Override
    public boolean isTrapped() {
        return getType() == BlockEntityType.TRAPPED_CHEST;
    }
}
