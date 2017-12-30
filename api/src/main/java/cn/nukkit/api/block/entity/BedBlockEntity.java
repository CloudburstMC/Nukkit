package cn.nukkit.api.block.entity;

import cn.nukkit.api.util.data.DyeColor;

public interface BedBlockEntity {

    DyeColor getDyeColor();

    void setDyeColor(DyeColor dyeColor);
}
