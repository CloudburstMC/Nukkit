package cn.nukkit.level.generator.standard.pop;

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

/**
 * A populator that places simple trees, with a similar shape to vanilla oak/birch trees.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class HugeTreePopulator extends ChancePopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:huge_tree");

    @JsonProperty
    protected BlockFilter replace = BlockFilter.REPLACEABLE;

    @JsonProperty
    protected BlockFilter on;

    @JsonProperty
    protected BlockSelector below;

    protected WorldFeature[] types;

    @JsonProperty
    protected boolean grid;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.replace, "replace must be set!");
        Objects.requireNonNull(this.on, "on must be set!");
        Objects.requireNonNull(this.types, "type must be set!");
        Objects.requireNonNull(this.below, "below must be set!");

        super.init(levelSeed, localSeed, generator);
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        final BlockFilter replace = this.replace;
        final BlockFilter on = this.on;

        if (this.grid) {
            for (int gridX = 0; gridX < 4; gridX++) {
                for (int gridZ = 0; gridZ < 4; gridZ++) {
                    int x = (chunkX << 4) + (gridX << 2) + random.nextInt(1, 4);
                    int z = (chunkZ << 4) + (gridZ << 2) + random.nextInt(1, 4);

                    IChunk chunk = level.getChunk(x >> 4, z >> 4);
                    for (int y = 254, id, lastId = chunk.getBlockRuntimeIdUnsafe(x & 0xF, 255, z & 0xF, 0); y >= 0; y--) {
                        id = chunk.getBlockRuntimeIdUnsafe(x & 0xF, y, z & 0xF, 0);

                        if (replace.test(lastId) && on.test(id)) {
                            this.tryPlaceTree(random, level, x, y, z);
                        }

                        lastId = id;
                    }
                }
            }
        } else {
            IChunk chunk = level.getChunk(chunkX, chunkZ);
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 254, id, lastId = chunk.getBlockRuntimeIdUnsafe(x, 255, z, 0); y >= 0; y--) {
                        id = chunk.getBlockRuntimeIdUnsafe(x, y, z, 0);

                        if (replace.test(lastId) && on.test(id)) {
                            this.tryPlaceTree(random, level, (chunkX << 4) | x, y, (chunkZ << 4) | z);
                        }

                        lastId = id;
                    }
                }
            }
        }
    }

    protected void tryPlaceTree(PRandom random, ChunkManager level, int x, int y, int z) {
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                int testId = level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0);
                if (!this.on.test(testId) && (!this.replace.test(testId) || !this.on.test(level.getBlockRuntimeIdUnsafe(x + dx, y - 1, z + dz, 0)))) {
                    return;
                }
            }
        }

        if (random.nextDouble() < this.chance && this.types[random.nextInt(this.types.length)].place(level, random, x, y + 1, z)) {
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
