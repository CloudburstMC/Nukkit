package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.Chunk;
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
    private BlockFilter replace = BlockFilter.REPLACEABLE;

    @JsonProperty
    private BlockFilter on;

    @JsonProperty
    private BlockSelector below;

    private WorldFeature[] types;

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

        IChunk chunk = level.getChunk(chunkX, chunkZ);
        for (int x = 0; x < 16; x++)    {
            for (int z = 0; z < 16; z++)    {
                for (int y = 254, id, lastId = chunk.getBlockRuntimeIdUnsafe(x, 255, z, 0); y >= 0; y--) {
                    id = chunk.getBlockRuntimeIdUnsafe(x, y, z, 0);

                    PLACE:
                    if (replace.test(lastId) && on.test(id)) {
                        int xx = (chunkX << 4) | x;
                        int zz = (chunkZ << 4) | z;
                        for (int dx = 0; dx <= 1; dx++) {
                            for (int dz = 0; dz <= 1; dz++) {
                                int testId = level.getBlockRuntimeIdUnsafe(xx + dx, y, zz + dz, 0);
                                if (!on.test(testId) && (!replace.test(testId) || !on.test(level.getBlockRuntimeIdUnsafe(xx + dx, y - 1, zz + dz, 0))))    {
                                    break PLACE;
                                }
                            }
                        }

                        if (random.nextDouble() < this.chance && this.types[random.nextInt(this.types.length)].place(level, random, xx, y + 1, zz)) {
                            int belowId = this.below.selectRuntimeId(random);
                            for (int dx = 0; dx <= 1; dx++) {
                                for (int dz = 0; dz <= 1; dz++) {
                                    level.setBlockRuntimeIdUnsafe(xx + dx, y, zz + dz, 0, belowId);
                                    level.setBlockRuntimeIdUnsafe(xx + dx, y - 1, zz + dz, 0, belowId);
                                }
                            }
                        }
                    }

                    lastId = id;
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
