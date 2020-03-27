package cn.nukkit.entity.misc;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;

public interface FallingBlock extends Entity {

    Block getBlock();

    void setBlock(Block block);
}
