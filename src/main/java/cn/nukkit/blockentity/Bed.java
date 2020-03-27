package cn.nukkit.blockentity;

import cn.nukkit.utils.DyeColor;

public interface Bed extends BlockEntity {

    DyeColor getColor();

    void setColor(DyeColor color);
}
