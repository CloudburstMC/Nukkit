package com.nukkitx.api.metadata.blockentity;

import com.nukkitx.api.metadata.data.PlantType;

public interface FlowerPotBlockEntity extends BlockEntity {

    PlantType getPlantType();

    void setPlantType(PlantType type);
}
