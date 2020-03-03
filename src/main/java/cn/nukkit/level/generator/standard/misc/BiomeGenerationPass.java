package cn.nukkit.level.generator.standard.misc;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.gen.decorator.Decorator;
import cn.nukkit.level.generator.standard.gen.replacer.BlockReplacer;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.daporkchop.lib.random.PRandom;

/**
 * Dummy generation pass to indicate where biome-specific generation passes should be inserted.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize
public final class BiomeGenerationPass implements BlockReplacer, Decorator, Populator {
    public static final Identifier          ID       = Identifier.fromString("nukkitx:biome");
    public static final BiomeGenerationPass INSTANCE = new BiomeGenerationPass();

    @Override
    public void decorate(IChunk chunk, PRandom random, int x, int z) {
        throw new UnsupportedOperationException("nukkitx:biome may only be used in the base preset!");
    }

    @Override
    public Block replace(Block prev, int x, int y, int z, double gradX, double gradY, double gradZ, double density) {
        throw new UnsupportedOperationException("nukkitx:biome may only be used in the base preset!");
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        throw new UnsupportedOperationException("nukkitx:biome may only be used in the base preset!");
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
