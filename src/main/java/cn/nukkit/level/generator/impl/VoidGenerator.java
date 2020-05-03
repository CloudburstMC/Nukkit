package cn.nukkit.level.generator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.utils.Identifier;
import net.daporkchop.lib.random.PRandom;

/**
 * A basic generator that does nothing at all, resulting in a world of nothing but air.
 *
 * @author DaPorkchop_
 */
public final class VoidGenerator implements Generator {
    public static final Identifier ID = Identifier.from("minecraft", "void");

    public VoidGenerator(long seed, String options) {
        //no-op
    }

    @Override
    public void generate(PRandom random, IChunk chunk, int chunkX, int chunkZ) {
        //no-op
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        //no-op
    }

    @Override
    public void finish(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        //no-op
    }
}
