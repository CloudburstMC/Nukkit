package cn.nukkit.level.biome.type;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public abstract class WateryBiome extends CoveredBiome {
    private static final Block AIR = Block.get(BlockID.AIR);
    private static final Block DIRT = Block.get(BlockID.DIRT);

    @Override
    public int getSurfaceDepth(int x, int y, int z) {
        return 0;
    }

    @Override
    public Block getSurface(int x, int y, int z) {
        //doesn't matter, surface depth is 0
        return AIR;
    }

    @Override
    public int getGroundDepth(int x, int y, int z) {
        return 5;
    }

    @Override
    public Block getGround(int x, int y, int z) {
        return DIRT;
    }
}
