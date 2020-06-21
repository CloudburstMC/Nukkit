package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.mushroom.BigMushroom;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

/**
 * @author DaPorkchop_
 */
public class MushroomPopulator extends PopulatorCount {

    private final int type;

    public MushroomPopulator() {
        this(-1);
    }

    public MushroomPopulator(final int type) {
        this.type = type;
    }

    @Override
    public void populateCount(final ChunkManager level, final int chunkX, final int chunkZ, final NukkitRandom random, final FullChunk chunk) {
        final int x = chunkX << 4 | random.nextBoundedInt(16);
        final int z = chunkZ << 4 | random.nextBoundedInt(16);
        final int y = this.getHighestWorkableBlock(level, x, z, chunk);
        if (y != -1) {
            new BigMushroom(this.type).generate(level, random, new Vector3(x, y, z));
        }
    }

    @Override
    protected int getHighestWorkableBlock(final ChunkManager level, int x, int z, final FullChunk chunk) {
        int y;
        x &= 0xF;
        z &= 0xF;
        for (y = 254; y > 0; --y) {
            final int b = chunk.getBlockId(x, y, z);
            if (b == BlockID.DIRT || b == BlockID.GRASS) {
                break;
            } else if (b != BlockID.AIR && b != BlockID.SNOW_LAYER) {
                return -1;
            }
        }

        return ++y;
    }

}