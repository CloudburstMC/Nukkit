package cn.nukkit.api.metadata.blockentity;

import cn.nukkit.api.metadata.data.PlantType;

public interface FlowerPotBlockEntity extends BlockEntity {

    PlantType getPlantType();

    void setPlantType(PlantType type);
}
