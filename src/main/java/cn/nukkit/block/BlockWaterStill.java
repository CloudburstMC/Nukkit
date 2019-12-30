package cn.nukkit.block;

import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.WATER;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockWaterStill extends BlockWater {

    public BlockWaterStill(Identifier id) {
        super(id);
    }

    @Override
    public Block getBlock(int meta) {
        return Block.get(WATER, meta);
    }

}
