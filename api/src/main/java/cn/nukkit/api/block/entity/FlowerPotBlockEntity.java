package cn.nukkit.api.block.entity;

import cn.nukkit.api.metadata.data.FlowerType;

public interface FlowerPotBlockEntity extends BlockEntity {

    FlowerType getFlowerType();

    void setFlowerType(FlowerType flowerType);
}
