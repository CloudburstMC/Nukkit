package com.nukkitx.server.block.entity;

import com.nukkitx.api.metadata.blockentity.ChestBlockEntity;
import com.nukkitx.server.inventory.NukkitInventory;
import lombok.Getter;

/**
 * @author CreeperFace
 */
public class NukkitChestBlockEntity extends NukkitNameableBlockEntity implements ChestBlockEntity {

    @Getter
    private final NukkitInventory inventory;


    public NukkitChestBlockEntity(NukkitInventory inventory) {
        this(inventory, BlockEntityType.CHEST);
    }

    public NukkitChestBlockEntity(NukkitInventory inventory, BlockEntityType type) {
        super(type);
        this.inventory = inventory;
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
