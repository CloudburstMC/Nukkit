package com.nukkitx.api.metadata.blockentity;

import com.nukkitx.api.metadata.data.DyeColor;

public interface BedBlockEntity extends BlockEntity {

    DyeColor getDyeColor();

    void setDyeColor(DyeColor dyeColor);
}
