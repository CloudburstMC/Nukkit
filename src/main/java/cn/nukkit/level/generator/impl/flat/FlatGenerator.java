package cn.nukkit.level.generator.impl.flat;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.ChunkPrimer;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.ChunkPos;
import cn.nukkit.registry.BlockRegistry;

import java.util.Collections;
import java.util.Random;

import static cn.nukkit.block.BlockIds.*;

/**
 * A basic generator for superflat worlds.
 *
 * @author DaPorkchop_
 */
public final class FlatGenerator implements Generator {
    @Override
    public void generate(Random random, ChunkPrimer chunk, int chunkX, int chunkZ) {
        final int bedrockId = BlockRegistry.get().getRuntimeId(BEDROCK, 0);
        final int dirtId = BlockRegistry.get().getRuntimeId(DIRT, 0);
        final int grassId = BlockRegistry.get().getRuntimeId(GRASS, 0);

        for (int x = 15; x >= 0; x--)   {
            for (int z = 15; z >= 0; z--)   {
                chunk.setBlockRuntimeIdUnsafe(x, 0, z, bedrockId);
                for (int y = 1; y < 4; y++) {
                    chunk.setBlockRuntimeIdUnsafe(x, y, z, dirtId);
                }
                chunk.setBlockRuntimeIdUnsafe(x, 4, z, grassId);
            }
        }
    }

    @Override
    public void populate(Random random, IChunk chunk, ChunkManager level) {
        //do nothing lol
    }

    @Override
    public Iterable<ChunkPos> populationChunks(IChunk chunk) {
        return Collections.emptyList();
    }
}
