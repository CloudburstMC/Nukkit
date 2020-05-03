package cn.nukkit.level.generator.standard.population;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.level.generator.standard.population.cluster.AbstractReplacingPopulator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class SpringPopulator extends AbstractReplacingPopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:spring");

    @JsonProperty
    protected IntRange height = IntRange.WHOLE_WORLD;

    @JsonProperty
    protected BlockFilter neighbor = BlockFilter.AIR;

    @JsonProperty
    protected IntRange neighborCount;

    @JsonProperty
    protected IntRange airCount;

    @JsonProperty
    protected BlockSelector block;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.height, "height must be set!");
        Objects.requireNonNull(this.neighbor, "neighbor must be set!");
        Objects.requireNonNull(this.neighborCount, "neighborCount must be set!");
        Objects.requireNonNull(this.airCount, "airCount must be set!");
        Objects.requireNonNull(this.block, "block must be set!");
    }

    @Override
    protected void populate0(PRandom random, ChunkManager level, int blockX, int blockZ) {
        int blockY = this.height.rand(random);

        if (blockY <= 0 || !this.replace.test(level.getBlockRuntimeIdUnsafe(blockX, blockY, blockZ, 0))) {
            return;
        }

        final BlockFilter neighbor = this.neighbor;

        int neighbors = 0;
        int air = 0;

        int id = level.getBlockRuntimeIdUnsafe(blockX, blockY - 1, blockZ, 0);
        if (neighbor.test(id)) {
            neighbors++;
        } else if (id == 0) {
            air++;
        }
        id = level.getBlockRuntimeIdUnsafe(blockX - 1, blockY, blockZ, 0);
        if (neighbor.test(id)) {
            neighbors++;
        } else if (id == 0) {
            air++;
        }
        id = level.getBlockRuntimeIdUnsafe(blockX + 1, blockY, blockZ, 0);
        if (neighbor.test(id)) {
            neighbors++;
        } else if (id == 0) {
            air++;
        }
        id = level.getBlockRuntimeIdUnsafe(blockX, blockY, blockZ - 1, 0);
        if (neighbor.test(id)) {
            neighbors++;
        } else if (id == 0) {
            air++;
        }
        id = level.getBlockRuntimeIdUnsafe(blockX, blockY, blockZ + 1, 0);
        if (neighbor.test(id)) {
            neighbors++;
        } else if (id == 0) {
            air++;
        }

        if (this.neighborCount.contains(neighbors) && this.airCount.contains(air)) {
            level.setBlockRuntimeIdUnsafe(blockX, blockY, blockZ, 0, this.block.selectRuntimeId(random));
            //TODO: request immediate block update
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
