package cn.nukkit.blockentity;

import cn.nukkit.block.Block;

public interface MovingBlock extends BlockEntity {

    Block getMovingBlock();

    void setMovingBlock(Block block);
}
