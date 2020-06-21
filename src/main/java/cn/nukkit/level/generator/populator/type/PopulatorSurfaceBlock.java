package cn.nukkit.level.generator.populator.type;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 * <p>
 * A populator that populates a single block type.
 */
public abstract class PopulatorSurfaceBlock extends PopulatorCount {

    @Override
    protected void populateCount(final ChunkManager level, final int chunkX, final int chunkZ, final NukkitRandom random, final FullChunk chunk) {
        final int x = random.nextBoundedInt(16);
        final int z = random.nextBoundedInt(16);
        final int y = this.getHighestWorkableBlock(level, x, z, chunk);
        if (y > 0 && this.canStay(x, y, z, chunk)) {
            this.placeBlock(x, y, z, this.getBlockId(x, z, random, chunk), chunk, random);
        }
    }

    protected abstract boolean canStay(int x, int y, int z, FullChunk chunk);

    protected abstract int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk);

    @Override
    protected int getHighestWorkableBlock(final ChunkManager level, final int x, final int z, final FullChunk chunk) {
        int y;
        //start at 254 because we add one afterwards
        for (y = 254; y >= 0; --y) {
            if (!PopulatorHelpers.isNonSolid(chunk.getBlockId(x, y, z))) {
                break;
            }
        }

        return y == 0 ? -1 : ++y;
    }

    protected void placeBlock(final int x, final int y, final int z, final int id, final FullChunk chunk, final NukkitRandom random) {
        chunk.setFullBlockId(x, y, z, id);
    }

}
