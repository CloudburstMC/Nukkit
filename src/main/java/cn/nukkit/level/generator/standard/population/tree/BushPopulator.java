package cn.nukkit.level.generator.standard.population.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

import static java.lang.Math.*;

/**
 * Places very short "trees", consisting of only a single log with a pile of leaves around it.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class BushPopulator extends AbstractTreePopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:bush");

    @JsonProperty
    protected BlockSelector log;

    @JsonProperty
    protected BlockSelector leaves;

    @JsonProperty
    protected int size = 2;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.log, "log must be set!");
        Objects.requireNonNull(this.leaves, "leaves must be set!");
        Preconditions.checkState(this.size > 0, "size must be at least 1!");
    }

    @Override
    protected void placeTree(PRandom random, ChunkManager level, int x, int y, int z) {
        level.setBlockRuntimeIdUnsafe(x, ++y, z, 0, this.log.selectRuntimeId(random));

        final int leaves = this.leaves.selectRuntimeId(random);
        final int size = this.size;

        for (int dy = 0; dy <= size; dy++) {
            int radius = size - dy;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if ((abs(dx) != radius || abs(dz) != radius || random.nextBoolean())
                            && this.replace.test(level.getBlockRuntimeIdUnsafe(x + dx, y + dy, z + dz, 0))) {
                        level.setBlockRuntimeIdUnsafe(x + dx, y + dy, z + dz, 0, leaves);
                    }
                }
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
