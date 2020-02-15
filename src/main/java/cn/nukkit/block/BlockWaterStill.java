package cn.nukkit.block;

import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.WATER;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockWaterStill extends BlockWater {

    protected BlockWaterStill(Identifier id, Identifier flowingId, Identifier stationaryId) {
        super(id, flowingId, stationaryId);
    }

    protected BlockWaterStill(Identifier flowingId, Identifier stationaryId) {
        this(stationaryId, flowingId, stationaryId);
    }

    @Override
    public Block getBlock(int meta) {
        return Block.get(WATER, meta);
    }

    public static BlockFactory factory(Identifier flowingId) {
        return id-> new BlockWaterStill(flowingId, id);
    }
}
