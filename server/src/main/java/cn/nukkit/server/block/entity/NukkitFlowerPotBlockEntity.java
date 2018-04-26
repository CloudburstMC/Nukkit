package cn.nukkit.server.block.entity;

import cn.nukkit.api.metadata.blockentity.FlowerPotBlockEntity;
import cn.nukkit.api.metadata.data.PlantType;
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
