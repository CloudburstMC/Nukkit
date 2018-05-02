package com.nukkitx.server.block.entity;

import com.nukkitx.api.metadata.blockentity.BedBlockEntity;
import com.nukkitx.api.metadata.data.DyeColor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NukkitBedBlockEntity implements BedBlockEntity {
    private DyeColor dyeColor;

    @Override
    public DyeColor getDyeColor() {
        return dyeColor;
    }

    @Override
    public void setDyeColor(DyeColor dyeColor) {
        this.dyeColor = dyeColor;
    }
}
