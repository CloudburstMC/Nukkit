package cn.nukkit.level.generator.populator.type;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.math.BedrockRandom;

/**
 * @author DaPorkchop_
 *
 * A populator that populates a single block type.
 */
public abstract class PopulatorSurfaceBlock extends PopulatorCount {
    @Override
    protected void populateCount(ChunkManager level, int chunkX, int chunkZ, BedrockRandom random, IChunk chunk) {
        int x = random.nextInt(16);
        int z = random.nextInt(16);
        int y = getHighestWorkableBlock(level, x, z, chunk);
        if (y > 0 && canStay(x, y, z, chunk, level)) {
            placeBlock(x, y, z, getBlock(x, z, random, chunk), chunk, random);
        }
    }

    protected abstract boolean canStay(int x, int y, int z, IChunk chunk, ChunkManager level);

    protected abstract Block getBlock(int x, int z, BedrockRandom random, IChunk chunk);

    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, IChunk chunk) {
        int y;
        //start at 254 because we add one afterwards
        for (y = 254; y >= 0; --y) {
            if (!PopulatorHelpers.isNonSolid(chunk.getBlockId(x, y, z))) {
                break;
            }
        }

        return y == 0 ? -1 : ++y;
    }

    protected void placeBlock(int x, int y, int z, Block block, IChunk chunk, BedrockRandom random) {
        chunk.setBlock(x, y, z, block);
    }
}
