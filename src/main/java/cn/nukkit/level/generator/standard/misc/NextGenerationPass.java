package cn.nukkit.level.generator.standard.misc;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.gen.decorator.Decorator;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.daporkchop.lib.random.PRandom;

/**
 * Dummy generation pass to indicate where generation passes from the next layer down should be inserted.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize
public final class NextGenerationPass implements Decorator, Populator {
    public static final Identifier         ID       = Identifier.fromString("nukkitx:next");
    public static final NextGenerationPass INSTANCE = new NextGenerationPass();

    @Override
    public void decorate(IChunk chunk, PRandom random, int x, int z) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ, int blockX, int blockZ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
