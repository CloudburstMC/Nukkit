package cn.nukkit.level.generator.impl.flat;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.ChunkPos;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.google.common.collect.ImmutableList;
import net.daporkchop.lib.random.PRandom;

import java.util.Collection;
import java.util.Collections;

import static cn.nukkit.block.BlockIds.*;

/**
 * A basic generator for superflat worlds.
 *
 * @author DaPorkchop_
 */
public final class FlatGenerator implements Generator {
    public static final Identifier ID = Identifier.from("nukkitx", "flat");

    public FlatGenerator(long seed, String options) {
        //porktodo: parse generator options
    }

    @Override
    public void generate(PRandom random, IChunk chunk, int chunkX, int chunkZ) {
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

        chunk.setBlockRuntimeIdUnsafe(0, 5, 0, bedrockId);
        chunk.setBlockRuntimeIdUnsafe(15, 5, 0, bedrockId);
        chunk.setBlockRuntimeIdUnsafe(0, 5, 15, bedrockId);
        chunk.setBlockRuntimeIdUnsafe(15, 5, 15, bedrockId);
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        //debug stuff
        if (true)   {
            int blockX = chunkX << 4;
            int blockZ = chunkZ << 4;

            for (int x = 16 - 3; x <= 16 + 3; x++)  {
                level.setBlockIdAt(blockX + x, 5, blockZ + 16 - 3, BRICK_BLOCK);
            }
            for (int z = 16 - 3; z <= 16 + 3; z++)  {
                level.setBlockIdAt(blockX + 16 - 3, 5, blockZ + z, BRICK_BLOCK);
            }

            level.setBlockIdAt(blockX + 15, 6, blockZ + 15, GLOWSTONE);
            level.setBlockIdAt(blockX + 16, 6, blockZ + 15, GLOWSTONE);
            level.setBlockIdAt(blockX + 15, 6, blockZ + 16, GLOWSTONE);
            level.setBlockIdAt(blockX + 16, 6, blockZ + 16, GLOWSTONE);
        }

        //no-op
    }

    @Override
    public Collection<ChunkPos> populationChunks(ChunkPos pos, int chunkX, int chunkZ) {
        //debug stuff
        if (true)   {
            return ImmutableList.of(pos.add(1, 0), pos.add(0, 1), pos.add(1, 1));
            //return ImmutableList.of(pos.add(1, 0), pos.add(0, 1), pos.sub(1, 0), pos.sub(0, 1));
        }

        return Collections.emptyList();
    }
}
