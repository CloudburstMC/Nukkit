package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.object.tree.ObjectTree;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.Identifier;
import lombok.extern.log4j.Log4j2;

import static cn.nukkit.block.BlockIds.*;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
@Log4j2
public class PopulatorTree extends PopulatorCount {

    private final int type;
    private ChunkManager level;

    public PopulatorTree() {
        this(BlockSapling.OAK);
    }

    public PopulatorTree(int type) {
        this.type = type;
    }

    @Override
    public void populateCount(ChunkManager level, final int chunkX, final int chunkZ, NukkitRandom random, IChunk chunk) {
        this.level = level;

        int cX = chunkX << 4;
        int cZ = chunkZ << 4;
        int x = NukkitMath.randomRange(random, cX, cX + 15);
        int z = NukkitMath.randomRange(random, cZ, cZ + 15);
        level.getChunk(chunkX, chunkZ);
        int y;
        try {
            y = this.getHighestWorkableBlock(x, z);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Chunk (%d, %d) > %d, %d", chunkX, chunkZ, x >> 4, z >> 4), e);
        }
        if (y < 3) {
            return;
        }
        ObjectTree.growTree(this.level, x, y, z, random, this.type);
    }

    private int getHighestWorkableBlock(int x, int z) {
        int y;
        for (y = 254; y > 0; --y) {
            Identifier b = this.level.getBlockIdAt(x, y, z);
            if (b == DIRT || b == GRASS) {
                break;
            } else if (b != AIR && b != SNOW_LAYER) {
                return -1;
            }
        }

        return ++y;
    }
}
