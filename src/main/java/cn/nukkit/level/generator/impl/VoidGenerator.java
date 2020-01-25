package cn.nukkit.level.generator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.ChunkPos;
import cn.nukkit.utils.Identifier;

import java.util.Collections;
import java.util.Random;

/**
 * A basic generator that does nothing at all, resulting in a world of nothing but air.
 *
 * @author DaPorkchop_
 */
public final class VoidGenerator implements Generator {
    public static final Identifier ID = Identifier.from("nukkitx", "void");

    public VoidGenerator(long seed, String options) {
        options.hashCode();
        //no-op
    }

    @Override
    public void generate(Random random, IChunk chunk, int chunkX, int chunkZ) {
        //no-op
    }

    @Override
    public void populate(Random random, IChunk chunk, ChunkManager level) {
        //no-op
    }

    @Override
    public Iterable<ChunkPos> populationChunks(IChunk chunk) {
        return Collections.emptyList();
    }
}
