package cn.nukkit.api.metadata.blockentity;

import cn.nukkit.api.metadata.data.DyeColor;

public interface BedBlockEntity extends BlockEntity {

    DyeColor getDyeColor();

    void setDyeColor(DyeColor dyeColor);
}
