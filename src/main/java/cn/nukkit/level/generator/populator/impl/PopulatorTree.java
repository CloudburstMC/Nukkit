package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.tree.ObjectTree;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PopulatorTree extends PopulatorCount {

    private final int type;

    private ChunkManager level;

    public PopulatorTree() {
        this(BlockSapling.OAK);
    }

    public PopulatorTree(final int type) {
        this.type = type;
    }

    @Override
    public void populateCount(final ChunkManager level, final int chunkX, final int chunkZ, final NukkitRandom random, final FullChunk chunk) {
        this.level = level;
        final int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
        final int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
        final int y = this.getHighestWorkableBlock(x, z);
        if (y < 3) {
            return;
        }
        ObjectTree.growTree(this.level, x, y, z, random, this.type);
    }

    private int getHighestWorkableBlock(final int x, final int z) {
        int y;
        for (y = 254; y > 0; --y) {
            final int b = this.level.getBlockIdAt(x, y, z);
            if (b == BlockID.DIRT || b == BlockID.GRASS) {
                break;
            } else if (b != BlockID.AIR && b != BlockID.SNOW_LAYER) {
                return -1;
            }
        }

        return ++y;
    }

}
