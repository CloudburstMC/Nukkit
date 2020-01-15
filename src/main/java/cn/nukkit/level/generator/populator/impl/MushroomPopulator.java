package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.object.mushroom.BigMushroom;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.Vector3i;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.*;

/**
 * @author DaPorkchop_
 */
public class MushroomPopulator extends PopulatorCount {
    private final int type;

    public MushroomPopulator() {
        this(-1);
    }

    public MushroomPopulator(int type) {
        this.type = type;
    }

    @Override
    public void populateCount(ChunkManager level, int chunkX, int chunkZ, BedrockRandom random, IChunk chunk) {
        int x = (chunkX << 4) | random.nextInt(16);
        int z = (chunkZ << 4) | random.nextInt(16);
        int y = this.getHighestWorkableBlock(level, x, z, chunk);
        if (y != -1) {
            new BigMushroom(type).generate(level, random, new Vector3i(x, y, z));
        }
    }

    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, IChunk chunk) {
        int y;
        x &= 0xF;
        z &= 0xF;
        for (y = 254; y > 0; --y) {
            Identifier b = chunk.getBlockId(x, y, z);
            if (b == DIRT || b == GRASS) {
                break;
            } else if (b != AIR && b != SNOW_LAYER) {
                return -1;
            }
        }

        return ++y;
    }
}