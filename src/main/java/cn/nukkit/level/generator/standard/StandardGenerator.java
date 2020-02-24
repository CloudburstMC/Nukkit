package cn.nukkit.level.generator.standard;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.utils.Identifier;
import net.daporkchop.lib.random.PRandom;

/**
 * Main class of the NukkitX Standard Generator.
 *
 * @author DaPorkchop_
 */
public final class StandardGenerator implements Generator {
    public static final Identifier ID = Identifier.from("nukkitx", "standard");

    public StandardGenerator(long seed, String options) {
    }

    @Override
    public void generate(PRandom random, IChunk chunk, int chunkX, int chunkZ) {
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
    }
}
