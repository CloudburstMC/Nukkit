package cn.nukkit.block;

import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.RED_FLOWER;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDandelion extends BlockFlower {
    public BlockDandelion(Identifier id) {
        super(id);
    }

    @Override
    protected Block getUncommonFlower() {
        return Block.get(RED_FLOWER);
    }
}
