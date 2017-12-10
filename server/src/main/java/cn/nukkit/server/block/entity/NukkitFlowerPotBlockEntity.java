package cn.nukkit.server.block.entity;

import cn.nukkit.api.block.entity.FlowerPotBlockEntity;
import cn.nukkit.api.util.data.FlowerType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NukkitFlowerPotBlockEntity implements FlowerPotBlockEntity{
    private final FlowerType type;

    @Override
    public FlowerType getFlowerType() {
        return type;
    }
}
