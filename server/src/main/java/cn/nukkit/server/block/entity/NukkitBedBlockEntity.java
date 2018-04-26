package cn.nukkit.server.block.entity;

import cn.nukkit.api.metadata.blockentity.BedBlockEntity;
import cn.nukkit.api.metadata.data.DyeColor;
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
