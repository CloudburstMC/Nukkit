package cn.nukkit.blockentity;

import cn.nukkit.block.Block;

public interface FlowerPot extends BlockEntity {

    Block getPlant();

    void setPlant(Block block);
}
