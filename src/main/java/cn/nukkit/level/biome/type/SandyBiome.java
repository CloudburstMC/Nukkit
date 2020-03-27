package cn.nukkit.level.biome.type;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SandyBiome extends CoveredBiome {
    private static final Block SAND = Block.get(BlockIds.SAND);
    private static final Block SANDSTONE = Block.get(BlockIds.SANDSTONE);

    @Override
    public int getSurfaceDepth(int x, int y, int z) {
        return 3;
    }

    @Override
    public Block getSurface(int x, int y, int z) {
        return SAND;
    }

    @Override
    public int getGroundDepth(int x, int y, int z) {
        return 2;
    }

    @Override
    public Block getGround(int x, int y, int z) {
        return SANDSTONE;
    }
}
