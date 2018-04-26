package cn.nukkit.server.block.entity;

import cn.nukkit.api.metadata.blockentity.FlowerPotBlockEntity;
import cn.nukkit.api.metadata.data.FlowerType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NukkitFlowerPotBlockEntity implements FlowerPotBlockEntity{
    private FlowerType type;

    @Override
    public FlowerType getFlowerType() {
        return type;
    }

    @Override
    public void setFlowerType(FlowerType type) {
        this.type = type;
    }
}
