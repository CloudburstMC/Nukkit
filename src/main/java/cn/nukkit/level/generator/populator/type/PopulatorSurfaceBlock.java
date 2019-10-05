package cn.nukkit.level.generator.populator.type;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 *
 * A populator that populates a single block type.
 */
public abstract class PopulatorSurfaceBlock extends PopulatorCount {
    @Override
    protected void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, Chunk chunk) {
        int x = random.nextBoundedInt(16);
        int z = random.nextBoundedInt(16);
        int y = getHighestWorkableBlock(level, x, z, chunk);
        if (y > 0 && canStay(x, y, z, chunk, level)) {
            placeBlock(x, y, z, getBlockId(x, z, random, chunk), chunk, random);
        }
    }

    protected abstract boolean canStay(int x, int y, int z, Chunk chunk, ChunkManager level);

    protected abstract int getBlockId(int x, int z, NukkitRandom random, Chunk chunk);

    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, Chunk chunk) {
        int y;
        //start at 254 because we add one afterwards
        for (y = 254; y >= 0; --y) {
            if (!PopulatorHelpers.isNonSolid(chunk.getBlockId(x, y, z))) {
                break;
            }
        }

        return y == 0 ? -1 : ++y;
    }

    protected void placeBlock(int x, int y, int z, int id, Chunk chunk, NukkitRandom random) {
        chunk.setFullBlock(x, y, z, id);
    }
}
