package cn.nukkit.level.generator.standard.pop.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.pop.ChancePopulator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

import static java.lang.Math.*;

/**
 * Base class for all tree populators.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractTreePopulator extends ChancePopulator {
    @JsonProperty
    protected BlockFilter replace = BlockFilter.REPLACEABLE;

    @JsonProperty
    protected BlockFilter on;

    @JsonProperty
    protected IntRange height = IntRange.WHOLE_WORLD;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.replace, "replace must be set!");
        Objects.requireNonNull(this.on, "on must be set!");
        Objects.requireNonNull(this.height, "height must be set!");

        super.init(levelSeed, localSeed, generator);
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        final BlockFilter replace = this.replace;
        final BlockFilter on = this.on;

        final int max = min(this.height.max - 1, 254);
        final int min = this.height.min;

        IChunk chunk = level.getChunk(chunkX, chunkZ);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = max, id, lastId = chunk.getBlockRuntimeIdUnsafe(x, y + 1, z, 0); y >= min; y--) {
                    id = chunk.getBlockRuntimeIdUnsafe(x, y, z, 0);

                    if (replace.test(lastId) && on.test(id) && random.nextDouble() < this.chance) {
                        this.tryPlaceTree(random, level, (chunkX << 4) | x, y, (chunkZ << 4) | z);
                    }

                    lastId = id;
                }
            }
        }
    }

    protected abstract void tryPlaceTree(PRandom random, ChunkManager level, int x, int y, int z);
}
