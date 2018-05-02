package com.nukkitx.server.block.entity;

import com.nukkitx.api.metadata.blockentity.FlowerPotBlockEntity;
import com.nukkitx.api.metadata.data.PlantType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NukkitFlowerPotBlockEntity implements FlowerPotBlockEntity {
    private PlantType type;

    @Override
    public PlantType getPlantType() {
        return type;
    }

    @Override
    public void setPlantType(PlantType type) {
        this.type = type;
    }
}
