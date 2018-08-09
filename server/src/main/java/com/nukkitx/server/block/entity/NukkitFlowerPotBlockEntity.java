package com.nukkitx.server.block.entity;

import com.nukkitx.api.metadata.blockentity.FlowerPotBlockEntity;
import com.nukkitx.api.metadata.data.PlantType;

public class NukkitFlowerPotBlockEntity extends NukkitBlockEntity implements FlowerPotBlockEntity {

    private PlantType type;

    public NukkitFlowerPotBlockEntity(PlantType type) {
        super(BlockEntityType.FLOWER_POT);
        this.type = type;
    }

    @Override
    public PlantType getPlantType() {
        return type;
    }

    @Override
    public void setPlantType(PlantType type) {
        this.type = type;
    }
}
