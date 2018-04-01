package cn.nukkit.api.block.entity;

import cn.nukkit.api.util.data.DyeColor;

public interface BedBlockEntity extends BlockEntity {

    DyeColor getDyeColor();

    void setDyeColor(DyeColor dyeColor);
}
