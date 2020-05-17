package cn.nukkit.level.generator.standard.population.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.feature.WorldFeature;
import cn.nukkit.level.feature.tree.TreeSpecies;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import java.util.Arrays;
import java.util.Objects;

import static java.lang.Math.*;
import static net.daporkchop.lib.random.impl.FastPRandom.*;

/**
 * A populator that places simple trees, with a similar shape to vanilla oak/birch trees.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class HugeTreePopulator extends AbstractTreePopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:huge_tree");

    @JsonProperty
    protected BlockSelector below;

    protected WorldFeature[] types;

    @JsonProperty
    protected boolean grid;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.types, "type must be set!");
        Objects.requireNonNull(this.below, "below must be set!");
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int blockX, int blockZ) {
        if (this.grid) {
            if ((mix32(mix64(this.seed() + (blockX >> 2)) + (blockZ >> 2)) & 0xF) != (((blockX & 3) << 2) | (blockZ & 3))) {
                //bitwise magic!
                //splits the world into 4x4 tiles with a single tree in each one
                return;
            }

            final BlockFilter replace = this.replace;
            final BlockFilter on = this.on;

            final int max = min(this.height.max - 1, 254);
            final int min = this.height.min;

            IChunk chunk = level.getChunk(blockX >> 4, blockZ >> 4);
            for (int y = max, id, lastId = chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, y + 1, blockZ & 0xF, 0); y >= min; y--) {
                id = chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, y, blockZ & 0xF, 0);

                if (replace.test(lastId) && on.test(id)) {
                    this.placeTree(random, level, blockX, y, blockZ);
                }

                lastId = id;
            }
        } else {
            super.populate(random, level, blockX, blockZ);
        }
    }

    @Override
    protected void placeTree(PRandom random, ChunkManager level, int x, int y, int z) {
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                int testId = level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0);
                if (!this.on.test(testId) && (!this.replace.test(testId) || !this.on.test(level.getBlockRuntimeIdUnsafe(x + dx, y - 1, z + dz, 0)))) {
                    return;
                }
            }
        }

        if (this.types[random.nextInt(this.types.length)].place(level, random, x, y + 1, z)) {
            int belowId = this.below.selectRuntimeId(random);
            for (int dx = 0; dx <= 1; dx++) {
                for (int dz = 0; dz <= 1; dz++) {
                    level.setBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0, belowId);
                    level.setBlockRuntimeIdUnsafe(x + dx, y - 1, z + dz, 0, belowId);
                }
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @JsonSetter("type")
    private void setType(@NonNull ConfigTree type) {
        this.types = new WorldFeature[]{type.build()};
    }

    @JsonSetter("types")
    private void setTypes(@NonNull ConfigTree[] types) {
        Preconditions.checkArgument(types.length > 0, "types may not be empty!");
        this.types = Arrays.stream(types)
                .map(ConfigTree::build)
                .toArray(WorldFeature[]::new);
    }

    @JsonDeserialize
    private static final class ConfigTree {
        private WorldFeature feature;

        @JsonCreator
        public ConfigTree(String species) {
            this.feature = Preconditions.checkNotNull(TreeSpecies.valueOf(species.toUpperCase()).getHugeGenerator(), "%s does not support huge trees!", species);
        }

        public WorldFeature build() {
            return this.feature;
        }
    }
}
