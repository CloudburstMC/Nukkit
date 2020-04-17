package cn.nukkit.level.generator.standard.population.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.population.ChancePopulator;
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
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.replace, "replace must be set!");
        Objects.requireNonNull(this.on, "on must be set!");
        Objects.requireNonNull(this.height, "height must be set!");
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int blockX, int blockZ) {
        final BlockFilter replace = this.replace;
        final BlockFilter on = this.on;

        final int max = min(this.height.max - 1, 254);
        final int min = this.height.min;

        IChunk chunk = level.getChunk(blockX >> 4, blockZ >> 4);
        for (int y = max, id, lastId = chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, y + 1, blockZ & 0xF, 0); y >= min; y--) {
            id = chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, y, blockZ & 0xF, 0);

            if (replace.test(lastId) && on.test(id) && random.nextDouble() < this.chance) {
                this.placeTree(random, level, blockX, y, blockZ);
            }

            lastId = id;
        }
    }

    protected abstract void placeTree(PRandom random, ChunkManager level, int x, int y, int z);
}
