package cn.nukkit.level.generator.standard.pop.cluster;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.pop.ChancePopulator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

import static java.lang.Math.*;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public abstract class AbstractClusterPopulator extends ChancePopulator {
    @JsonProperty
    protected BlockFilter replace;

    @JsonProperty
    protected IntRange height;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.replace, "replace must be set!");
        Objects.requireNonNull(this.height, "height must be set!");
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int blockX, int blockZ) {
        final int height = level.getChunk(blockX >> 4, blockZ >> 4).getHighestBlock(blockX & 0xF, blockZ & 0xF);

        final double chance = this.chance;

        for (int y = max(this.height.min, 0), max = min(this.height.max, height << 1); y < max; y++)    {
            if (random.nextDouble() >= chance)  {
                this.placeCluster(random, level, blockX, y, blockZ);
            }
        }
    }

    protected abstract void placeCluster(PRandom random, ChunkManager level, int x, int y, int z);
}
