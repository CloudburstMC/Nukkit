package com.nukkitx.server.block.entity;

import com.nukkitx.api.metadata.blockentity.BedBlockEntity;
import com.nukkitx.api.metadata.data.DyeColor;

public class NukkitBedBlockEntity extends NukkitBlockEntity implements BedBlockEntity {

    private DyeColor dyeColor;

    public NukkitBedBlockEntity(DyeColor dyeColor) {
        super(BlockEntityType.BED);
        this.dyeColor = dyeColor;
    }

    @Override
    public DyeColor getDyeColor() {
        return dyeColor;
    }

    @Override
    public void setDyeColor(DyeColor dyeColor) {
        this.dyeColor = dyeColor;
    }
}
